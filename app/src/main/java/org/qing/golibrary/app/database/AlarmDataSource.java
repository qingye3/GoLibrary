package org.qing.golibrary.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * The Datasource. All interaction with the database should be done through the datasource
 */
public class AlarmDataSource {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private static final String TABLENAME = "alarms";
    private static final String TAG = "AlarmDataSource";
    private static final String[] COLUMNS = {"alarm_id", "hour", "minute", "description", "repeat_mon",  "repeat_tue",  "repeat_wed",
            "repeat_thr",  "repeat_fri",  "repeat_sat",  "repeat_sun", "start_date", "end_date"};
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public AlarmDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException{
        database = dbHelper.getWritableDatabase();
    }

    public void clear(){
        database.delete(TABLENAME, null, null);
    }

    public void close(){
        dbHelper.close();
    }

    /**
     * Inserting an alarm into the database, id is auto incremented
     * @param alarm an alarm
     */
    public int createAlarm(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put("description", alarm.getDescription());
        values.put("hour", alarm.getHour());
        values.put("minute", alarm.getMinute());
        values.put("repeat_mon", alarm.isDayRepeat(DayInWeek.MONDAY));
        values.put("repeat_tue", alarm.isDayRepeat(DayInWeek.TUESDAY));
        values.put("repeat_wed", alarm.isDayRepeat(DayInWeek.WEDNESDAY));
        values.put("repeat_thr", alarm.isDayRepeat(DayInWeek.THURSDAY));
        values.put("repeat_fri", alarm.isDayRepeat(DayInWeek.FRIDAY));
        values.put("repeat_sat", alarm.isDayRepeat(DayInWeek.SATURDAY));
        values.put("repeat_sun", alarm.isDayRepeat(DayInWeek.SUNDAY));
        values.put("start_date", dateFormat.format(alarm.getStartDate()));
        values.put("end_date", dateFormat.format(alarm.getEndDate()));
        long id = database.insert(TABLENAME, null, values);  // don't the the null time hack lol
        return (int) id;
    }

    /**
     * return all alarms in the SQLite DB
     * @return all alarms in an ArrayList
     */
    public ArrayList<Alarm> getAlarms(){
        Cursor cursor = database.query(TABLENAME, COLUMNS, null,
                null, null, null, null);
        cursor.moveToFirst();
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();

        while(!cursor.isAfterLast()){
            Alarm alarm = new Alarm();
            alarm.setId(cursor.getInt(0));
            alarm.setHour(cursor.getInt(1));
            alarm.setMinute(cursor.getInt(2));
            alarm.setDescription(cursor.getString(3));
            alarm.setDayRepeat(DayInWeek.MONDAY, cursor.getInt(4) > 0);
            alarm.setDayRepeat(DayInWeek.TUESDAY, cursor.getInt(5) > 0);
            alarm.setDayRepeat(DayInWeek.WEDNESDAY, cursor.getInt(6) > 0);
            alarm.setDayRepeat(DayInWeek.THURSDAY, cursor.getInt(7) > 0);
            alarm.setDayRepeat(DayInWeek.FRIDAY, cursor.getInt(8) > 0);
            alarm.setDayRepeat(DayInWeek.SATURDAY, cursor.getInt(9) > 0);
            alarm.setDayRepeat(DayInWeek.SUNDAY, cursor.getInt(10) > 0);
            try {
                alarm.setStartDate(dateFormat.parse(cursor.getString(11)));
                alarm.setEndDate(dateFormat.parse(cursor.getString(12)));
            } catch (ParseException e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();
            }
            alarms.add(alarm);
            cursor.moveToNext();
        }
        cursor.close();
        return alarms;
    }

    /**
     * remove an alarm matching the id
     * @param id the id to match
     */
    public void removeAlarm(int id){
        database.delete(TABLENAME, "alarm_id" + "=" + String.valueOf(id), null);
    }

}
