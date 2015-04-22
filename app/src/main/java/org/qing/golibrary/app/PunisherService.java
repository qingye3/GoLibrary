package org.qing.golibrary.app;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class PunisherService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{
    private ArrayList<Location> libraryLocations;
    private static final String TAG = "PunisherSer";
    private GoogleApiClient mGoogleApiClient;
    private Intent mIntent;
    private LocationRequest mLocationRequest;

    public PunisherService() {
        super("PunisherService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "punisher service started");
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

    private void createLibraryLocations() {
        libraryLocations = new ArrayList<Location>();
        Location location = new Location("map");

        //Grainger Library
        location.setLatitude(40.112433);
        location.setLongitude(-88.22687);
        libraryLocations.add(location);

        //Siebel Center
        location.setLatitude(40.113877);
        location.setLongitude(-88.224886);
        libraryLocations.add(location);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

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
            //Reject a location if the accuracy is less than 25 meter
            if (location.getAccuracy() > 25){
                return;
            }

            //Calculate distance to the nearest library
            float minDist = Float.MAX_VALUE;
            for (Location libLocation : libraryLocations){
                if (libLocation.distanceTo(location) < minDist){
                    minDist = libLocation.distanceTo(location);
                }
            }

            if (minDist < 85){
                sendNotification("I will not punish you!", "Nearest library is " + minDist + " meters away.");
            } else {
                sendNotification("You will be punished!", "Nearest library is " + minDist + " meters away.");
            }
        } else {
            Log.d(TAG, "Failed to get a location");
        }

        //We don't need location any more, turn off location service
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        //Release the lock so the receiver can go to sleep now
        AlarmReceiver.completeWakefulIntent(mIntent);
    }
}
