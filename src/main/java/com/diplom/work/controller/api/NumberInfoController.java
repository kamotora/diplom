package com.diplom.work.controller.api;


import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Rule;
import com.diplom.work.core.Settings;
import com.diplom.work.core.json.NumberInfo;
import com.diplom.work.core.json.NumberInfoAnswer;
import com.diplom.work.exceptions.SignsNotEquals;
import com.diplom.work.repo.RuleRepository;
import com.diplom.work.svc.SettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class NumberInfoController {
    private final RuleRepository ruleRepository;
    private final SettingsService settingsService;

    @Autowired
    public NumberInfoController(RuleRepository ruleRepository, SettingsService settingsService) {
        this.ruleRepository = ruleRepository;
        this.settingsService = settingsService;
    }

    //  @Value("${api.Client-ID}")
    private String myClientID;
    //  @Value("${api.Client-Key}")
    private String myClientKey;

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
            myClientID = settings.getClientID();
            myClientKey = settings.getClientKey();
        } catch (Exception e) {
            // Настроек нет
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(String.format("ТЕКСТ ОШИБКИ: %s Получили запрос на get_number_info, header.X-Client-ID = %s \n" +
                    "header.X-Client-Sign = %s \n, body = %s", e.getMessage(), clientID, clientSign, numberInfo.toString()));
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Настройки не настроены"));
        }

        //Отладочная инфа
        LOGGER.warn(String.format("Получили запрос на get_number_info, header.X-Client-ID = %s \n" +
                "header.X-Client-Sign = %s \n, body = %s", clientID, clientSign, numberInfo.toString()));
        if (Strings.isNullOrEmpty(clientID) || Strings.isNullOrEmpty(clientSign))
            LOGGER.error("Headers не получены. Проверь name");

        if (!clientID.equals(myClientID)) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error("ClientID не совпадают");
            LOGGER.error(String.format("Получили ClientID %s \n В настройках ClientID %s", clientID, myClientID));
        }

        try {
            //Проверяем подпись
            if (settings.getIsNeedCheckSign())
                ControllerUtils.checkSigns(body, myClientID, myClientKey, clientSign, "get_number_info");
            //Находим правило
            Rule rule = ruleRepository.findByClientNumber(numberInfo.getFrom_number());
            if (rule == null)
                throw new IncorrectResultSizeDataAccessException(0);
            // Формируем ответ
            NumberInfoAnswer answer = new NumberInfoAnswer(rule);
            //Делаем заголовки, нужные для ВАТС
            HttpHeaders headers = ControllerUtils.getHeaders(answer, clientID, myClientKey);
            //TODO удалить после отладки
            System.err.println(ResponseEntity.status(200).headers(headers).body(answer));
            //
            LOGGER.warn("############################ SUCCESS! ############################");
            return ResponseEntity.ok().headers(headers).body(answer);
        } catch (IncorrectResultSizeDataAccessException e) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error("Не нашли правило для " + numberInfo.getFrom_number());
            return ResponseEntity.status(404).body(new NumberInfoAnswer(404, "Не найдено информации о том, куда перенаправлять"));
        } catch (SignsNotEquals signsNotEquals) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(signsNotEquals.getMessage());
            return ResponseEntity.status(403).body(new NumberInfoAnswer(403, "Подписи не равны"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error("Не удалось получить JSON ответа");
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Не удалось получить JSON ответа"));
        } catch (Exception e) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(new NumberInfoAnswer(500, "Неизвестная ошибка"));
        }
    }
}
