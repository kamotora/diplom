package com.diplom.work.core;

import com.diplom.work.core.json.view.Views;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@EqualsAndHashCode
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(Views.onlyId.class)
    private Long id;

    @Column(name = "session_id")
    @JsonView(Views.forTable.class)
    private String session_id;

    @Column(name = "timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @JsonView(Views.forTable.class)
    private String timestamp;

    @Transient
    @JsonView(Views.simpleObject.class)
    private LocalDateTime timestampInDateTimeFormat;

    @Column(name = "type")
    private String type;

    @Column(name = "state")
    private String state;

    @Column(name = "from_number")
    @JsonView(Views.forTable.class)
    private String from_number;

    @Column(name = "from_pin")
    @JsonView(Views.simpleObject.class)
    private String from_pin;

    @Column(name = "request_number")
    @JsonView(Views.forTable.class)
    private String request_number;

    @Column(name = "request_pin")
    @JsonView(Views.simpleObject.class)
    private String request_pin;

    @JsonView(Views.simpleObject.class)
    private String disconnect_reason;

    //true,false
    @JsonView(Views.simpleObject.class)
    private String is_record;

    @Transient
    public LocalDateTime getTimestampInDateTimeFormat() {
        if (timestamp == null && timestampInDateTimeFormat == null)
            return null;
        if (timestampInDateTimeFormat == null)
            timestampInDateTimeFormat = Timestamp.valueOf(timestamp).toLocalDateTime();
        return timestampInDateTimeFormat;
    }

    public boolean getIs_recordAsBool() {
        if (is_record == null || is_record.isEmpty())
            return false;
        return is_record.strip().equalsIgnoreCase("true");
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
    @JsonView(Views.forTable.class)
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
    @JsonView(Views.forTable.class)
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

    /**
     * @return true - если этот лог о начале разговора, иначе - false
     */
    @Transient
    public boolean isCall() {
        return state.equals("connected");
    }

    /**
     * @return true - если этот лог для внутреннего вызова
     */
    @Transient
    public boolean isInternal() {
        return type.equals("internal");
    }

    /**
     * @return true - если этот лог для входящего вызова
     */
    @Transient
    public boolean isOutbound() {
        return type.equals("outbound");
    }

    /**
     * @return true - если этот лог для исходящего вызова
     */
    @Transient
    public boolean isIncoming() {
        return type.equals("incoming");
    }

}
