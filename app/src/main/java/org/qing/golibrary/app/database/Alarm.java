package org.qing.golibrary.app.database;

import java.util.Date;
import java.util.HashMap;

public class Alarm {
    private int id;
    private int hour;
    private int mintue;
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

    public int getMintue() {
        return mintue;
    }

    public void setMintue(int mintue) {
        this.mintue = mintue;
    }
}
