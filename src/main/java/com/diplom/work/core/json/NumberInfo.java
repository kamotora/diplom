package com.diplom.work.core.json;

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
    private String from_number;
    private String request_number;
}
