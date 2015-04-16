package org.qing.golibrary.app.database;

import junit.framework.TestCase;

import java.util.Calendar;

public class AlarmTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testSetAndGet() throws Exception {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        Alarm alarm = new Alarm();
        alarm.setId(5);
        alarm.setStartDate(c.getTime());
        alarm.setDescription("stuff");
        alarm.setEndDate(c.getTime());
        alarm.setDayRepeat(DayInWeek.FRIDAY, true);
        alarm.setHour(5);
        alarm.setMinute(10);

        assertEquals(alarm.getId(), 5);
        assertEquals(alarm.getStartDate(), c.getTime());
        assertEquals(alarm.getDescription(), "stuff");
        assertEquals(alarm.getEndDate(), c.getTime());
        assertEquals(alarm.isDayRepeat(DayInWeek.FRIDAY), true);
        assertEquals(alarm.isDayRepeat(DayInWeek.THURSDAY), false);
        assertEquals(alarm.getHour(), 5);
        assertEquals(alarm.getMinute(), 10);
    }
}