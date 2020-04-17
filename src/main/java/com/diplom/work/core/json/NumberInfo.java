package com.diplom.work.core.json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Для парсинга JSON ответа с сервера, метод call_event
 * @author kamotora
 *  * */

public final class NumberInfo {
    private String domain;
    private String from_number;
    private String request_number;

    public NumberInfo() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    @Override
    public String toString() {
        return "NumberInfo{" +
                "domain='" + domain + '\'' +
                ", from_number='" + from_number + '\'' +
                ", request_number='" + request_number + '\'' +
                '}';
    }
}
