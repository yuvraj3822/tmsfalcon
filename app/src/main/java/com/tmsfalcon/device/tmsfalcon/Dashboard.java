package com.tmsfalcon.device.tmsfalcon;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public class Dashboard extends NavigationBaseActivity{

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    SessionManager session;
    NetworkValidator networkValidator;

    int driver_id;

    String TAG = this.getClass().getSimpleName();
    String image_name,root,full_image_path;

    PermissionManager permissionsManager = new PermissionManager();
    static final Integer CALL = 0x2;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_dashboard, null, false);
        drawer.addView(contentView, 0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initIds();

        //getDeviceInfo();

        if(networkValidator.isNetworkConnected()){
            registerDeviceIdToServer();
        }
        else {
            Toast.makeText(Dashboard.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        //loadDriverImage();

        if (!permissionsManager.checkPermission(Dashboard.this, Dashboard.this, Manifest.permission.CALL_PHONE)) {
            permissionsManager.askForPermission(Dashboard.this, Dashboard.this, Manifest.permission.CALL_PHONE, CALL);
        }
    }


    @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("override method","dashboard");
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        //Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());
    }

    private void getDeviceInfo()  {
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT + 1].getName();

        String  details =  "VERSION.RELEASE : "+Build.VERSION.RELEASE
                +"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                +"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                +"\nCode name : "+osName
                +"\nBOARD : "+Build.BOARD
                +"\nBOOTLOADER : "+Build.BOOTLOADER
                +"\nBRAND : "+Build.BRAND
                +"\nCPU_ABI : "+Build.CPU_ABI
                +"\nCPU_ABI2 : "+Build.CPU_ABI2
                +"\nDISPLAY : "+Build.DISPLAY
                +"\nFINGERPRINT : "+Build.FINGERPRINT
                +"\nHARDWARE : "+Build.HARDWARE
                +"\nHOST : "+Build.HOST
                +"\nID : "+Build.ID
                +"\nDEVICE : "+Build.DEVICE
                +"\nMANUFACTURER : "+Build.MANUFACTURER
                +"\nMODEL : "+Build.MODEL
                +"\nPRODUCT : "+Build.PRODUCT
                +"\nSERIAL : "+Build.SERIAL
                +"\nTAGS : "+Build.TAGS
                +"\nTIME : "+Build.TIME
                +"\nTYPE : "+Build.TYPE
                +"\nUNKNOWN : "+Build.UNKNOWN
                +"\nUSER : "+Build.USER;
        Log.e("details",details);

    }

    private void registerDeviceIdToServer(){
        // Tag used to cancel the request

        String tag_json_obj = "register_device_id";
        String device_full_type = Build.BRAND+"-"+Build.MODEL;

        String url = UrlController.REGISTER_DEVICE_TOKEN;
        Field[] fields = Build.VERSION_CODES.class.getFields();
        String osName = fields[Build.VERSION.SDK_INT + 1].getName();
        Map<String, String> params = new HashMap<>();
        params.put("device_token", SessionManager.getInstance().getDeviceToken());
        params.put("device_type","android");
        params.put("device_version",Build.VERSION.RELEASE);
        params.put("device_version_name",osName);
        params.put("manufacturer",Build.MANUFACTURER);
        params.put("device_full_type",device_full_type);
        params.put("installed_mobile_app_version",BuildConfig.VERSION_NAME);
        
        Log.e("device token",""+SessionManager.getInstance().getDeviceToken());

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request
                .Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONArray json_messages ;
                        String messages = "";
                        int expired_count = 0;

                        try {
                            status = response.getBoolean("status");
                            json_messages = response.getJSONArray("messages");
                            expired_count = response.getInt("expired_count");
                            session.storeExpiredDocumentsCount(expired_count);
                            for(int i = 0 ; i < json_messages.length(); i++){
                                messages += json_messages.get(i)+"\n";
                            }

                           // Toast.makeText(Dashboard.this,messages,Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                       // Log.e("Response registerDeviceIdToServer", response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(Dashboard.this,error);
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("Token",session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void initIds() {

        session = new SessionManager(Dashboard.this);
        driver_id = session.get_driver_id();
        image_name = driver_id+"_thumb"+ ".jpg";
        root = Environment.getExternalStorageDirectory().toString();
        full_image_path = root+"/tmsfalcon/"+image_name;
        networkValidator = new NetworkValidator(Dashboard.this);

    }

    private void loadDriverImage() {

        if (ContextCompat.checkSelfPermission(Dashboard.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Dashboard.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        else{
            displayDriverPic();
            /*File img_file = new File(full_image_path);
            if(img_file.exists()){
                displayDriverThumb(img_file);
            }
            else{
                downloadDriverThumb();
            }*/
        }

    }


    private void downloadDriverThumb() {
        if(networkValidator.isNetworkConnected()){
            final ProgressDialog pDialog = new ProgressDialog(Dashboard.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            String image_url = UrlController.HOST+session.get_driver_thumb();
            imageLoader.get(image_url, new ImageLoader.ImageListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", String.valueOf(error));
                    Log.e(TAG, "Image Load Error: " + error.getMessage());
                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                    if (response.getBitmap() != null) {
                        // load image into imageview
                        Bitmap response_bitmap = response.getBitmap();
                        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File myDir = new File(root + "/tmsfalcon");
                        if (!myDir.exists()) {

                            myDir.mkdirs();
                        }
                        File file = new File (myDir, image_name);
                        if (file.exists ()) {
                            if(!file.delete()){
                                Log.e("Error ","File not deleted.");
                            }
                        }
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            response_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();

                            File img_file = new File(full_image_path);
                            displayDriverThumb(img_file);

                        } catch (Exception e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                    }
                    pDialog.hide();
                }
            });
        }
        else {
            Toast.makeText(Dashboard.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }


    private void displayDriverThumb(File imgFile) {
        /*final RotateAnimation rotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        rotate.setDuration(4000);
        rotate.setRepeatCount(Animation.INFINITE);
        header_image.setAnimation(rotate);*/
        Glide.with(Dashboard.this)
                .load(imgFile)
                .error(drawable)
                .into(header_image);
        /*Picasso.with(Dashboard.this)
                .load(imgFile)
                .error(drawable)
                //.placeholder( R.drawable.ic_progress_circles )
                .into(header_image, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                       // rotate.cancel();
                        //do smth when picture is loaded successfully
                    }
                    @Override
                    public void onError() {
                        //do smth when there is picture loading error
                    }
                });*/
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Dashboard.this.finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


}
