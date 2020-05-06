package com.diplom.work.exceptions;

public class SettingsNotFound extends Exception {
    public SettingsNotFound() {
        super("Настроек не найдено!");
    }

    public SettingsNotFound(String message) {
        super(message);
    }
}
