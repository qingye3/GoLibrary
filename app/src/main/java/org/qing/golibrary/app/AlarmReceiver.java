package org.qing.golibrary.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

/**
 * An alarm receiver class. Also configure the alarm.
 * This is a wakeful receiver. The CPU is guaranteed to be awake until the wake lock is unlocked by the PunisherService
 */
public class AlarmReceiver extends WakefulBroadcastReceiver{
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    private final String TAG = "AlarmReceiver";

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received alarm");
        Intent service = new Intent(context, PunisherService.class);
        startWakefulService(context, service);
    }

    public void setAlarm(Context context, Calendar time){
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), alarmIntent);

        ComponentName receiver = new ComponentName(context, AlarmReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }
}
