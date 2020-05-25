package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.Settings;
import com.diplom.work.core.json.CallInfo;
import com.diplom.work.exceptions.SettingsNotFound;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CallService {
    private final SettingsService settingsService;
    private static final String BASE_URL = "https://api.cloudpbx.rt.ru/call_info";

    @Autowired
    public CallService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public ResponseEntity<CallInfo> getCallInfoBySessionID(@NonNull String sessionID) throws SettingsNotFound, JsonProcessingException {
        String body = new ObjectMapper().createObjectNode().put("session_id", sessionID).toString();
        Settings settings = settingsService.getSettings();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(body, ControllerUtils.getHeaders(body, settings.getClientID(), settings.getClientKey()));
        ResponseEntity<CallInfo> response = restTemplate.postForEntity(BASE_URL, entity, CallInfo.class);
        System.err.println(response);
        return response;
    }
//    public CallInfo getCallInfoByLog(@NonNull Log log) throws SettingsNotFound, JsonProcessingException {
//        return getCallInfoBySessionID(log.getSession_id());
//    }
}
