package com.scanlibrary;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.sql.Array;


/**
 * Created by Android on 7/4/2017.
 */

public class ScanAppController extends Application {

    public static int driver_id ;

    public static int accident_report_id ;

    public static int first_time_login ;

    public static boolean data_load ; //for checking whether the offline database of locations needs to be uploaded or not

    public static String previous_lat = "0";

    public static String previous_long  = "0";

    public static final String TAG = ScanAppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static long[] total_upload; // for uploading files (progress bar purpose)
    @SuppressWarnings("unused")
    public static String token;

    private static ScanAppController mInstance;

    public static Bitmap nowbitmap;

    public static Uri nowUri;

    public static byte[] nowByteArray;

    public static boolean isCameraOpen;
    public static Context appContext;

    public static boolean LOAD_ACCIDENT_DATA = false;

    public static synchronized ScanAppController getInstance() {
        if(mInstance == null){
            mInstance = new ScanAppController();
        }
        return mInstance;
    }

    public static Context getAppContext(){
        return appContext;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }




    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        int socketTimeout = 30000; // 30 seconds. You can change it
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        req.setRetryPolicy(policy);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req)  {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    @SuppressWarnings("unused")
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}