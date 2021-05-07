package com.tmsfalcon.device.tmsfalcon.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;

import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by Dell on 9/6/2018.
 */

public class LocationService extends Service {

    NetworkValidator networkValidator;
    private static final String TAG = LocationService.class.getSimpleName();
    GoogleApiClient mLocationClient;
    SessionManager sessionManager;

    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_SPEED = "extra_speed";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_CONFIDENCE = "extra_confidence";
    public static final String EXTRA_ALTITUDE = "extra_altitude";

    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 1000 * 10;

    public LocationCallback locationCallback;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initIds();
        createNotificationChannel();
        startForegroundForLocation();
        initLocationCallback();
        sessionManager = new SessionManager(this);
        if(SessionManager.getInstance().getKeyGpsCurrentStatus() != null && SessionManager.getInstance().getKeyGpsCurrentStatus().equals("on_duty")){
            Log.e(TAG,"in  on duty");
            startLocationUpdates(SessionManager.getInstance().getKeyGpsOnDutyInterval());
        }
        else if(SessionManager.getInstance().getKeyGpsCurrentStatus() != null && SessionManager.getInstance().getKeyGpsCurrentStatus().equals("off_duty") ){
            Log.e(TAG,"in  off duty");
            startLocationUpdates(SessionManager.getInstance().getKeyGpsOffDutyInterval());
        }
        else{
            Log.e(TAG,"in custom else");
            startLocationUpdates(1000 *60 *2);
        }
    }

    public void initLocationCallback(){
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                onLocationChanged(locationResult.getLastLocation());
               /* for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                }*/
            };
        };
    }

    public void getLastLocation()   {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        Log.e("MapDemoActivity", "in location " + location);
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined

        /*if(SessionManager.getInstance().getKeyGpsCurrentStatus() != null && SessionManager.getInstance().getKeyGpsCurrentStatus().equals("on_duty")){
            Log.e(TAG,"in  on duty");
            mLocationRequest.setInterval(SessionManager.getInstance().getKeyGpsOnDutyInterval());
        }
        else if(SessionManager.getInstance().getKeyGpsCurrentStatus() != null && SessionManager.getInstance().getKeyGpsCurrentStatus().equals("off_duty") ){
            Log.e(TAG,"in  off duty");
            mLocationRequest.setInterval(SessionManager.getInstance().getKeyGpsOffDutyInterval());
        }
        else{
            Log.e(TAG,"in custom else");
            mLocationRequest.setInterval(1000 *60 *2);
        }*/
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
       // Log.e(TAG," "+msg);


        sessionManager.storeCurrentLocation(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));

        Geocoder gcd = new Geocoder(getBaseContext(),
                Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String locality = addresses.get(0).getLocality();
                String subLocality = addresses.get(0).getSubLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                /*if (subLocality != null) {

                    currentLocation = locality + "," + subLocality;
                } else {

                    currentLocation = locality;
                }
                current_locality = locality;*/

                sessionManager.storeCurrentAddress(state,locality);
                sessionManager.storeCurrentStreetAddress(address);


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        sendMessageToUI(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),String.valueOf(location.getSpeed()),String.valueOf(location.getAltitude()));

    }

    protected void startLocationUpdates(int interval) {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(interval*2);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest,
                locationCallback,
                Looper.myLooper());

    }

    public void stopLocationUpdates(){
        getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }


    public void initIds() {
        networkValidator = new NetworkValidator(LocationService.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("onStartCommand", "I am in Location Service");
        startForegroundForLocation();

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        return START_STICKY;

        //return super.onStartCommand(intent, flags, startId);
    }

    public void startForegroundForLocation() {
        int NOTIFICATION_ID = (int) (System.currentTimeMillis() % 10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this, "my_location_channel").build());
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("onBind", "I am in Location Service");
        return null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_location_service);
            String description = getString(R.string.channel_description_location_service);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_location_channel", name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendMessageToUI(String lat, String lng,String speed,String altitude) {

        //Log.e(TAG, "Sending info...");

        Intent intent = new Intent(Utils.ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        intent.putExtra(EXTRA_SPEED, speed);
        intent.putExtra(EXTRA_ALTITUDE, altitude);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
