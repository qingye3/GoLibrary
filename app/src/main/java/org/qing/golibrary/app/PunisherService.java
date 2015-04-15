package org.qing.golibrary.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PunisherService extends IntentService {
    private final String TAG = "PunisherSer";

    public PunisherService() {
        super("PunisherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "punisher service started");
        sendNotification("I am the punisher!");
        AlarmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                .setContentTitle("PUNISHMENT TIME")
                .setSmallIcon(R.drawable.ic_action_accept)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentText(message);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(1, builder.build());
    }
}
