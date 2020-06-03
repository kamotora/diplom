package com.diplom.work.controller.api;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Client;
import com.diplom.work.core.Log;
import com.diplom.work.core.Settings;
import com.diplom.work.exceptions.ManagerNumberNotFoundException;
import com.diplom.work.exceptions.SignsNotEquals;
import com.diplom.work.repo.LogRepository;
import com.diplom.work.svc.ClientService;
import com.diplom.work.svc.SettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Обработка запроса по call_events от ВАТС
 */
@RestController
@Slf4j
@RequestMapping("api/")
public class CallEventController {
    private final LogRepository logRepository;
    private final SettingsService settingsService;
    private final ClientService clientService;

    private static final String ERROR_TITLE = "############################ ОШИБОЧКА! ############################";

    @Autowired
    public CallEventController(LogRepository logRepository, SettingsService settingsService, ClientService clientService) {
        this.logRepository = logRepository;
        this.settingsService = settingsService;
        this.clientService = clientService;
    }

    /**
     * Обработка запроса по call_events от ВАТС
     * Вся информация записывается в лог (Журнал звонков)
     *
     * @param clientID   - идентификатор клиента, должен совпадать с нашим ClientID в настройках @see Settings
     * @param clientSign - подпись запроса = sha256hex(clientid+body+clientkey). Можно проверить, и если не совпадает, игнорить. А можно ничего не делать)
     * @param body       - тело запроса
     * @return ответ 200 серверу
     * @see Log тело запроса в виде класса
     */
    @PostMapping(path = "call_events",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> getNewCall(@RequestHeader(name = "X-Client-ID", required = false) String clientID,
                                           @RequestHeader(name = "X-Client-Sign", required = false) String clientSign,
                                           @RequestBody String body) throws JsonProcessingException {

        Log callEvent = new ObjectMapper().readValue(body, Log.class);
        // Получаем настройки
        try {
            Settings settings = settingsService.getSettings();

            //Проверяем подпись
            if (settings.getIsNeedCheckSign())
                ControllerUtils.checkSigns(body, settings.getClientID(), settings.getClientKey(), clientSign, "call_events");

            // Если это начало разговора и входящий или исходящий вызов
            // парсим номера менеджера и клиента
            // ищем клиента в базе по номеру
            // обновляем последнего говорившего с клиентом
            if (callEvent.isCall() && !callEvent.isInternal()) {
                String clientNumber, managerNumber;
                if (callEvent.isIncoming()) {
                    clientNumber = ControllerUtils.parseNumberFromSip(callEvent.getFrom_number());
                    managerNumber = callEvent.getRequest_pin();
                } else {
                    clientNumber = ControllerUtils.parseNumberFromSip(callEvent.getRequest_number());
                    managerNumber = callEvent.getFrom_pin();
                }
                if (managerNumber != null) {
                    Client client = clientService.getFirstByNumberSubstr(clientNumber);
                    if (client == null) {
                        client = new Client();
                        client.setNumber(clientNumber);
                    }
                    client.setLastManagerNumber(managerNumber);
                    clientService.save(client);
                } else
                    throw new ManagerNumberNotFoundException();
            }
        } catch (SignsNotEquals signsNotEquals) {
            log.error(ERROR_TITLE);
            log.error(signsNotEquals.getMessage());
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            // Настроек нет
            log.error(ERROR_TITLE);
            log.error("ТЕКСТ ОШИБКИ: {}. Получили запрос на call_events, header.X-Client-ID = {} %n" +
                    "header.X-Client-Sign = {} %n, body = {}", e, clientID, clientSign, callEvent);
            return ResponseEntity.status(500).build();
        }

        // Сохраняем лог
        logRepository.save(callEvent);
        log.warn("Получили запрос на call_events, body = {}", callEvent);
        return ResponseEntity.ok().build();
    }


}
