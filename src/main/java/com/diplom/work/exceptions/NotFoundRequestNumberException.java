package com.diplom.work.exceptions;

public class NotFoundRequestNumberException extends Exception {

    public NotFoundRequestNumberException(String requestNumber) {
        super("Не найдено информации о том, куда перенаправлять. Звонил: " + requestNumber);
    }
}
