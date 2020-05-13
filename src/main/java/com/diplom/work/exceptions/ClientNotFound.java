package com.diplom.work.exceptions;

public class ClientNotFound extends Exception {
    public ClientNotFound() {
        super("Такого клиента не найдено");
    }

    public ClientNotFound(String message) {
        super(message);
    }

}
