package com.diplom.work.exceptions;

public class OldPasswordsNotEquals extends Throwable {
    public OldPasswordsNotEquals() {
        super("Старые пароли не совпадают");
    }

    public OldPasswordsNotEquals(String message) {
        super(message);
    }
}
