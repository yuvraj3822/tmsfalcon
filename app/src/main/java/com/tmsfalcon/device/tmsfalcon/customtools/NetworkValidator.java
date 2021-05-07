package com.tmsfalcon.device.tmsfalcon.customtools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;

/**
 * Created by Android on 7/3/2017.
 */

public class NetworkValidator {

    Context context;
    private static NetworkValidator instance;

    public NetworkValidator(Context c) {
        this.context = c;
    }

    public NetworkValidator(){

    }

    @SuppressWarnings("unused")
    public static NetworkValidator getInstance() {
        if (instance == null)
            instance = new NetworkValidator();

        return instance;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public void checkNetwork(){
        if(!isNetworkConnected()){
            Toast.makeText(context, context.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }


}
