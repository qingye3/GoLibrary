package org.qing.golibrary.app.database;

import android.test.AndroidTestCase;

import java.util.Calendar;

public class AlarmDataSourceTest extends AndroidTestCase{
    AlarmDataSource mAlarmDataSource;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mAlarmDataSource = new AlarmDataSource(getContext());
    }

    public void testClear() throws Exception {
        mAlarmDataSource.open();
        mAlarmDataSource.clear();
        assertEquals(mAlarmDataSource.getAlarms().size(), 0);
        mAlarmDataSource.close();
    }

    public void testCreateAndGetAlarm() throws Exception {
        mAlarmDataSource.open();
        Alarm alarm = new Alarm();
        alarm.setDayRepeat(DayInWeek.FRIDAY, true);
        alarm.setDayRepeat(DayInWeek.TUESDAY, true);
        alarm.setMinute(5);
        alarm.setHour(10);
        alarm.setStartDate(Calendar.getInstance().getTime());
        alarm.setEndDate(Calendar.getInstance().getTime());
        alarm.setDescription("HaHa");

        mAlarmDataSource.createAlarm(alarm);
        assertEquals(mAlarmDataSource.getAlarms().size(), 1);
        Alarm alarm2 = mAlarmDataSource.getAlarms().get(0);
        assertEquals(alarm2.getMinute(), alarm.getMinute());
        assertEquals(alarm2.getHour(), alarm.getHour());
        assertEquals(alarm2.getDescription(), alarm.getDescription());
        assertEquals(alarm2.isDayRepeat(DayInWeek.MONDAY), alarm.isDayRepeat(DayInWeek.MONDAY));
        assertEquals(alarm2.isDayRepeat(DayInWeek.TUESDAY), alarm.isDayRepeat(DayInWeek.TUESDAY));
        assertEquals(alarm2.isDayRepeat(DayInWeek.WEDNESDAY), alarm.isDayRepeat(DayInWeek.WEDNESDAY));
        assertEquals(alarm2.isDayRepeat(DayInWeek.THURSDAY), alarm.isDayRepeat(DayInWeek.THURSDAY));
        assertEquals(alarm2.isDayRepeat(DayInWeek.FRIDAY), alarm.isDayRepeat(DayInWeek.FRIDAY));
        assertEquals(alarm2.isDayRepeat(DayInWeek.SATURDAY), alarm.isDayRepeat(DayInWeek.SATURDAY));
        assertEquals(alarm2.isDayRepeat(DayInWeek.SUNDAY), alarm.isDayRepeat(DayInWeek.SUNDAY));

        mAlarmDataSource.removeAlarm(alarm2.getId());
        assertEquals(mAlarmDataSource.getAlarms().size(), 0);
        mAlarmDataSource.close();
    }
}