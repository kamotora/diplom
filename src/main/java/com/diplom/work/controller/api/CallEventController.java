package com.diplom.work.controller.api;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Client;
import com.diplom.work.core.Log;
import com.diplom.work.core.Settings;
import com.diplom.work.exceptions.SignsNotEquals;
import com.diplom.work.repo.LogRepository;
import com.diplom.work.svc.ClientService;
import com.diplom.work.svc.SettingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class CallEventController {
    private final LogRepository logRepository;
    private final SettingsService settingsService;
    private final ClientService clientService;

    @Autowired
    public CallEventController(LogRepository logRepository, SettingsService settingsService, ClientService clientService) {
        this.logRepository = logRepository;
        this.settingsService = settingsService;
        this.clientService = clientService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CallEventController.class);


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
                }
                else
                    throw new Exception("Не найден номер менеджера");
            }
        } catch (Exception e) {
            // Настроек нет
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(String.format("ТЕКСТ ОШИБКИ: %s Получили запрос на call_events, header.X-Client-ID = %s \n" +
                    "header.X-Client-Sign = %s \n, body = %s", e.getMessage(), clientID, clientSign, callEvent.toString()));
            return ResponseEntity.status(500).build();
        } catch (SignsNotEquals signsNotEquals) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(signsNotEquals.getMessage());
            return ResponseEntity.status(403).build();
        }

        // Сохраняем лог
        logRepository.save(callEvent);
        LOGGER.warn("Получили запрос на call_events, body = " + callEvent.toString());
        return ResponseEntity.ok().build();
    }


}
