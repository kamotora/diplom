package com.diplom.work.core;

public enum Days {
    Monday("Пн"),
    Tuesday("Вт"),
    Wednesday("Ср"),
    Thursday("Чт"),
    Friday("Пт"),
    Saturday("Сб"),
    Sunday("Вс");

    private final String name;

    Days(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
