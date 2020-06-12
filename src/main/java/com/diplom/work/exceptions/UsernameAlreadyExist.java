package com.diplom.work.exceptions;

public class UsernameAlreadyExist extends Exception{
    public UsernameAlreadyExist() {
        super("Пользователь с таким логином уже есть!");
    }

}
