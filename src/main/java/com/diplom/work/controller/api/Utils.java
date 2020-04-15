package com.diplom.work.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;

import java.nio.charset.StandardCharsets;

public class Utils {

    /**
     * @param body тело запроса в виде объекта для перевода в json
     * @param clientID уникальный код идентификации  с Ростелекома для подписи запроса
     * @param clientKey уникальный ключ для подписи
     * @return требуемые заголовки запроса
     * */
    public static HttpHeaders getHeaders(Object body, String clientID, String clientKey) throws JsonProcessingException {
        String bodyJSON = new ObjectMapper().writeValueAsString(body);
        System.out.println(bodyJSON);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Client-ID", clientID);
        headers.add("X-Client-Sing",
                Hashing.sha256().hashString(clientID+bodyJSON+clientKey, StandardCharsets.UTF_8).toString());
        return headers;
    }
}
