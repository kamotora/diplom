package com.diplom.work.exceptions;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
    @Override
    public String getMessage(){
        return String.format("Невалидный запрос. Ошибка: %s", super.getMessage());
    }
}
