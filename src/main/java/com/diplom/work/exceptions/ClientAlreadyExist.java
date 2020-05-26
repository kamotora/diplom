package com.diplom.work.exceptions;

import com.diplom.work.core.Client;

public class ClientAlreadyExist extends Exception{
    public ClientAlreadyExist() {
        super("Клиент с таким логином уже есть!");
    }

    public ClientAlreadyExist(String message) {
        super(message);
    }

    public ClientAlreadyExist(Client findedClient){ super("Клиент с таким номером уже есть. Номер: " + findedClient.getNumber() + ", ФИО: " + findedClient.getName());}
}
