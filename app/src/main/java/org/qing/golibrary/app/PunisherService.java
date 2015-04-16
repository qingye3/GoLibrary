package org.qing.golibrary.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class PunisherService extends IntentService {
    private static final String TAG = "PunisherSer";

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
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("PUNISHMENT TIME")
                        .setSmallIcon(R.drawable.ic_action_accept)
                        .setSound(sound)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(1, builder.build());
    }
}
