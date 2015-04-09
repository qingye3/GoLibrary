package org.qing.golibrary.app.database;

/**
 * Enum type for days in a week. Should be used for the model.
 */
public enum DayInWeek {
    MONDAY("Mon"),
    TUESDAY("Tue"),
    WEDNESDAY("Wed"),
    THURSDAY("Thr"),
    FRIDAY("Fri"),
    SATURDAY("Sat"),
    SUNDAY("Sun");

    private final String name;

    DayInWeek(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }

}
