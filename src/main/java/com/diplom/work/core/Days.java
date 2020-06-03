package com.diplom.work.core;

import java.time.DayOfWeek;

public enum Days {
    MONDAY("Пн"),
    TUESDAY("Вт"),
    WEDNESDAY("Ср"),
    THURSDAY("Чт"),
    FRIDAY("Пт"),
    SATURDAY("Сб"),
    SUNDAY("Вс");

    private final String name;

    Days(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Days getByDayOfWeek(DayOfWeek dayOfWeek){
        switch (dayOfWeek){
            case MONDAY: return MONDAY;
            case TUESDAY: return TUESDAY;
            case WEDNESDAY: return WEDNESDAY;
            case THURSDAY: return THURSDAY;
            case FRIDAY: return FRIDAY;
            case SATURDAY: return SATURDAY;
            case SUNDAY: return SUNDAY;
            default: return null;
        }
    }
}
