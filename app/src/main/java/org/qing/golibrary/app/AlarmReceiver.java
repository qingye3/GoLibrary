package org.qing.golibrary.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import org.qing.golibrary.app.database.Alarm;

import java.util.Calendar;

/**
 * An alarm receiver class. Also configure the alarm.
 * This is a wakeful receiver. The CPU is guaranteed to be awake until the wake lock is unlocked by the PunisherService
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{

    private static final String TAG = "AlarmReceiver";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received alarm");
        Intent service = new Intent(context, PunisherService.class);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context, Alarm alarm){
        Log.d(TAG, "Setting alarm" + alarm.getId());

        Calendar calendar = getCalendarFromAlarm(alarm);

        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private Calendar getCalendarFromAlarm(Alarm alarm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(alarm.getStartDate());
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }

    public void cancelAlarm(Context context, Alarm alarm){
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.cancel(alarmIntent);
    }
}
