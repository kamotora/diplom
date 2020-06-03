package com.diplom.work.core.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Для парсинга JSON ответа с сервера, метод get_record
 * Содержит запись вызова, полученную по его session_id
 *
 **/
@Data
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetRecord {
    /**
     * Код выполнения операции:
     * 0 – Операция выполнена успешно (код проверен, номер свободен)
     * null - Не допускается
     */
    private String result;

    /**
     * Описание результата выполнения запроса
     * null - Не допускается
     */
    private String resultMessage;
    /**
     * Одноразовая ссылка на файл с записью разговора, доступный для скачивания.
     * null - Допускается, если result > 0
     */
    private String url;
}
