package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Settings;
import com.diplom.work.exceptions.SettingsNotFound;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

@Service
@Slf4j
public class RequestService {
    private final SettingsService settingsService;
    private static final String BASE_URL = "https://api.cloudpbx.rt.ru/";

    @Autowired
    public RequestService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    /**
     * Сделать post запрос на ВАТС
     *
     * @param method_name куда слать запрос (получится с https://api.cloudpbx.rt.ru/method_name)
     */
    public ResponseEntity<String> postRequest(@NonNull final String method_name, @NonNull final String body) throws SettingsNotFound, JsonProcessingException {
        URI uri = URI.create(BASE_URL + method_name);
        Settings settings = settingsService.getSettings();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = ControllerUtils.getHeaders(body, settings.getClientID(), settings.getClientKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        log.warn("Запрос по {}: {}", method_name, entity);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        log.warn("Получили ответ по {}: {}", method_name, response);
        return response;
    }
}
