package com.diplom.work.controller.api.exceptions;

public class NewPasswordsNotEquals  extends Exception {
    public NewPasswordsNotEquals() {
        super("Новые пароли не совпадают");
    }

    public NewPasswordsNotEquals(String message) {
        super(message);
    }
}
