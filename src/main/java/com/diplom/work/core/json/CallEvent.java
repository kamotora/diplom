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
