package com.diplom.work.controller.api;


import com.diplom.work.comparators.RulePriorityComparator;
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
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.thymeleaf.util.StringUtils.isEmptyOrWhitespace;

/**
 * Обработка запроса по get_number_info от ВАТС
 */
@RestController
@RequestMapping("api/")
@Slf4j
public class NumberInfoController {
    private final RuleService ruleService;
    private final SettingsService settingsService;
    private final ClientService clientService;
    private final LogService logService;

    private static final String SUCCESS_TITLE = "++++++++++++++++++++++++++++ SUCCESS! ++++++++++++++++++++++++++++";
    private static final String WARN_TITLE = "---------------------------- Предупреждение! -----------------------";
    private static final String ERROR_TITLE = "############################ ОШИБОЧКА! ############################";

    @Autowired
    public NumberInfoController(RuleService ruleService, SettingsService settingsService, ClientService clientService, LogService logService) {
        this.ruleService = ruleService;
        this.settingsService = settingsService;
        this.clientService = clientService;
        this.logService = logService;
    }

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
            log.error(ERROR_TITLE);
            log.error("ОШИБКА:{}  Получили запрос на get_number_info, header.X-Client-ID = {}%n" +
                    "header.X-Client-Sign = {} %n, body = {}", e, clientID, clientSign, numberInfo);
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, e.getMessage()));
        }

        //Отладочная инфа - вывод тела
        // todo удалить после отладки
        log.warn(String.format("Получили запрос на get_number_info, header.X-Client-ID = %s %n" +
                "header.X-Client-Sign = %s %n, body как строка = %s, body как объект = %s", clientID, clientSign, body, numberInfo.toString()));

        if (!clientID.equals(settings.getClientID())) {
            log.error(ERROR_TITLE);
            log.error("ClientID не совпадают");
            log.error("Получили ClientID {} %n В настройках ClientID {}", clientID, settings.getClientID());
        }

        try {
            //Проверяем подпись
            if (settings.getIsNeedCheckSign())
                ControllerUtils.checkSigns(body, settings.getClientID(), settings.getClientKey(), clientSign, "get_number_info");
            // Находим клиента
            Client client = clientService.getFirstByNumberSubstr(numberInfo.getFrom_number());

            // Правила для всех используются всегда)
            Set<Rule> allRules = ruleService.getRulesForAll();
            //Если такого клиента нет, создадим
            if (client == null) {
                client = new Client(numberInfo.getFrom_number());
                clientService.save(client);
            } else {
                //Если клиент есть, добавляем в общую кучу правила с его участием
                allRules.addAll(client.getRules());
            }

            // Фильтруем по времени, ищем первое правило с наибольшим приоритетом (число наименьшее), если правил нет, то null
            Rule rule = allRules.stream()
                    .filter(ruleService::isRuleCanUseNow)
                    .min(new RulePriorityComparator())
                    .orElseThrow(() -> new NotFoundRequestNumberException(numberInfo.getFrom_number()));

            // Формируем ответ
            NumberInfoAnswer answer = getAnswer(client, rule);
            if (answer == null)
                throw new NotFoundRequestNumberException(numberInfo.getFrom_number());

            //Делаем заголовки, нужные для ВАТС
            HttpHeaders headers = ControllerUtils.getHeaders(answer, clientID, settings.getClientKey());
            // Вывод тела ответа
            // TODO удалить после отладки
            log.warn("Тело ответа: {}", ResponseEntity.status(200).headers(headers).body(answer));
            // Вывод сообщения об успехе
            log.info(SUCCESS_TITLE);
            return ResponseEntity.ok().headers(headers).body(answer);
        } catch (SignsNotEquals signsNotEquals) {
            log.error(ERROR_TITLE);
            log.error(signsNotEquals.getMessage());
            return ResponseEntity.status(403).body(new NumberInfoAnswer(403, "Подписи не равны"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.error(ERROR_TITLE);
            log.error("Не удалось получить JSON ответа");
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Не удалось получить JSON ответа"));
        } catch (NotFoundRequestNumberException e) {
            log.warn(WARN_TITLE);
            log.warn(e.getMessage());
            return ResponseEntity.status(404).body(new NumberInfoAnswer(404, e.getMessage()));
        } catch (Exception e) {
            log.error("");
            log.error(e.getMessage());
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
    public NumberInfoAnswer getAnswer(@NonNull Client client, @NonNull Rule rule) throws NumberParseException {
        if (rule.getIsSmart()) {
            // Если есть инфа о последнем разговоре
            if (!isEmptyOrWhitespace(client.getLastManagerNumber()))
                return new NumberInfoAnswer(client.getLastManagerNumber(), client);
            // Если информации нет, не теряем надежды и пробуем найти в логах
            String pin = logService.findLastPinByClientNumber(client.getNumber());
            if (pin != null) {
                return new NumberInfoAnswer(pin, client);
            }
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