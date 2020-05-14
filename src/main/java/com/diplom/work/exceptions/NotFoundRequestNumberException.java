package com.diplom.work.exceptions;

public class NotFoundRequestNumberException extends Throwable {
    public NotFoundRequestNumberException() {
        super("Не найдено информации о том, куда перенаправлять");
    }

    public NotFoundRequestNumberException(String requestNumber) {
        super("Не найдено информации о том, куда перенаправлять. Звонил: " + requestNumber);
    }
}
