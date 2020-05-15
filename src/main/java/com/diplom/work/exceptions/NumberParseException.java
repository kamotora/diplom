package com.diplom.work.exceptions;

public class NumberParseException extends Exception {
    public NumberParseException(String inputNum){
        super("Не удалось спарсить номер из строки: "+inputNum);
    }
}
