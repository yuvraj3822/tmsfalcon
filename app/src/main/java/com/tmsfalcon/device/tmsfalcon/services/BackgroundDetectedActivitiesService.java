package com.tmsfalcon.device.tmsfalcon.services;

/**
 * Created by Dell on 9/11/2018.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.broadcastReceivers.StartJobIntentServiceReceiver;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;

public class BackgroundDetectedActivitiesService extends Service {

    private static final String TAG = BackgroundDetectedActivitiesService.class.getSimpleName();
    private Intent mIntentService;
    private PendingIntent mPendingIntent;
    private ActivityRecognitionClient mActivityRecognitionClient;

    IBinder mBinder = new BackgroundDetectedActivitiesService.LocalBinder();

    public class LocalBinder extends Binder {
        public BackgroundDetectedActivitiesService getServerInstance() {
            return BackgroundDetectedActivitiesService.this;
        }
    }

    public BackgroundDetectedActivitiesService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mActivityRecognitionClient = new ActivityRecognitionClient(this);
        mIntentService = new Intent(this, DetectedActivityJobIntentService.class);
        mPendingIntent = PendingIntent.getBroadcast(this,
                1,
                StartJobIntentServiceReceiver.getIntent(BackgroundDetectedActivitiesService.this, mIntentService, 123),
                PendingIntent.FLAG_UPDATE_CURRENT);
        // mPendingIntent = PendingIntent.getService(this, 1, mIntentService, PendingIntent.FLAG_UPDATE_CURRENT);
        createNotificationChannel();
        startForegroundForLocation();
        requestActivityUpdatesButtonHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForegroundForLocation();
        return START_STICKY;
    }

    public void requestActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.requestActivityUpdates(
                Utils.DETECTION_INTERVAL_IN_MILLISECONDS,
                mPendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                /*Toast.makeText(getApplicationContext(),
                        "Successfully requested activity updates",
                        Toast.LENGTH_SHORT)
                        .show();*/
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                /*Toast.makeText(getApplicationContext(),
                        "Requesting activity updates failed to start",
                         Toast.LENGTH_SHORT)
                        .show();*/
            }
        });
    }

    public void startForegroundForLocation() {
        int NOTIFICATION_ID = (int) (System.currentTimeMillis() % 10000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(NOTIFICATION_ID, new Notification.Builder(this, "my_activity_recognition_channel").build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name_location_service);
            String description = getString(R.string.channel_description_location_service);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_activity_recognition_channel", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void removeActivityUpdatesButtonHandler() {
        Task<Void> task = mActivityRecognitionClient.removeActivityUpdates(
                mPendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                /*Toast.makeText(getApplicationContext(),
                        "Removed activity updates successfully!",
                        Toast.LENGTH_SHORT)
                        .show();*/
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               /* Toast.makeText(getApplicationContext(), "Failed to remove activity updates!",
                        Toast.LENGTH_SHORT).show();*/
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdatesButtonHandler();
    }
}