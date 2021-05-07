package com.tmsfalcon.device.tmsfalcon;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.tmsfalcon.device.tmsfalcon.Responses.ProfilePicUploadResponse;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.fragments.TruckBasicProfile;
import com.tmsfalcon.device.tmsfalcon.fragments.TruckDocumentList;
import com.tmsfalcon.device.tmsfalcon.fragments.TruckFaultCodesFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class TruckDetail extends NavigationBaseActivity {

    int truck_id;
    SessionManager session;
    NetworkValidator networkValidator;
    private TabLayout tabLayout;
    ImageView truck_thumb;
    JSONObject truckDetailJsonOject;
    TextView unitNo,make,model_year,currentLocation,weightCapacity;
    private static final int GALLERY = 1, CAMERA = 2;
    Bitmap profile_bitmap;
    Call<ProfilePicUploadResponse> call;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_truck_detail);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_truck_detail, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        truck_detail_main_block.setVisibility(View.GONE);
        Bundle bundle = getIntent().getExtras();
        truck_id = bundle.getInt("truck_id");
        Log.e("truck id",""+truck_id);
        initIds();
        if(networkValidator.isNetworkConnected()){
            getTruckDetail();
        }
        else {
            Toast.makeText(TruckDetail.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }

    }
    private void initIds() {
        networkValidator = new NetworkValidator(TruckDetail.this);
        session = new SessionManager(TruckDetail.this);
        tabLayout = findViewById(R.id.tabs);
        truck_thumb = findViewById(R.id.truck_image);
        unitNo = findViewById(R.id.unit_no);
        make = findViewById(R.id.make);
        //model_year = (TextView) findViewById(R.id.model_year);
        currentLocation = findViewById(R.id.current_location);
        weightCapacity = findViewById(R.id.weight_capacity);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        viewPager.setCurrentItem(0);

    }


    private void setupViewPager(ViewPager viewPager) {

        TruckBasicProfile truckBasicprofile = new TruckBasicProfile();
        Bundle truckBasicprofileBundle = new Bundle();
        truckBasicprofileBundle.putString("truck_detail", String.valueOf(truckDetailJsonOject));
        truckBasicprofile.setArguments(truckBasicprofileBundle);

        TruckDocumentList truckDocumentList = new TruckDocumentList();
        Bundle truckDocumentListBundle = new Bundle();
        truckDocumentListBundle.putString("truck_id", String.valueOf(truck_id));
        truckDocumentList.setArguments(truckDocumentListBundle);

        TruckFaultCodesFragment faultCodesFragment = new TruckFaultCodesFragment();
        Bundle faultCodesFragmentBundle = new Bundle();
        faultCodesFragmentBundle.putString("truck_id", String.valueOf(truck_id));
        faultCodesFragment.setArguments(faultCodesFragmentBundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(truckBasicprofile, "Basic Details");
        adapter.addFragment(truckDocumentList, "Documents");
        adapter.addFragment(faultCodesFragment, "Fault Codes");
        viewPager.setAdapter(adapter);

        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

   /* @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }*/


    private void getTruckDetail() {

        // Tag used to cancel the request
        String tag_json_obj = "truck_detail_tag";

        String url = UrlController.TRUCK;

        showProgessBar();

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(truck_id));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url+"/"+truck_id, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;

                        Log.e("Response ", response.toString());
                        try {
                            truck_detail_main_block.setVisibility(View.VISIBLE);
                            status = response.getBoolean("status");
                            if(status){
                                truckDetailJsonOject = response.getJSONObject("data");
                                String url = UrlController.HOST+truckDetailJsonOject.getString("thumb");
                                Log.e("truck thumb",url);
                                Glide.with(TruckDetail.this)
                                        .load(url)
                                        .override(200,200)
                                        .error(R.drawable.default_truck)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                                truckImageLayout.setVisibility(View.VISIBLE);
                                                imageProgressLayout.setVisibility(View.GONE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                                truckImageLayout.setVisibility(View.VISIBLE);
                                                imageProgressLayout.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(truck_thumb);
                                /*Picasso.with(TruckDetail.this).setLoggingEnabled(true);
                                Picasso.with(TruckDetail.this)
                                        .load(url)
                                        .resize(200,200)
                                        .centerCrop()
                                        .error(R.drawable.default_truck)
                                        .into(truck_thumb, new com.squareup.picasso.Callback() {
                                            @Override
                                            public void onSuccess() {
                                                truckImageLayout.setVisibility(View.VISIBLE);
                                                imageProgressLayout.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onError() {
                                                truckImageLayout.setVisibility(View.VISIBLE);
                                                imageProgressLayout.setVisibility(View.GONE);
                                            }
                                        });*/
                                unitNo.setText("#"+truckDetailJsonOject.getString("unit_number"));
                                if(truckDetailJsonOject.getString("model").length() > 0){
                                    make.setText(truckDetailJsonOject.getString("make")+","+truckDetailJsonOject.getString("model"));
                                }
                                else{
                                    make.setText(truckDetailJsonOject.getString("make"));
                                }
                                //make.setText(truckDetailJsonOject.getString("make"));
                                //model_year.setText(truckDetailJsonOject.getString("model")+","+truckDetailJsonOject.getString("year"));
                                currentLocation.setText(truckDetailJsonOject.getString("location_city")+","+truckDetailJsonOject.getString("location_state")+","+truckDetailJsonOject.getString("location_zipcode"));
                                weightCapacity.setText(truckDetailJsonOject.getString("gross_weight"));


                                setupViewPager(viewPager);
                                tabLayout.setupWithViewPager(viewPager);
                            }

                        } catch (JSONException e) {
                             Log.e("exception ", String.valueOf(e));
                        }
                        hideProgressBar();
                        truckImageLayout.setVisibility(View.GONE);
                        imageProgressLayout.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(TruckDetail.this,error);
                hideProgressBar();

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(TruckDetail.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( //Method of Fragment
                        new String[]{android.Manifest.permission.CAMERA},
                        CAMERA
                );
            }
            else {
                intent_camera_action();
            }
        } else {
            intent_camera_action();
        }

    }

    private void intent_camera_action(){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    profile_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    profile_bitmap = ImageHelper.resizeImageScaled(profile_bitmap);
                    upload_profile_pic();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(TruckDetail.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            profile_bitmap = (Bitmap) data.getExtras().get("data");
            profile_bitmap = ImageHelper.resizeImageScaled(profile_bitmap);
            upload_profile_pic();

        }
    }

    void upload_profile_pic() {
        if (networkValidator.isNetworkConnected()) {
            truckImageLayout.setVisibility(View.GONE);
            imageProgressLayout.setVisibility(View.VISIBLE);
            Map<String, RequestBody> postFields = new HashMap<>();
            postFields.put("resource_type", requestBody("COMPANY_TRUCK"));
            postFields.put("resource_id", requestBody(String.valueOf(truck_id)));

            final File file = ImageHelper.bitmapToFile(TruckDetail.this, profile_bitmap, "profilePic");
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part multipart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            //showProgessBar();
            Log.e("session token ", SessionManager.getInstance().get_token());
            call = RestClient.get().uploadResourceProfilePic(SessionManager.getInstance().get_token(), multipart,postFields);
            call.enqueue(new Callback<ProfilePicUploadResponse>() {
                @Override
                public void onResponse(Call<ProfilePicUploadResponse> call, retrofit2.Response<ProfilePicUploadResponse> response) {
                    Log.e("response ", ""+new Gson().toJson(response.body()));
                    if (response.body() != null || response.isSuccessful()) {
                        List messagesList = response.body().getMessages();
                        String messages = "";
                        for (int i = 0; i < messagesList.size(); i++) {
                            messages += messagesList.get(i);
                        }
                        if (response.body().getStatus()) {
                            //session.storeDriverThumb(response.body().getData().getFile_url());

                            String url = response.body().getData().getFile_url();
                            Log.e("url ",""+url);
                            Glide.with(TruckDetail.this)
                                    .load(url)
                                    .override(200,200)
                                    .error(R.drawable.default_truck)
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                            truckImageLayout.setVisibility(View.VISIBLE);
                                            imageProgressLayout.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                            truckImageLayout.setVisibility(View.VISIBLE);
                                            imageProgressLayout.setVisibility(View.GONE);
                                            return false;
                                        }
                                    })
                                    .into(truck_thumb);
                           /* Picasso.with(TruckDetail.this)
                                    .load(url)
                                    .error(R.drawable.default_truck)
                                    .into(truck_thumb, new com.squareup.picasso.Callback() {
                                        @Override
                                        public void onSuccess() {
                                            truckImageLayout.setVisibility(View.VISIBLE);
                                            imageProgressLayout.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onError() {
                                            truckImageLayout.setVisibility(View.VISIBLE);
                                            imageProgressLayout.setVisibility(View.GONE);
                                        }
                                    });*/

                        }
                        if(messages.length() > 0){
                            Toast.makeText(TruckDetail.this, "" + messages, Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        try {
                            String error_string = response.errorBody().string();
                            Log.e("error string "," is "+error_string);
                            ErrorHandler.setRestClientMessage(TruckDetail.this, error_string);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //hideProgressBar();
                }

                @Override
                public void onFailure(Call<ProfilePicUploadResponse> call, Throwable t) {
                    //hideProgressBar();
                    truckImageLayout.setVisibility(View.VISIBLE);
                    imageProgressLayout.setVisibility(View.GONE);
                    Log.e("server call error", t.getMessage());
                    Toast.makeText(TruckDetail.this, "Response Call Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else{
            Toast.makeText(TruckDetail.this, ""+getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /*@Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;
    @Bind(R.id.cart_badge)
    TextView cartBadgeTextView;*/
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.image_progress_layout)
    LinearLayout imageProgressLayout;
    @Bind(R.id.upload_profile_pic)
    ImageView uploadPicImageView;
    @Bind(R.id.truck_detail_main_block)
    LinearLayout truck_detail_main_block;
    @Bind(R.id.truck_image_layout)
    RelativeLayout truckImageLayout;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        performBackFunction();
    }

    public void performBackFunction(){
        Intent i = new Intent(TruckDetail.this, DashboardActivity.class);
        startActivity(i);
    }

   /* @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(TruckDetail.this, NotificationActivity.class);
        startActivity(i);
    }*/


    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
        performBackFunction();
    }

    @OnClick(R.id.upload_profile_pic)
    void upload_profile_popup(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(TruckDetail.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from Gallery",
                "Capture photo from Camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

   /* @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(TruckDetail.this);
    }*/


}
