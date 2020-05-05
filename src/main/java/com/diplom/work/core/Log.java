package com.diplom.work.core;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Для парсинга JSON ответа с сервера, метод call_event
 * Для записи в лог
*  * */
@Entity
@Table(name = "logs")
@AllArgsConstructor
@NoArgsConstructor
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Transient
    public LocalDateTime getTimestampInDateTimeFormat() {
        if(timestampInDateTimeFormat == null)
            timestampInDateTimeFormat = Timestamp.valueOf(timestamp).toLocalDateTime();
        return timestampInDateTimeFormat;
    }


    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFrom_number() {
        return from_number;
    }

    public void setFrom_number(String from_number) {
        this.from_number = from_number;
    }

    public String getFrom_pin() {
        return from_pin;
    }

    public void setFrom_pin(String from_pin) {
        this.from_pin = from_pin;
    }

    public String getRequest_number() {
        return request_number;
    }

    public void setRequest_number(String request_number) {
        this.request_number = request_number;
    }

    public String getRequest_pin() {
        return request_pin;
    }

    public void setRequest_pin(String request_pin) {
        this.request_pin = request_pin;
    }

    public String getDisconnect_reason() {
        return disconnect_reason;
    }

    public void setDisconnect_reason(String disconnect_reason) {
        this.disconnect_reason = disconnect_reason;
    }

    public String getIs_record() {
        return is_record;
    }

    public void setIs_record(String is_record) {
        this.is_record = is_record;
    }

    public boolean getIs_recordAsBool() {
        if(is_record == null || is_record.isEmpty())
            return false;
        return is_record.equals("true");
    }

    /**
     * @return Тип уведомления по-русски согласно документации
     * Из документации:
     *      Тип уведомления:
     *      new – о новом вызове
     *      calling – начало дозвона на контактный номер сотрудника;
     *      connected – о начале разговора
     *      disconnected – о завершении разговора
     *      end – о завершении вызова
     * */
    public String getStateName(){
        switch (state){
            case "new": return "Новый вызов";
            case "calling": return "Начало дозвона";
            case "connected": return "Начало разговора";
            case "disconnected": return "Завершение разговора";
            case "end": return "Вызов завершен";
            default: return "Неизвестное состояние";
        }
    }


    /**
     * @return Тип уведомления по-русски согласно документации
     * Из документации:
     *      Тип вызова:
     *      incoming – входящий
     *      outbound – исходящий
     *      internal – внутренний
     * */
    public String getTypeName(){
        switch (type){
            case "incoming": return "Входящий";
            case "outbound": return "Исходящий";
            case "internal": return "Внутренний";
            default: return "Неизвестный тип вызова";
        }
    }

}
