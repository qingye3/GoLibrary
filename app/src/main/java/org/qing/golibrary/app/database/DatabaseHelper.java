package org.qing.golibrary.app.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "go_library.db";
    private static final String TAG = "SQLiteHelper";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * on creating the helper, create the database schema
     *
     * @param db database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createAlarmTable =
                "create table alarms(" +
                        "alarm_id integer primary key autoincrement not null, " +
                        "hour int," +
                        "minute int," +
                        "description text," +
                        "repeat_mon boolean," +
                        "repeat_tue boolean," +
                        "repeat_wed boolean," +
                        "repeat_thr boolean," +
                        "repeat_fri boolean," +
                        "repeat_sat boolean," +
                        "repeat_sun boolean," +
                        "start_date text," +
                        "end_date text);";
        db.execSQL(createAlarmTable);

    }


    /**
     * on upgrading, destroy all old data
     *
     * @param db         database
     * @param oldVersion old version number, ignored
     * @param newVersion new version number, ignored
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version" + oldVersion + " to " + newVersion + "which destroy all old data");
        db.execSQL("drop table if exists alarms;");
        onCreate(db);
    }
}
