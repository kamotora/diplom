package com.diplom.work.core;

import com.diplom.work.core.json.view.LogsViews;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonView;
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
@Data
public class Log {
    @Id
    @GeneratedValue
    @JsonView(LogsViews.onlyId.class)
    private Long id;

    @Column(name = "session_id")
    @JsonView(LogsViews.forTable.class)
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
    @JsonView(LogsViews.forTable.class)
    private String from_number;

    @Column(name = "from_pin")
    private String from_pin;

    @Column(name = "request_number")
    @JsonView(LogsViews.forTable.class)
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @JsonView(LogsViews.forTable.class)
    public String getTimestamp() {
        return timestamp;
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
    @JsonView(LogsViews.forTable.class)
    @JsonGetter("state_call")
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
     * @return Тип вызова по-русски согласно документации
     * Из документации:
     * Тип вызова:
     * incoming – входящий
     * outbound – исходящий
     * internal – внутренний
     */
    @JsonView(LogsViews.forTable.class)
    @JsonGetter("type")
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
