package com.diplom.work.controller.api;


import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Client;
import com.diplom.work.core.Rule;
import com.diplom.work.core.Settings;
import com.diplom.work.core.json.NumberInfo;
import com.diplom.work.core.json.NumberInfoAnswer;
import com.diplom.work.exceptions.NotFoundRequestNumberException;
import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.exceptions.SettingsNotFound;
import com.diplom.work.exceptions.SignsNotEquals;
import com.diplom.work.svc.ClientService;
import com.diplom.work.svc.LogService;
import com.diplom.work.svc.RuleService;
import com.diplom.work.svc.SettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.thymeleaf.util.StringUtils.isEmptyOrWhitespace;

@RestController
@RequestMapping("api/")
public class NumberInfoController {
    private final RuleService ruleService;
    private final SettingsService settingsService;
    private final ClientService clientService;
    private final LogService logService;

    @Autowired
    public NumberInfoController(RuleService ruleService, SettingsService settingsService, ClientService clientService, LogService logService) {
        this.ruleService = ruleService;
        this.settingsService = settingsService;
        this.clientService = clientService;
        this.logService = logService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NumberInfoController.class);

    /**
     * Обработка запроса по get_number_info от ВАТС
     *
     * @param clientID   - идентификатор клиента, должен совпадать с нашим ClientID в настройках @see Settings
     * @param clientSign - подпись запроса = sha256hex(clientid+body+clientkey). Можно проверить, и если не совпадает, игнорить. А можно ничего не делать)
     * @param body       - запрос, включающий инфу о том, кто и куда звонит
     * @return куда переадресовать звонок или вернуть ошибку
     * @see NumberInfo
     */
    @PostMapping(path = "get_number_info",
            consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    //TODO required убрать после отладки
    public ResponseEntity<NumberInfoAnswer> getNewCall(@RequestHeader(name = "X-Client-ID", required = false) String clientID,
                                                       @RequestHeader(name = "X-Client-Sign", required = false) String clientSign,
                                                       @RequestBody String body) throws JsonProcessingException {
        NumberInfo numberInfo = new ObjectMapper().readValue(body, NumberInfo.class);
        // Получаем настройки
        Settings settings;
        try {
            settings = settingsService.getSettings();
        } catch (SettingsNotFound e) {
            // Настроек нет
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(String.format("ТЕКСТ ОШИБКИ: %s Получили запрос на get_number_info, header.X-Client-ID = %s \n" +
                    "header.X-Client-Sign = %s \n, body = %s", e.getMessage(), clientID, clientSign, numberInfo.toString()));
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, e.getMessage()));
        }

        //Отладочная инфа - вывод тела todo удалить после отладки
        LOGGER.warn(String.format("Получили запрос на get_number_info, header.X-Client-ID = %s \n" +
                "header.X-Client-Sign = %s \n, body как строка = %s, body как объект = %s", clientID, clientSign, body, numberInfo.toString()));

        if (Strings.isNullOrEmpty(clientID) || Strings.isNullOrEmpty(clientSign))
            LOGGER.error("Headers не получены. Проверь name");

        if (!clientID.equals(settings.getClientID())) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error("ClientID не совпадают");
            LOGGER.error(String.format("Получили ClientID %s \n В настройках ClientID %s", clientID, settings.getClientID()));
        }

        try {
            //Проверяем подпись
            if (settings.getIsNeedCheckSign())
                ControllerUtils.checkSigns(body, settings.getClientID(), settings.getClientKey(), clientSign, "get_number_info");
            // Находим клиента
            Client client = clientService.getFirstByNumber(numberInfo.getFrom_number());
            // Находим правило
            // Если клиента нет ни в одном правиле,
            // ищем правила для всех
            Rule rule = null;
            if (client == null) {
                rule = ruleService.getFirstRuleForAllCanUseNow();
                client = new Client(numberInfo.getFrom_number());
            } else {
                for (Rule clientsRule : client.getRules()) {
                    if (ruleService.isRuleCanUseNow(clientsRule)) {
                        rule = clientsRule;
                        break;
                    }
                }
            }
            if (rule == null)
                throw new NotFoundRequestNumberException(numberInfo.getFrom_number());
            // Формируем ответ
            NumberInfoAnswer answer = getAnswer(client, rule);
            if (answer == null)
                throw new NotFoundRequestNumberException(numberInfo.getFrom_number());

            //Делаем заголовки, нужные для ВАТС
            HttpHeaders headers = ControllerUtils.getHeaders(answer, clientID, settings.getClientKey());
            //TODO удалить после отладки
            System.err.println(ResponseEntity.status(200).headers(headers).body(answer));
            //
            LOGGER.warn("############################ SUCCESS! ############################");
            return ResponseEntity.ok().headers(headers).body(answer);
        } catch (SignsNotEquals signsNotEquals) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(signsNotEquals.getMessage());
            return ResponseEntity.status(403).body(new NumberInfoAnswer(403, "Подписи не равны"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error("Не удалось получить JSON ответа");
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Не удалось получить JSON ответа"));
        } catch (NotFoundRequestNumberException e) {
            LOGGER.warn("---------------------------- Предупреждение! -----------------------");
            LOGGER.error(e.getMessage());
            return ResponseEntity.status(404).body(new NumberInfoAnswer(404, e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Неизвестная ошибка"));
        }
    }


    /**
     * Маршрутизация вызова
     *
     * @param client клиент
     * @param rule   правило, по которому маршрутизируем
     * @return ответ серверу ВАТС, куда направить
     * @see NumberInfoAnswer
     */
    //todo set client nonnull
    public NumberInfoAnswer getAnswer(Client client, Rule rule) throws NumberParseException {
        if (rule.getIsSmart() && client != null) {
            // Если есть инфа о последнем разговоре
            if (!isEmptyOrWhitespace(client.getLastManagerNumber()))
                return new NumberInfoAnswer(client.getLastManagerNumber(), client);
            // Если информации нет, не теряем надежды и пробуем найти в логах
            String pin = logService.findLastPinByClientNumber(client.getNumber());
            if (pin != null)
                return new NumberInfoAnswer(pin);
        }
        //Маршрутизируем стандартно - на указанный номер
        else {
            if (rule.getManager() != null && !isEmptyOrWhitespace(rule.getManager().getNumber())) {
                return new NumberInfoAnswer(rule.getManager().getNumber(), client);
            }
            if (!isEmptyOrWhitespace(rule.getManagerNumber())) {
                return new NumberInfoAnswer(rule.getManagerNumber(), client);
            }
        }
        return null;
    }
}
