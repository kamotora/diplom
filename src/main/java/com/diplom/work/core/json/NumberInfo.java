package com.diplom.work.core.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Для парсинга JSON ответа с сервера, метод call_event
 * @author kamotora
 *  * */

public final class NumberInfo {
    private String domain;
    private String timestamp;
    private String from_number;
    private String request_number;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public LocalDateTime getTimestampInDateTimeFormat(){
        if(timestamp == null)
            return null;
        return Timestamp.valueOf(timestamp).toLocalDateTime();
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getFrom_number() {
        return from_number;
    }

    public void setFrom_number(String from_number) {
        this.from_number = from_number;
    }

    public String getRequest_number() {
        return request_number;
    }

    public void setRequest_number(String request_number) {
        this.request_number = request_number;
    }
}
