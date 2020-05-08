package com.diplom.work.controller.api;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Log;
import com.diplom.work.core.Settings;
import com.diplom.work.core.json.NumberInfoAnswer;
import com.diplom.work.exceptions.SignsNotEquals;
import com.diplom.work.repo.LogRepository;
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

    @Autowired
    public CallEventController(LogRepository logRepository, SettingsService settingsService) {
        this.logRepository = logRepository;
        this.settingsService = settingsService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CallEventController.class);


    @PostMapping(path = "call_events",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> getNewCall(@RequestHeader(name = "X-Client-ID", required = false) String clientID,
                                           @RequestHeader(name = "X-Client-Sign" , required = false)String clientSign,
                                           @RequestBody String body) throws JsonProcessingException {

        Log callEvent = new ObjectMapper().readValue(body, Log.class);
        // Получаем настройки
        try {
            Settings settings = settingsService.getSettings();
            //Проверяем подпись
            if (settings.getIsNeedCheckSign())
                ControllerUtils.checkSigns(body, settings.getClientID(), settings.getClientKey(), clientSign, "call_events");
        } catch (Exception e) {
            // Настроек нет
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(String.format("ТЕКСТ ОШИБКИ: %s Получили запрос на call_events, header.X-Client-ID = %s \n" +
                    "header.X-Client-Sign = %s \n, body = %s", e.getMessage(), clientID, clientSign, callEvent.toString()));
            return ResponseEntity.status(500).build();
        }
        catch (SignsNotEquals signsNotEquals) {
            LOGGER.error("############################ ОШИБОЧКА! ############################");
            LOGGER.error(signsNotEquals.getMessage());
            return ResponseEntity.status(403).build();
        }
        logRepository.save(callEvent);
        LOGGER.warn("Получили запрос на call_events, body = "+callEvent.toString());
        return ResponseEntity.ok().build();
    }


}
