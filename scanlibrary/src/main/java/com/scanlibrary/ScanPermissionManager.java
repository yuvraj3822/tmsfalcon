package com.scanlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Created by Android on 9/21/2017.
 */

public class ScanPermissionManager {

    public void askForPermission(Context context, Activity activity,String permission, Integer requestCode) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {

                    //This is called if user has denied the permission
                    //before
                    //In this case I am just asking the permission again
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);

                } else {

                    ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                }
            } else {
                Toast.makeText(context, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
            }
        }
        else{

        }
    }

    @SuppressWarnings("unused")
    public boolean checkPermission(Context context, Activity activity, String permission){
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }
}
