package com.diplom.work.controller;

import com.diplom.work.exceptions.NumberParseException;
import com.diplom.work.exceptions.SignsNotEquals;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.thymeleaf.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
public class ControllerUtils {
    private ControllerUtils() {
    }

    static Map<String, String> getErrors(BindingResult bindingResult) {
        Collector<FieldError, ?, Map<String, String>> collector = Collectors.toMap(
                fieldError -> fieldError.getField() + "Error",
                FieldError::getDefaultMessage
        );
        return bindingResult.getFieldErrors().stream().collect(collector);
    }

    /**
     * @param body      тело запроса в виде объекта для перевода в json или уже в json
     * @param clientID  уникальный код идентификации  с Ростелекома для подписи запроса
     * @param clientKey уникальный ключ для подписи
     * @return требуемые заголовки запроса
     */
    public static HttpHeaders getHeaders(Object body, String clientID, String clientKey) throws JsonProcessingException {
        String bodyJSON;
        // Если передали строку - считаем, что уже в JSON'e
        if(body instanceof String)
            bodyJSON = (String)body;
        else
            bodyJSON = new ObjectMapper().writeValueAsString(body);
        log.info(bodyJSON);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Client-ID", clientID);
        headers.add("X-Client-Sign",
                Hashing.sha256().hashString(clientID + bodyJSON + clientKey, StandardCharsets.UTF_8).toString());
        return headers;
    }

    public static void checkSigns(String body, String clientID, String clientKey, String requestClientSign, String name_method) throws SignsNotEquals {
        String myClientSing = Hashing.sha256().hashString(clientID + body + clientKey, StandardCharsets.UTF_8).toString();
        if (requestClientSign.equals(myClientSing)) {
            log.info("Подписи " + name_method + " равны");
        } else {
            throw new SignsNotEquals(name_method, requestClientSign, myClientSing);
        }
    }

    /**
     * @param sip номер в формате sip:number@domain
     * @return номер
     */
    public static String parseNumberFromSip(@NonNull String sip) throws NumberParseException {
        final String START_NUM = "sip:";
        int start = sip.indexOf(START_NUM);
        if (start == -1)
            throw new NumberParseException(sip);

        start += START_NUM.length();
        int finish = sip.indexOf('@', start);
        if (finish == -1)
            throw new NumberParseException(sip);

        String result;
        result = sip.substring(start, finish);
        if (StringUtils.isEmptyOrWhitespace(result))
            throw new NumberParseException(sip);

        return result;
    }

    public static String randomString(int len) {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }
}
