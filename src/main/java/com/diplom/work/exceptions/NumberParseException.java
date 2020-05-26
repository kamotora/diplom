package com.diplom.work.exceptions;

public class NumberParseException extends Exception {
    public NumberParseException(){
        super("Неправильно введён номер");
    }
    public NumberParseException(String inputNum){
        super("Не удалось спарсить номер из строки: "+inputNum);
    }
}
