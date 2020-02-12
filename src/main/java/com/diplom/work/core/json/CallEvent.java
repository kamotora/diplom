package com.diplom.work.core.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Для парсинга JSON ответа с сервера, метод call_event
 * @author kamotora
 *  * */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CallEvent {
    private String session_id;
    //private String timestamp;
    private Timestamp timestamp;
    private String type;
    private String state;
    private String from_number;
    private String from_pin;
    private String request_number;
    private String request_pin;
    private String disconnect_reason;
    private String is_record;
}
