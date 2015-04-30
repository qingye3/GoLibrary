package org.qing.golibrary.app.database;

import java.util.Calendar;

/**
 * Enum type for days in a week. Should be used for the model.
 */
public enum DayInWeek {
    MONDAY("Mon", Calendar.MONDAY),
    TUESDAY("Tue", Calendar.TUESDAY),
    WEDNESDAY("Wed", Calendar.WEDNESDAY),
    THURSDAY("Thr", Calendar.THURSDAY),
    FRIDAY("Fri", Calendar.FRIDAY),
    SATURDAY("Sat", Calendar.SATURDAY),
    SUNDAY("Sun", Calendar.SUNDAY);

    private final String name;
    private final int dayOfWeek;

    DayInWeek(String name, int dayOfWeek){
        this.name = name;
        this.dayOfWeek = dayOfWeek;
    }

    public String toString(){
        return name;
    }

    public int dayOfWeek(){
        return dayOfWeek;
    }

}
