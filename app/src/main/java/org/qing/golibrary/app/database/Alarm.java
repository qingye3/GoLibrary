package org.qing.golibrary.app.database;

import java.util.Date;
import java.util.HashMap;

/**
 * Model for alarm
 */
public class Alarm {
    private int id;
    private int hour;
    private int minute;
    private HashMap<DayInWeek, Boolean> repeatDays;
    private String description;
    private Date endDate;

    public Alarm() {
        repeatDays = new HashMap<DayInWeek, Boolean>();
        for (DayInWeek day : DayInWeek.values()){
            repeatDays.put(day, false);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isDayRepeat(DayInWeek day){
        return repeatDays.get(day);
    }

    public void setDayRepeat(DayInWeek day, Boolean isRepeat){
        repeatDays.put(day, isRepeat);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getRepeatString() {
        String retStr = "";
        for (DayInWeek day : DayInWeek.values()){
            if (isDayRepeat(day)){
                retStr += day.toString() + ". ";
            }
        }
        if (retStr.equals("")){
            retStr = "No repeats";
        }
        return retStr;
    }
}
