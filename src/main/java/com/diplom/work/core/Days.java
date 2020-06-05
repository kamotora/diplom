package com.diplom.work.core;

import java.time.DayOfWeek;
import java.time.LocalDate;

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

    public static Days getTodayDay(){
        return getByDayOfWeek(DayOfWeek.from(LocalDate.now()));
    }

    public static Days getByDayOfWeek(DayOfWeek dayOfWeek){
        switch (dayOfWeek){
            case MONDAY: return Monday;
            case TUESDAY: return Tuesday;
            case WEDNESDAY: return Wednesday;
            case THURSDAY: return Thursday;
            case FRIDAY: return Friday;
            case SATURDAY: return Saturday;
            case SUNDAY: return Sunday;
            default: return null;
        }
    }
}
