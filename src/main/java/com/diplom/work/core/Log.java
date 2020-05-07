package com.diplom.work.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Для парсинга JSON ответа с сервера, метод call_event
 * Для записи в лог
 *
 * @see lombok.Lombok
 * *
 */
@Entity
@Table(name = "logs")
@AllArgsConstructor
@NoArgsConstructor
@Data
////Для проверки подписи
//@JsonPropertyOrder(
//        {"session_id", "timestamp", "type", "state", "from_number", "from_pin", "request_number", "request_pin", "disconnect_reason", "is_record"})
public class Log {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "session_id")
    private String session_id;
    @Column(name = "timestamp")
    private String timestamp;
    @Transient
    private LocalDateTime timestampInDateTimeFormat;
    @Column(name = "type")
    private String type;
    @Column(name = "state")
    private String state;
    @Column(name = "from_number")
    private String from_number;
    @Column(name = "from_pin")
    private String from_pin;
    @Column(name = "request_number")
    private String request_number;
    @Column(name = "request_pin")
    private String request_pin;
    private String disconnect_reason;
    //true,false
    private String is_record;

    @Transient
    public LocalDateTime getTimestampInDateTimeFormat() {
        if (timestampInDateTimeFormat == null)
            timestampInDateTimeFormat = Timestamp.valueOf(timestamp).toLocalDateTime();
        return timestampInDateTimeFormat;
    }

    public boolean getIs_recordAsBool() {
        if (is_record == null || is_record.isEmpty())
            return false;
        return is_record.strip().toLowerCase().equals("true");
    }

    /**
     * @return Тип уведомления по-русски согласно документации
     * Из документации:
     * Тип уведомления:
     * new – о новом вызове
     * calling – начало дозвона на контактный номер сотрудника;
     * connected – о начале разговора
     * disconnected – о завершении разговора
     * end – о завершении вызова
     */
    public String getStateName() {
        switch (state) {
            case "new":
                return "Новый вызов";
            case "calling":
                return "Начало дозвона";
            case "connected":
                return "Начало разговора";
            case "disconnected":
                return "Завершение разговора";
            case "end":
                return "Вызов завершен";
            default:
                return "Неизвестное состояние";
        }
    }


    /**
     * @return Тип уведомления по-русски согласно документации
     * Из документации:
     * Тип вызова:
     * incoming – входящий
     * outbound – исходящий
     * internal – внутренний
     */
    public String getTypeName() {
        switch (type) {
            case "incoming":
                return "Входящий";
            case "outbound":
                return "Исходящий";
            case "internal":
                return "Внутренний";
            default:
                return "Неизвестный тип вызова";
        }
    }

}
