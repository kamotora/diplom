package com.diplom.work.svc;

import com.diplom.work.core.dto.CallInfo;
import com.diplom.work.core.dto.GetRecord;
import com.diplom.work.exceptions.SettingsNotFound;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import lombok.NonNull;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.UUID;

@Service
public class CallService {
    private final RequestService requestService;
    private final static String CALL_INFO_METHOD_NAME = "call_info";
    private final static String GET_RECORD_METHOD_NAME = "get_record";
    private final ObjectMapper objectMapper;

    @Autowired
    public CallService(RequestService requestService) {
        this.requestService = requestService;
        objectMapper = new ObjectMapper();
    }

    public CallInfo getCallInfoBySessionID(@NonNull String sessionID) throws SettingsNotFound, JsonProcessingException {
        Gson gson = new Gson();
        String body = objectMapper.createObjectNode().put("session_id", sessionID).toString();
        ResponseEntity<String> response = requestService.postRequest(CALL_INFO_METHOD_NAME, body);
        try {
            return gson.fromJson(response.getBody(), CallInfo.class);
        } catch (Exception e) {
            return null;
        }
    }

    public GetRecord getRecordBySessionID(@NonNull String sessionID, @Nullable String ip_address) throws SettingsNotFound, JsonProcessingException {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("session_id", sessionID);
        if (ip_address != null)
            objectNode.put("ip_adress", ip_address);
        String body = objectNode.toString();
        ResponseEntity<String> response = requestService.postRequest(GET_RECORD_METHOD_NAME, body);
        Gson gson = new Gson();
        try {
            return gson.fromJson(response.getBody(), GetRecord.class);
        } catch (Exception e) {
            return null;
        }
    }
}
