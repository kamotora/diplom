package com.diplom.work.exceptions;

public class ManagerNumberNotFoundException extends Exception {
    public ManagerNumberNotFoundException() {
        super("Не найден номер менеджера");
    }
}
