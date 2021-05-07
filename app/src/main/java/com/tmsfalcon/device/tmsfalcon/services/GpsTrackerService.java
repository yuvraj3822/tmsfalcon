package com.tmsfalcon.device.tmsfalcon.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.GpsApiCalls;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.database.Driver;
import com.tmsfalcon.device.tmsfalcon.entities.GpsLocationParams;

import java.util.Calendar;

import okhttp3.RequestBody;

public class GpsTrackerService extends Service {

    private static final String TAG = GpsTrackerService.class.getSimpleName();
    Boolean location_status = false;
    NetworkValidator networkValidator;
    String latitude,longitude,speed,speedInMiles,type,confidence,userActivity,altitude;
    Double speedInMilesDouble;
    int parsed_speed;

    public GpsTrackerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initIds();
        createNotificationChannel();
        startForegroundForLocation();


    }

    public void initIds(){
        networkValidator = new NetworkValidator(GpsTrackerService.this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.e("onStartCommand", "I am in Gps Service");
        startForegroundForLocation();

        location_status = SessionManager.getInstance().getKeyGpsLocationStatus();
        Log.e(TAG,"location_status "+SessionManager.getInstance().getKeyGpsLocationStatus());
        //location_status = true;
        if(location_status){

            if(networkValidator.isNetworkConnected()){
                AppController.data_load = false;

            }
            else{
                AppController.data_load = true;
            }
            if(Utils.isLocationEnabled(GpsTrackerService.this)){
                startLocationService();  // Start Location Service for Driver Tracking Purpose
                registerLocationReceiver();
            }
            else{
               /* AlertDialog.Builder builder = new AlertDialog.Builder(GpsTrackerService.this);
                builder.setTitle(R.string.gps_not_found_title);  // GPS not found
                builder.setMessage(R.string.gps_not_found_message); // Want to enable?
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
                builder.setNegativeButton(R.string.no, null);
                builder.create().show();*/

            }
        }
        else{

        }

        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.
        //return START_STICKY;

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        unregisterLocationReceiver();
        super.onDestroy();
    }

    public void startLocationService(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(new Intent(GpsTrackerService.this, LocationService.class));
        } else {
            startForegroundService(new Intent(GpsTrackerService.this, LocationService.class));
        }
    }

    public void registerLocationReceiver(){
        LocalBroadcastManager.getInstance(this).registerReceiver(
               broadcastReceiver, new IntentFilter(Utils.ACTION_LOCATION_BROADCAST)
        );
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Utils.ACTION_LOCATION_BROADCAST)){
                latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);
                speed = intent.getStringExtra(LocationService.EXTRA_SPEED);
                //Log.e("speed raw ",speed);

                /*speedInMilesDouble = Utils.convertMetreToMile(Float.parseFloat(speed));
                speedInMiles = Double.toString(speedInMilesDouble);*/

                parsed_speed = Utils.mps_to_kmph(Double.parseDouble(speed));

                altitude = intent.getStringExtra(LocationService.EXTRA_ALTITUDE );
            }

            /*SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD-HH:MM:SS");
            String currentDateandTime = sdf.format(new Date());*/

            String getLocalToUTCDate = Utils.getLocalToUTCDate(Calendar.getInstance().getTime());

            String currentDateandTime = Utils.getLocalToUTCDate(Calendar.getInstance().getTime());

            //Log.e("Data ","Lat => "+latitude+" Long => "+longitude+" Speed Raw => "+speed+" Speed => "+parsed_speed+" LocalToUTCDate => "+getLocalToUTCDate+
                   // " UTCToLocalDate => "+Utils.getUTCToLocalDate(getLocalToUTCDate) );
            double angle_double = angleFromCoordinate(Double.parseDouble(AppController.previous_lat),
                    Double.parseDouble(AppController.previous_long),
                    Double.parseDouble(latitude),
                    Double.parseDouble(longitude));
            String angle = String.valueOf(angle_double);
            String params = "batp=100|acc=1|";
            String loc_valid = "1";
            if (latitude != null && longitude != null) {
                if(AppController.data_load){
                   // Log.e("in","data load if "+AppController.data_load);
                    //save in db
                    Driver driver = new Driver(GpsTrackerService.this);
                    GpsLocationParams model = new GpsLocationParams();
                    model.setLat(latitude);
                    model.setLng(longitude);
                    model.setSpeed(""+parsed_speed);
                    model.setAngle(angle);
                    model.setLoc_valid(loc_valid);
                    model.setParams(params);
                    model.setAltitude(altitude);
                    model.setDt(currentDateandTime);

                    driver.addLocation(model);

                    driver.showLocations(driver);
                }
                else{
                   // Log.e("in","data load else "+AppController.data_load);
                    RequestBody body = GpsApiCalls.convertsingleLocation(currentDateandTime,latitude,longitude,altitude,angle,""+parsed_speed,loc_valid,params);
                    GpsApiCalls.sendLocation(GpsTrackerService.this,body,false);
                }

                AppController.previous_lat = latitude;
                AppController.previous_long = longitude;

            }
        }
    };

    public void unregisterLocationReceiver(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    public void startForegroundForLocation() {
        int NOTIFICATION_ID = (int) (System.currentTimeMillis() % 10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this, "my_gps_tracker_channel").build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_gps_tracker_service);
            String description = getString(R.string.channel_description_location_service);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_gps_tracker_channel", name, importance);
            channel.setDescription(description);
            channel.setSound(null,null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        // throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    private double angleFromCoordinate(double lat1, double long1, double lat2,
                                       double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return brng;
    }
}
