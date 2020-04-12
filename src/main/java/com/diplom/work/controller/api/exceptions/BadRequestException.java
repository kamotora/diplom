package com.diplom.work.controller.api.exceptions;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
    @Override
    public String getMessage(){
        return String.format("Невалидный запрос. Ошибка: %s", super.getMessage());
    }
}
