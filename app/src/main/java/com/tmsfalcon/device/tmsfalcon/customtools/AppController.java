package com.tmsfalcon.device.tmsfalcon.customtools;

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
import com.androidnetworking.AndroidNetworking;
import com.downloader.PRDownloader;
import com.facebook.stetho.Stetho;
import com.tmsfalcon.device.tmsfalcon.widgets.PolygonPoints;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by Android on 7/4/2017.
 */

public class AppController extends Application {

    public static int driver_id ;

    public static int accident_report_id ;

    public static int first_time_login ;

    public static boolean data_load ; //for checking whether the offline database of locations needs to be uploaded or not

    public static String previous_lat = "0";

    public static String previous_long  = "0";

    public static final String TAG = AppController.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static final String ACCIDENT = "Accident";
    public static final String INCIDENT = "Incident";
    public static String strIncidentType = "Accident";


    public static  final String AccidentContainerName = "accident-reports";

    public static long[] total_upload; // for uploading files (progress bar purpose)
    @SuppressWarnings("unused")
    public static String token;

    private static AppController mInstance;

    public static Bitmap nowbitmap;

    public static Bitmap signatureAccident;
    public static String azureSigantureUrl;


    public static Uri nowUri;

    public final static Stack<PolygonPoints> allDraggedPointsStack = new Stack<>(); //for cropping points open cv lib

    public static Context appContext;

    public static boolean LOAD_ACCIDENT_DATA = false;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        nowbitmap = null;
        appContext = getApplicationContext();
        PRDownloader.initialize(getApplicationContext());
        // Adding an Network Interceptor for Debugging purpose :

        Stetho.initializeWithDefaults(this);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient() .newBuilder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
               // .addNetworkInterceptor(logging)
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        //AndroidNetworking.initialize(getApplicationContext());
        //new ANRWatchDog().start();

    }
    public static synchronized AppController getInstance() {
        if(mInstance == null){
            mInstance = new AppController();
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

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
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


    public static int math(float f) {
        int c = (int) ((f) + 0.5f);
        float n = f + 0.5f;
        return (n - c) % 2 == 0 ? (int) f : c;
    }
}