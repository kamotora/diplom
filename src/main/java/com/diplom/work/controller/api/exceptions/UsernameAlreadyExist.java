package com.diplom.work.controller.api.exceptions;

public class UsernameAlreadyExist extends Exception{
    public UsernameAlreadyExist() {
        super("Пользователь с таким логином уже есть!");
    }

    public UsernameAlreadyExist(String message) {
        super(message);
    }
}
