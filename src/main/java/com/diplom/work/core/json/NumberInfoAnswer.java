package com.diplom.work.core.json;

public class NumberInfoAnswer {
    private int result;
    //Описание результата выполнения запроса. Мб Null при result == 0
    private String resultMessage;
    //Отображаемое имя для добавления информации о вызове. Мб Null при result > 0
    private String displayName;
    private String PIN;

    /**
     * Если всё ок
     * @param PIN - куда направить
     * @param displayName - ин-фа о вызове
     * */
    public NumberInfoAnswer(String PIN, String displayName) {
        this.result = 0;
        this.displayName = displayName;
        this.PIN = PIN;
    }
    /**
     * Если что-то не так ок
     * @param result - код ошибки > 0
     * @param resultMessage - ин-фа об ошибке
     * */
    public NumberInfoAnswer(int result, String resultMessage) {
        this.result = result;
        this.resultMessage = resultMessage;
    }

    public NumberInfoAnswer(int result, String resultMessage, String displayName, String PIN){
        this.result = result;
        this.resultMessage = resultMessage;
        this.displayName = displayName;
        this.PIN = PIN;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }
}
