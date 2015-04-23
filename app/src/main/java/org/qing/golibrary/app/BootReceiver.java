package org.qing.golibrary.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.qing.golibrary.app.database.Alarm;
import org.qing.golibrary.app.database.AlarmDataSource;

import java.sql.SQLException;

/**
 * All alarms are cancelled when a device shutdown
 * The boot receiver class will restart all alarms at boot time
 */
public class BootReceiver extends BroadcastReceiver {
    static final String TAG = "BootReceiver";
    Context mContext;

    /**
     * Upon boot, schedule all alarm turned off by the shutdown
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            AlarmReceiver alarmReceiver = new AlarmReceiver();
            scheduleAllAlarm(alarmReceiver);
        }
    }

    /**
     * Helper function to schedule all alarm
     */
    private void scheduleAllAlarm(AlarmReceiver alarmReceiver) {
        AlarmDataSource alarmDataSource = new AlarmDataSource(mContext);
        try{
            alarmDataSource.open();
        } catch (SQLException e){
            Log.d(TAG, e.getMessage());
        }
        for (Alarm alarm : alarmDataSource.getAlarms()){
            alarmReceiver.setAlarm(mContext, alarm);
        }
        alarmDataSource.close();
    }
}
