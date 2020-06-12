package com.diplom.work.core.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Для парсинга JSON ответа с сервера, метод call_event
 * @author kamotora
 *  * */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class NumberInfo {
    private String domain;
    @JsonProperty("from_number")
    private String fromNumber;
    @JsonProperty("request_number")
    private String requestNumber;
}
