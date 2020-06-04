package com.diplom.work.core.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

/**
 * Для парсинга JSON ответа с сервера, метод call_info
 * Содержит информацию по вызову, полученную по его session_id
 *
 * @author kamotora
 * *
 */
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallInfo {
    /**
     * Код выполнения операции:
     * 0 – Операция выполнена успешно (код проверен, номер свободен)
     * null - Не допускается
     */
    private Integer result;

    /**
     * Описание результата выполнения запроса
     * null - Допускается, если result = 0
     */
    private String resultMessage;

    /**
     * Детальная информация о вызове
     * null - Допускается, если result != 0
     */
    private Info info;

    @Data
    public static class Info {
        public Info(Integer call_type, Integer direction, Integer state, String orig_number, String orig_pin, String dest_number, String answering_sipuri, String answering_pin, String start_call_date, Integer duration, String session_log, Boolean is_voicemail, Boolean is_record, Boolean is_fax, String status_code, String status_string) {
            this.call_type = call_type;
            this.direction = direction;
            this.state = state;
            this.orig_number = orig_number;
            this.orig_pin = orig_pin;
            this.dest_number = dest_number;
            this.answering_sipuri = answering_sipuri;
            this.answering_pin = answering_pin;
            this.start_call_date = start_call_date;
            this.duration = duration;
            this.session_log = session_log;
            this.is_voicemail = is_voicemail;
            this.is_record = is_record;
            this.is_fax = is_fax;
            this.status_code = status_code;
            this.status_string = status_string;
        }

        /**
         * 1 – обычный звонок
         * 3 – callback
         * null - нет
         */
        private Integer call_type;
        /**
         * Направление вызова:
         * 1 – от внешнего клиента (входящий);
         * 2 – внешнему клиенту (исходяший);
         * 3 – внутренний
         * null - нет
         */
        private Integer direction;
        /**
         * 1 – вызов принят
         * 2 – вызов не принят
         * null - нет
         */
        private Integer state;
        /**
         * Номер в формате SIP-URI вызывающего абонента.
         * null - нет
         */
        private String orig_number;
        /**
         * PIN вызывающего абонента (для исходящих и внутренних вызовов).
         * null - Допускается для входящих вызовов
         */
        private String orig_pin;
        /**
         * Номер в формате SIP-URI вызывающего абонента.
         * - для входящих вызовов – номер линии домена;
         * null - нет
         */
        private String dest_number;
        /**
         * Номер первого ответившего абонента в формате SIP-URI.
         * null - Допускается, если не было акустического соединения
         * */
        private String answering_sipuri;
        /**
         * PIN первого ответившего абонента (для входящих и внутренних вызовов).
         * null - да
         * */
        private String answering_pin;
        /**
         * Дата и время входящего вызовы
         * Timestamp
         * null - нет
         * */
        private String start_call_date;
        /**
         * Продолжительность вызова в секундах,
         * при отсутствии соединения передается 0
         * null - нет
         * */
        private Integer duration;
        /**
         * Краткий протокол вызова (переадресации, переводы, перехваты и т.д.), как в журнале
         * null - нет
         * */
        private String session_log;
        /**
         * Флаг, уведомляющий о наличии голосового сообщения.
         * null - нет
         * */
        private Boolean is_voicemail;
        /**
         * Флаг, уведомляющий о наличии записи разговора.
         * null - нет
         * */
        private Boolean is_record;
        /**
         * Флаг, уведомляющий о наличии факсимильного сообщения.
         * null - нет
         * */
        private Boolean is_fax;
        /**
         * Код ошибки соединения
         * null - да
         * */
        private String status_code;
        /**
         * Текст ошибки соединения
         * null - да
         * */
        private String status_string;



        public String getCallTypeName(){
            switch(call_type){
                case(1): return "обычный звонок";
                case(3): return "callback";
                default: return "Неизвестный тип";
            }
        }

        public String getDirectionName(){
            switch(direction){
                case(1): return "входящий";
                case(2): return "исходящий";
                case(3): return "внутренний";
                default: return "неизвестный";
            }
        }

        public String getStateName(){
            switch(state){
                case(1): return "да";
                case(2): return "нет";
                default: return "неизвестно";
            }
        }

    }
}
