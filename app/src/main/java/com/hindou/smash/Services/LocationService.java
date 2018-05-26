package com.hindou.smash.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hindou.smash.Controllers.BikerFragementActivity;
import com.hindou.smash.R;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final long INTERVAL = 1000 * 2;
    private static final long FASTEST_INTERVAL = 1000 * 1;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation, lStart, lEnd;
    static double distance = 0;
    static double speed;
    RemoteViews contentView;
    NotificationCompat.Builder mBuilder;
    NotificationManagerCompat notificationManager;
    Notification notification;


    private final IBinder mBinder = new localBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();


        return mBinder;
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConnected(Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException e) {
        }
    }


    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        distance = 0;
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;
        if (lStart == null) {
            lStart = mCurrentLocation;
            lEnd = mCurrentLocation;
        } else
            lEnd = mCurrentLocation;

        //calculating the speed with getSpeed method it returns speed in m/s so we are converting it into kmph
        speed = location.getSpeed()* 18 / 5;
        //Calling the method below updates the  live values of distance and speed to the TextViews.
        updateUI();
        contentView = new RemoteViews(getPackageName(), R.layout.notif_item_biker);

        contentView.setTextViewText(R.id.title, "Biker Data");
        contentView.setTextViewText(R.id.text,"distance: "+ String.valueOf(new DecimalFormat("#.###").format(distance) + " Km's."));

        mBuilder = new NotificationCompat.Builder(getBaseContext(),"notif02")
                .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                .setContent(contentView);
        notificationManager = NotificationManagerCompat.from(getBaseContext());
        notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(2, notification);


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    //The live feed of Distance and Speed are being set in the method below .
    private void updateUI() {
        if (BikerFragementActivity.p == 0) {

            distance = distance + (lStart.distanceTo(lEnd) / 1000.00);
            BikerFragementActivity.endTime = System.currentTimeMillis();
            long diff = BikerFragementActivity.endTime - BikerFragementActivity.startTime;
            Log.d("starttime",String.valueOf(BikerFragementActivity.startTime));
            Log.d("endtime",String.valueOf(BikerFragementActivity.endTime));
            diff = TimeUnit.MILLISECONDS.toMinutes(diff);
            BikerFragementActivity.time.setText(diff + " mins");
            Log.d("time",String.valueOf(diff));


           BikerFragementActivity.speed.setText(new DecimalFormat("#.##").format(speed) + " km/hr");

            Log.d("speed",String.valueOf(speed));
            Log.d("distance",String.valueOf(distance));
            BikerFragementActivity.distance.setText(new DecimalFormat("#.###").format(distance) + " Km's.");



            lStart = lEnd;

        }

    }


    @Override
    public boolean onUnbind(Intent intent) {
        stopLocationUpdates();
        if (mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();
        lStart = null;
        lEnd = null;
        distance = 0;
        return super.onUnbind(intent);
    }
    public class localBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }


    }


}
