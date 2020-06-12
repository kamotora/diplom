package com.diplom.work.core.json;

import com.diplom.work.core.Client;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NumberInfoAnswer {
    private int result;
    //Описание результата выполнения запроса. Мб Null при result == 0
    private String resultMessage;
    //Отображаемое имя для добавления информации о вызове. Мб Null при result > 0
    private String displayName;
    // Внутренний номер менеджера
    @JsonProperty("PIN")
    private String PIN;

    /**
     * Если всё ок и о клиенте ничего не известно (кроме номера)
     *
     * @param pin - куда направить
     */
    public NumberInfoAnswer(String pin) {
        this.result = 0;
        this.displayName = "Нет информации о ФИО";
        this.PIN = pin;
    }

    /**
     * Если всё ок
     *
     * @param pin         - куда направить
     * @param displayName - ин-фа о вызове (например, ФИО клиента)
     */
    public NumberInfoAnswer(String pin, String displayName) {
        this.result = 0;
        this.displayName = displayName;
        this.PIN = pin;
    }

    /**
     * Если всё ок + сообщение об успехе
     *
     * @param pin         - куда направить
     * @param displayName - ин-фа о вызове
     * @param fromNumber  - кто звонит (для вывода сообещния)
     */
    public NumberInfoAnswer(String pin, String fromNumber, String displayName) {
        this.result = 0;
        this.displayName = displayName;
        this.PIN = pin;
        this.resultMessage = String.format("Успешно перенаправили номер %s на внутренний номер %s", fromNumber, pin);
    }

    /**
     * Если всё ок + сообщение об успехе
     *
     * @param pin    - куда направить
     * @param client - ин-фа о клиенте
     */
    public NumberInfoAnswer(String pin, Client client) {
        this.result = 0;
        this.displayName = client.getName();
        this.PIN = pin;
        this.resultMessage = String.format("Успешно перенаправили номер %s на внутренний номер %s", client.getNumber(), pin);
    }

    /**
     * Если что-то не так ок
     *
     * @param result        - код ошибки > 0
     * @param resultMessage - ин-фа об ошибке
     */
    public NumberInfoAnswer(int result, String resultMessage) {
        this.result = result;
        this.resultMessage = resultMessage;
    }

    public NumberInfoAnswer(int result, String resultMessage, String displayName, String pin) {
        this.result = result;
        this.resultMessage = resultMessage;
        this.displayName = displayName;
        this.PIN = pin;
    }
}

