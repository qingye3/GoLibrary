package org.qing.golibrary.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;
import org.qing.golibrary.app.database.Alarm;
import org.qing.golibrary.app.database.AlarmDataSource;
import org.qing.golibrary.app.database.DayInWeek;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class PunisherService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    public static final String PUNISHER_UPDATE = "org.qing.golibrary.app.PunisherService.UPDATE";

    private ArrayList<Location> libraryLocations;
    private static final String TAG = "PunisherSer";
    private GoogleApiClient mGoogleApiClient;
    private Intent mIntent;
    private LocationRequest mLocationRequest;
    private LocalBroadcastManager broadcastManager;
    private int alarmID;

    public PunisherService() {
        super("PunisherService");
    }


    /**
     * Upon handling the intent make a blocking call to get the current location
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "punisher service started");
        alarmID = intent.getIntExtra(AlarmReceiver.ALARM_ID, -1);
        reschedule();
        broadcastManager = LocalBroadcastManager.getInstance(this);

        createLocationRequest();
        createLibraryLocations();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mIntent = intent;
        mGoogleApiClient.blockingConnect();
    }

    private void reschedule() {
        AlarmDataSource datasource = new AlarmDataSource(this);
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (alarmID != -1){
            Alarm alarm = datasource.getAlarm(alarmID);
            if (!alarm.isRepeat()){
                datasource.removeAlarm(alarmID);
                return;
            }
            Calendar alarmCalendar = AlarmReceiver.getCalendarFromAlarm(alarm);
            for (int i = 0; i < 7; i++){
                alarmCalendar.add(Calendar.DATE, 1);
                if (alarm.isDayRepeat(calendarDayToMyDay(alarmCalendar.get(Calendar.DAY_OF_WEEK)))){
                    Date endTime = getFinalSecond(alarm.getEndDate());
                    if (endTime.after(alarmCalendar.getTime())){
                        setAlarmToCalendar(alarmCalendar, alarm);
                        datasource.updateAlarm(alarm);
                        AlarmReceiver.setAlarm(this, alarm);
                        return;
                    }
                }
            }
            datasource.removeAlarm(alarmID);
        }
    }

    private void setAlarmToCalendar(Calendar alarmCalendar, Alarm alarm) {
        alarm.setStartDate(alarmCalendar.getTime());
        alarm.setHour(alarmCalendar.get(Calendar.HOUR_OF_DAY));
        alarm.setMinute(alarmCalendar.get(Calendar.MINUTE));
    }


    private DayInWeek calendarDayToMyDay(int calendarDay){
        switch (calendarDay){
            case Calendar.MONDAY:
                return DayInWeek.MONDAY;
            case Calendar.TUESDAY:
                return DayInWeek.TUESDAY;
            case Calendar.WEDNESDAY:
                return DayInWeek.WEDNESDAY;
            case Calendar.THURSDAY:
                return DayInWeek.THURSDAY;
            case Calendar.FRIDAY:
                return DayInWeek.FRIDAY;
            case Calendar.SATURDAY:
                return DayInWeek.SATURDAY;
            case Calendar.SUNDAY:
                return DayInWeek.SUNDAY;
        }
        return null;
    }

    private Date getFinalSecond(Date endDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * Initialize the library locations
     */
    private void createLibraryLocations() {
        libraryLocations = new ArrayList<Location>();
        Location location = new Location("map");

        //Grainger Library
        location.setLatitude(40.112433);
        location.setLongitude(-88.22687);
        libraryLocations.add(location);

        //Main Library
        location = new Location("map");
        location.setLatitude(40.104784);
        location.setLongitude(-88.228699);
        libraryLocations.add(location);

        //Lincoln Hall
        location = new Location("map");
        location.setLatitude(40.106574);
        location.setLongitude(-88.228266);
        libraryLocations.add(location);

        //Siebel Center
        location = new Location("map");
        location.setLatitude(40.113877);
        location.setLongitude(-88.224886);
        libraryLocations.add(location);
    }

    /**
     * Check the location every 2 seconds
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Send a helpful message to the user if he is punished or not
     */
    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.ic_action_accept)
                        .setSound(sound)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setContentText(message);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(1, builder.build());
    }

    /**
     * implements the connection callback
     */
    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Handle location change by the fused location api
     * @param location the new location
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location!= null){
            location.setAltitude(0);
            //Reject a location if the accuracy is less than 25 meter
            if (location.getAccuracy() > 25){
                Log.i("LocationLat", String.valueOf(location.getLatitude()));
                Log.i("LocationLon", String.valueOf(location.getLongitude()));
                return;
            }
            Log.i("LocationLat", String.valueOf(location.getLatitude()));
            Log.i("LocationLon", String.valueOf(location.getLongitude()));

            //Calculate distance to the nearest library
            float minDist = Float.MAX_VALUE;
            for (Location libLocation : libraryLocations){
                if (libLocation.distanceTo(location) < minDist){
                    minDist = libLocation.distanceTo(location);
                }
            }

            if (minDist < 85){
                sendNotification("I will not punish you!", "Nearest library was " + minDist + " meters away.");
                reward();
            } else {
                sendNotification("It's punishment time!", "Nearest library was " + minDist + " meters away." +
                                          "You are punished because you failed to attended the study session");
                punish();
            }
        } else {
            Log.d(TAG, "Failed to get a location");
        }

        //We don't need location any more, turn off location service
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        //Release the lock so the receiver can go to sleep now
        AlarmReceiver.completeWakefulIntent(mIntent);
    }

    private void reward() {
        incrementStreak();
        if (getRewardStreak() >= 3){
            saveRewardStreak(0);
            incrementQuota();

            Intent intent = new Intent(PUNISHER_UPDATE);
            broadcastManager.sendBroadcast(intent);
        }
    }


    /**
     * Send the Facebook punishement message
     */
    private void punish() {
        AccessToken token = AccessToken.getCurrentAccessToken();
        JSONObject graphObj = new JSONObject();
        try {
            graphObj.put("message", getRandomPunishStatus());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        GraphRequest request = GraphRequest.newPostRequest(
                token,
                token.getUserId() + "/feed",
                graphObj,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        if (graphResponse != null && graphResponse.getJSONObject() != null){
                            Log.i("PunishResponse:", graphResponse.getJSONObject().toString());
                        }
                    }
                }
        );
        request.executeAsync();
    }


    /**
     *
     * @return a punish message randomly picked
     */
    private String getRandomPunishStatus(){
        ArrayList<String> statuses = new ArrayList<String>();
        statuses.add("I love Justin Biber!");
        statuses.add("I just kicked a five-year-old!");
        statuses.add("Dear Professor, please flunk me cuz I can't study");
        statuses.add("To those who reply to this status, I owe you 5 bucks.");
        Random rand = new Random();
        return statuses.get(rand.nextInt(statuses.size()));
    }

    private int getRewardStreak() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getInt(getString(R.string.CURRENT_STREAK), 0);
    }

    private void saveRewardStreak(int streak){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.CURRENT_STREAK), streak);
        editor.apply();
    }

    private void incrementStreak(){
        saveRewardStreak(getRewardStreak() + 1);
    }

    private int getNumOfQuotas() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPref.getInt(getString(R.string.NUM_OF_QUOTAS), 1);
    }

    private void saveNumOfQuotas(int numOfQuotas) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.NUM_OF_QUOTAS), numOfQuotas);
        editor.apply();
    }

    private void incrementQuota(){
        if (getNumOfQuotas() < 3){
            saveNumOfQuotas(getNumOfQuotas() + 1);
        }
    }
}
