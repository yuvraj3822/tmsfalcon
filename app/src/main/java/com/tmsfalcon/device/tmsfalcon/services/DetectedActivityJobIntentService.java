package com.tmsfalcon.device.tmsfalcon.services;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;

import java.util.ArrayList;

/**
 * Created by Dell on 9/12/2018.
 */

public class DetectedActivityJobIntentService extends JobIntentService {

    public DetectedActivityJobIntentService() {
        Log.e("in","DetectedActivityJobIntentService");
    }

    protected static final String TAG = DetectedActivityJobIntentService.class.getSimpleName();

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e("in","onHandleWork");
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        for (DetectedActivity activity : detectedActivities) {
            Log.e("in","activity");
            Log.e(TAG, "Detected activity: " + activity.getType() + ", " + activity.getConfidence());
            //broadcastActivity(activity);
            broadcastLocationService(activity);
        }
    }


    private void broadcastActivity(DetectedActivity activity) {
        Intent intent = new Intent(Utils.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void broadcastLocationService(DetectedActivity activity) {
        Log.e("in","broadcastLocationService method");
        Intent intent = new Intent(Utils.BROADCAST_DETECTED_ACTIVITY_TO_SERVICE);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
