package com.diplom.work.exceptions;

public class SignsNotEquals extends Throwable {
    public SignsNotEquals(String nameMethod, String requestClientSign, String myClientSing) {
        super("Подписи " + nameMethod + " не равны\n" + "Пришёл header.X-Client-Sign = "
                + requestClientSign + "\nМы получили header.X-Client-Sign = " + myClientSing);
    }
}
