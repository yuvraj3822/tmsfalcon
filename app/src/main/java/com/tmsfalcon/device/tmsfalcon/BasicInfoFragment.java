package com.tmsfalcon.device.tmsfalcon;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.tmsfalcon.device.tmsfalcon.Responses.ProfilePicUploadResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BasicInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BasicInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BasicInfoFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    NetworkValidator networkValidator;
    String TAG = this.getClass().getSimpleName();
    SessionManager session;
    private Context context ;

    private String mParam1;
    private String mParam2;
    PermissionManager permissionsManager = new PermissionManager();
    TextView username_textview,gender_textview,dob_textview,email_textview,type_textview,ssn_textview,home_phone_textview,
    address_textview,mail_addressTextView,address_duration_textview,company_name_textview,company_address_textview,company_ein_textview,fullname_textview,
    emergency_contact_person_textview,emergency_contact_relation_textview,emergency_contact_cell_textview,dl_textview,dl_state_textview,
    dl_expiration_textview,dl_additional_textview;
    LinearLayout company_layout;
    CircleImageView driver_imageView;
    String image_name,root,full_image_path;
    int driver_id;
    TextView no_data_textview;
    ProgressBar progressBar;
    private static final int GALLERY = 1, CAMERA = 2;
    Bitmap profile_bitmap;
    Call<ProfilePicUploadResponse> call;
    Drawable drawable;

    private void initIds(View view) {

        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        username_textview = view.findViewById(R.id.username);
        gender_textview = view.findViewById(R.id.gender);
        dob_textview = view.findViewById(R.id.dob);
        email_textview = view.findViewById(R.id.email);
        type_textview = view.findViewById(R.id.type);
        //ssn_textview = (TextView) view.findViewById(R.id.ssn);
        home_phone_textview = view.findViewById(R.id.home_phone);
        address_textview = view.findViewById(R.id.address);

        mail_addressTextView = view.findViewById(R.id.mail_address);


        //address_duration_textview = (TextView) view.findViewById(R.id.address_duration);
        company_name_textview = view.findViewById(R.id.company_name);
        company_address_textview = view.findViewById(R.id.company_address);
        company_ein_textview = view.findViewById(R.id.company_ein);
        fullname_textview = view.findViewById(R.id.driver_name);
        emergency_contact_person_textview = view.findViewById(R.id.emergency_contact_person);
        emergency_contact_relation_textview = view.findViewById(R.id.emergency_contact_relation);
        emergency_contact_cell_textview = view.findViewById(R.id.emergency_contact_cell);
        dl_textview =  view.findViewById(R.id.dl);
        dl_expiration_textview = view.findViewById(R.id.dl_expiration);
        dl_state_textview = view.findViewById(R.id.dl_state);
        dl_additional_textview = view.findViewById(R.id.dl_additional);
        company_layout = view.findViewById(R.id.company_layout);
        driver_imageView = view.findViewById(R.id.profile_image);
        driver_id = session.get_driver_id();
        image_name = driver_id+"_thumb"+ ".jpg";
        root = Environment.getExternalStorageDirectory().toString();
        full_image_path = root+"/tmsfalcon/"+image_name;
        no_data_textview = view.findViewById(R.id.no_data_textview);
        progressBar = view.findViewById(R.id.progress_bar);
        /*int colorCodeDark = Color.parseColor("#071528");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));*/
    }

    private OnFragmentInteractionListener mListener;

    public BasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //this.context = context;
    }

    @Override
    @Deprecated
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BasicInfoFragment.
     */

    public static BasicInfoFragment newInstance(String param1, String param2) {
        BasicInfoFragment fragment = new BasicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_basic_info, container, false);
        ButterKnife.bind(BasicInfoFragment.this,view);
        initIds(view);
        if(session.getDriverGender() == "male"){
            drawable = getResources().getDrawable(R.drawable.driver_male);
        }
        else{
            drawable = getResources().getDrawable(R.drawable.driver_female);
        }
        if (getUserVisibleHint()) {
            if(networkValidator.isNetworkConnected()){

                getProfileAction();
                loadDriverImageWithUrl(session.get_driver_thumb());
            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
        return view;
    }

    private void loadDriverImageWithUrl(String url){
        driver_imageView.setVisibility(View.GONE);
        uploadProfilePic.setVisibility(View.GONE);
        imageProgressLayout.setVisibility(View.VISIBLE);
        Glide.with(getActivity())
                .load(url)
                .error(drawable)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        driver_imageView.setVisibility(View.VISIBLE);
                        uploadProfilePic.setVisibility(View.VISIBLE);
                        imageProgressLayout.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        driver_imageView.setVisibility(View.VISIBLE);
                        uploadProfilePic.setVisibility(View.VISIBLE);
                        imageProgressLayout.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(driver_imageView);
       /* Picasso.with(getActivity())
                .load(url)
                .error(drawable)
                .into(driver_imageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        driver_imageView.setVisibility(View.VISIBLE);
                        uploadProfilePic.setVisibility(View.VISIBLE);
                        imageProgressLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        driver_imageView.setVisibility(View.VISIBLE);
                        uploadProfilePic.setVisibility(View.VISIBLE);
                        imageProgressLayout.setVisibility(View.GONE);
                    }
                });*/
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            if(networkValidator.isNetworkConnected()){
                getProfileAction();

            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
    }

    private void loadDriverImage() {


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( //Method of Fragment
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            );
        }

        else{
            File img_file = new File(full_image_path);
            if(img_file.exists()){
                displayDriverThumb(img_file);
            }
            else{
                downloadDriverThumb();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    File img_file = new File(full_image_path);
                   /* if(img_file.exists()){
                        displayDriverThumb(img_file);
                    }
                    else{
                        Log.e(TAG,"image not exists");
                        downloadDriverThumb();
                    }*/

                } else {
                    Toast.makeText(context,"You need Storage Permission to Access Files.",Toast.LENGTH_LONG).show();
                }
                return;
            }
            case CAMERA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    intent_camera_action();

                } else {
                    Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    private void downloadDriverThumb() {
        if(networkValidator.isNetworkConnected()){
            /*final ProgressDialog pDialog = new ProgressDialog(context);
            pDialog.setMessage("Loading...");
            pDialog.show();*/
            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            String image_url = UrlController.HOST+session.get_driver_thumb();
            Log.e(TAG+" thumb ", image_url);
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
                        if (file.exists ()){
                            if(!file.delete()){
                                Log.e("Error ","File not deleted.");
                            }
                            //file.delete ();
                        }
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(file);
                            Log.e("try", String.valueOf(myDir));
                            response_bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();

                            File img_file = new File(full_image_path);
                            displayDriverThumb(img_file);

                        } catch (Exception e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        finally {
                            try {
                                if(out != null){
                                    out.close();
                                }
                            } catch (IOException e) {
                                Log.e("exception ", String.valueOf(e));
                            }
                        }

                    }
                    //pDialog.hide();
                }
            });
        }
        else {
            Toast.makeText(context,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    private void displayDriverThumb(File imgFile) {
        Glide.with(context)
                .load(imgFile)
                .error(drawable)
                .into(driver_imageView);
        /*Picasso.with(context)
                .load(imgFile)
                .error(drawable)
                .into(driver_imageView);*/
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void getProfileAction() {
        // Tag used to cancel the request
        String tag_json_obj = "basic_info_tag";

        String url = UrlController.PROFILE;

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        JSONObject basic_profile_json;
                        String mailingCity,mailingState,mailingZipcode,company_name,company_address,company_country,company_state,company_city,company_zip_code,
                                company_ein,username,first_name,middle_name,last_name,gender,dob,email,type,ssn,dl,dl_state,
                                dl_expiration,dl_additional,home_phone,address,strMailAddress,strMailCity,strMailState,strMailZipcode,country,state,city,zip_code,address_duration_years,
                                address_duration_months,emergency_contact_person,emergency_contact_relationship,emergency_contact_cell;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                data_json = response.getJSONObject("data");
                                basic_profile_json = data_json.getJSONObject("basic");
                                session.deleteProfileJsonArray("data");
                                session.createProfileJsonArrays("data",data_json.toString());
                                if(basic_profile_json != null && basic_profile_json.length() > 0){
                                    company_name = basic_profile_json.optString("company_name");
                                    company_address = basic_profile_json.optString("company_address");
                                    company_country = basic_profile_json.optString("company_country");
                                    company_state = basic_profile_json.optString("company_state");
                                    company_city = basic_profile_json.optString("company_city");
                                    company_zip_code = basic_profile_json.optString("company_zip_code");
                                    company_ein = basic_profile_json.optString("company_ein");

                                    username = basic_profile_json.optString("username");
                                    first_name = basic_profile_json.optString("first_name");
                                    middle_name = basic_profile_json.optString("middle_name");
                                    last_name = basic_profile_json.optString("last_name");
                                    gender = basic_profile_json.optString("gender");
                                    dob = basic_profile_json.optString("dob");
                                    email = basic_profile_json.optString("email");
                                    type = basic_profile_json.optString("type");
                                    //ssn = basic_profile_json.optString("ssn");
                                    dl = basic_profile_json.optString("dl");
                                    dl_state = basic_profile_json.optString("dl_state");
                                    dl_expiration = basic_profile_json.optString("dl_expiration");
                                    dl_additional = basic_profile_json.optString("dl_additional");
                                    home_phone = basic_profile_json.optString("home_phone");

                                    address = basic_profile_json.optString("address");
                                    strMailAddress = basic_profile_json.optString("mailing_address");



                                    strMailCity = basic_profile_json.optString("mailing_city");
                                    strMailState = basic_profile_json.optString("mailing_state");
                                    strMailZipcode = basic_profile_json.optString("mailing_zip_code");


                                    country = basic_profile_json.optString("country");
                                    state = basic_profile_json.optString("state");
                                    city = basic_profile_json.optString("city");

                                    zip_code = basic_profile_json.optString("zip_code");
                                    address_duration_years = basic_profile_json.optString("address_duration_years");
                                    address_duration_months = basic_profile_json.optString("address_duration_months");
                                    emergency_contact_person = basic_profile_json.optString("emergency_contact_person");

                                    emergency_contact_relationship = basic_profile_json.optString("emergency_contact_relationship");
                                    emergency_contact_cell = basic_profile_json.optString("emergency_contact_cell");

                                    CustomValidator.setCombinedStringData(fullname_textview," ",first_name,middle_name,last_name);
                                    CustomValidator.setCustomText(username_textview,username, (View) username_textview.getParent());
                                    CustomValidator.setCustomText(gender_textview,gender,(View) gender_textview.getParent());
                                    CustomValidator.setCustomText(dob_textview,dob,(View) dob_textview.getParent());
                                    CustomValidator.setCustomText(email_textview,email,(View) email_textview.getParent());
                                    CustomValidator.setCustomText(type_textview,type,(View) type_textview.getParent());
                                    //CustomValidator.setCustomText(ssn_textview,ssn,(View) ssn_textview.getParent());
                                    CustomValidator.setCustomText(home_phone_textview,home_phone,(View) home_phone_textview.getParent());



                                    CustomValidator.setAddress2(address_textview,address,city,state,zip_code);
//                                    CustomValidator.setAddressData(address_textview,", ",address,city,state,zip_code);


                                    if(strMailAddress!=null){
                                        if (strMailAddress.equalsIgnoreCase("null") || strMailAddress.equalsIgnoreCase("")){
//                                            CustomValidator.setAddressData(mail_addressTextView,", ",address,city,state,zip_code);
                                            CustomValidator.setAddress2(mail_addressTextView,address,city,state,zip_code);
                                        } else {
//                                            CustomValidator.setAddressData(mail_addressTextView,", ",strMailAddress,strMailCity,strMailState,strMailZipcode);
                                            CustomValidator.setAddress2(mail_addressTextView,strMailAddress,strMailCity,strMailState,strMailZipcode);
                                        }
                                    }else{
//                                        CustomValidator.setAddressData(mail_addressTextView,", ",address,city,state,zip_code);
                                        CustomValidator.setAddress2(mail_addressTextView,address,city,state,zip_code);
                                    }


                                    //address_duration_textview.setText(address_duration_years+" Years "+address_duration_months+" Months");
                                    CustomValidator.setCustomText(dl_textview,dl,(View) dl_textview.getParent());
                                    CustomValidator.setCustomText(dl_state_textview,dl_state,(View) dl_state_textview.getParent());
                                    CustomValidator.setCombinedStringData(dl_additional_textview,", ",dl_additional);
                                    CustomValidator.setCustomText(dl_expiration_textview,dl_expiration,(View) dl_expiration_textview.getParent());
                                    CustomValidator.setCustomText(emergency_contact_person_textview,emergency_contact_person,(View) emergency_contact_person_textview.getParent());
                                    CustomValidator.setCustomText(emergency_contact_relation_textview,emergency_contact_relationship,(View) emergency_contact_relation_textview.getParent());
                                    CustomValidator.setCustomText(emergency_contact_cell_textview,emergency_contact_cell,(View) emergency_contact_cell_textview.getParent());

                                    if(company_name.equals("")){
                                        company_layout.setVisibility(View.GONE);
                                    }
                                    else{
                                        CustomValidator.setStringData(company_name_textview,company_name);
                                        CustomValidator.setAddressData(company_address_textview,", ",company_address,company_city,company_state,company_zip_code);
                                        CustomValidator.setStringData(company_ein_textview,company_ein);
                                    }

                                }
                                else{
                                    no_data_textview.setVisibility(View.VISIBLE);
                                }

                            }
                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            ErrorHandler.setVolleyMessage(getActivity(),error);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( //Method of Fragment
                        new String[]{Manifest.permission.CAMERA},
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
        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    profile_bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentURI);
                    profile_bitmap = ImageHelper.resizeImageScaled(profile_bitmap);
                    upload_profile_pic();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
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

            final File file = ImageHelper.bitmapToFile(getActivity(), profile_bitmap, "profilePic");
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part multipart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            showProgessBar();
            Log.e("session token ", SessionManager.getInstance().get_token());
            call = RestClient.get().uploadProfilePic(SessionManager.getInstance().get_token(), multipart);
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
                            String url = response.body().getData().getFile_url();
                            session.storeDriverThumb(url);
                            loadDriverImageWithUrl(url);
                           // displayDriverThumb(file);

                        }
                        if(messages.length() > 0){
                            Toast.makeText(getActivity(), "" + messages, Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        try {
                            String error_string = response.errorBody().string();
                            ErrorHandler.setRestClientMessage(getActivity(), error_string);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    hideProgressBar();
                    progressBar.setProgress(0);
                }

                @Override
                public void onFailure(Call<ProfilePicUploadResponse> call, Throwable t) {
                    hideProgressBar();
                    progressBar.setProgress(0);
                    Log.e("server call error", t.getMessage());
                    Toast.makeText(getActivity(), "Response Call Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else{
            Toast.makeText(getActivity(), ""+getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    @Bind(R.id.image_progress_layout)
    LinearLayout imageProgressLayout;

    @Bind(R.id.upload_profile_pic)
    CircleImageView uploadProfilePic;

    @OnClick(R.id.upload_profile_pic)
    void upload_profile_popup(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
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
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
}
