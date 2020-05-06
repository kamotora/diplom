package com.diplom.work.exceptions;

public class SignsNotEquals extends Throwable {
    public SignsNotEquals(String name_method, String requestClientSign, String myClientSing) {
        super("Подписи " + name_method + " не равны\n" + "Пришёл header.X-Client-Sign = "
                + requestClientSign + "\nМы получили header.X-Client-Sign = " + myClientSing);
    }
}
