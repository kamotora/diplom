package com.diplom.work.controller;

import com.diplom.work.exceptions.SignsNotEquals;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class ControllerUtils {
    static Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }

    /**
     * @param body      тело запроса в виде объекта для перевода в json
     * @param clientID  уникальный код идентификации  с Ростелекома для подписи запроса
     * @param clientKey уникальный ключ для подписи
     * @return требуемые заголовки запроса
     */
    public static HttpHeaders getHeaders(Object body, String clientID, String clientKey) throws JsonProcessingException {
        String bodyJSON = new ObjectMapper().writeValueAsString(body);
        System.out.println(bodyJSON);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Client-ID", clientID);
        headers.add("X-Client-Sign",
                Hashing.sha256().hashString(clientID + bodyJSON + clientKey, StandardCharsets.UTF_8).toString());
        return headers;
    }

    public static boolean checkSigns(String body, String clientID, String clientKey, String requestClientSign, String name_method) throws SignsNotEquals {
        String myClientSing = Hashing.sha256().hashString(clientID + body + clientKey, StandardCharsets.UTF_8).toString();
        if (requestClientSign.equals(myClientSing)) {
            System.out.println("Подписи " + name_method + " равны");
            return true;
        } else {
            throw new SignsNotEquals(name_method, requestClientSign, myClientSing);
        }
    }

    /**
     * @param sip номер в формате sip:number@domain
     * @return номер
     */
    public static String parseNumberFromSip(@NonNull String sip) {
        int start = sip.indexOf("sip:");
        if (start == -1)
            return null;
        int finish = sip.indexOf('@', start);
        if (finish == -1)
            return null;
        String result;
        try {
            result = sip.substring(start, finish);
            if (result.isEmpty())
                return null;
        } catch (Exception exception) {
            return null;
        }
        return result;
    }
}
