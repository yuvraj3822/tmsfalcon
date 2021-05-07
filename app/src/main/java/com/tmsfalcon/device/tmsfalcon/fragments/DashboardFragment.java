package com.tmsfalcon.device.tmsfalcon.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.ExpiredDocumentActivity;
import com.tmsfalcon.device.tmsfalcon.activities.LoadsActivity;
import com.tmsfalcon.device.tmsfalcon.activities.UploadedDocumentsActivity;
import com.tmsfalcon.device.tmsfalcon.activities.directUploadModule.DirectUploadCameraScreen;
import com.tmsfalcon.device.tmsfalcon.activities.navigation.NavigationActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements View.OnClickListener {

    @SuppressWarnings("unused")
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    SessionManager session;
    NetworkValidator networkValidator;
    String TAG = this.getClass().getSimpleName();
    Context context;
    LinearLayout captureLayout,uploadLayout,loadLayout,navigateLayout;
    RelativeLayout callLayout;
    boolean is_flash_enabled = false;
    TextView bottom_flash_text;
    ImageView bottom_lights;
    private String cameraId;
    CameraManager manager = null;
    CameraCharacteristics characteristics;
    RelativeLayout lights_layout;
    private Camera camera;
    Camera.Parameters params;
    private boolean hasFlashCameraOne;
    DirectUploadTable db;
    String company_name,company_phone;
    String dispatcher_name = "";
    String dispatcher_phone = "";
    PermissionManager permissionsManager = new PermissionManager();
    private static final int CALL = 0x2;
    ProgressBar progressBar;
    int expired_document_count = 0;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        /*View view = inflater.inflate(R.layout.fragment_dashboard, container, false);*/
        View view = inflater.inflate(R.layout.demo_dashboard, container, false);
        ButterKnife.bind(DashboardFragment.this,view);
        initIds(view);

        driverNameText.setText("Hi "+session.get_driver_fullname()+" !!");
        companyNameText.setText("Welcome to "+session.get_company_name() +" !!");

        expired_document_count = SessionManager.getInstance().getExpiredDocCount();
        Log.e("session token ",""+session.get_token());
        setExpiredDocLayout();
        captureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),DirectUploadCameraScreen.class);
                getActivity().startActivity(i);
            }
        });
        uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),UploadedDocumentsActivity.class);
                getActivity().startActivity(i);
            }
        });
        callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!permissionsManager.checkPermission(getActivity(), getActivity(), Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(getActivity(),"Please Grant Permission to call",Toast.LENGTH_LONG).show();
                    permissionsManager.askForPermission(getActivity(), getActivity(), Manifest.permission.CALL_PHONE, CALL);
                }
                else{
                    fetchPhones();
                }

            }
        });
        loadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),LoadsActivity.class);
                getActivity().startActivity(i);
            }
        });
        navigateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),NavigationActivity.class);
                getActivity().startActivity(i);
            }
        });
        db = new DirectUploadTable(getActivity());
        db.deleteAllRecords();
        cameraSettings();
        return view;

    }

    private void setExpiredDocLayout(){
        if(expired_document_count > 0){
            String header_text = "";
            expiredDocHeader.setVisibility(View.VISIBLE);
            if(expired_document_count > 1){
                header_text = expired_document_count+" documents have been expired.";
            }
            else if(expired_document_count == 1){
                header_text = expired_document_count+" document has been expired.";
            }
            expiredDocText.setText(header_text);
        }
        else {
            expiredDocHeader.setVisibility(View.GONE);
        }
    }

    private void call_action(){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.call_layout);
        dialog.show();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.85f;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        TextView company_textview =  dialog.findViewById(R.id.company);
        TextView dispatcher_textview =  dialog.findViewById(R.id.dispatcher);
        company_textview.setText("Call "+company_name);
        dispatcher_textview.setText("Call "+dispatcher_name);
        LinearLayout call_company = dialog.findViewById(R.id.company_layout);
        LinearLayout call_dispatcher =  dialog.findViewById(R.id.dispatcher_layout);
        if(company_phone == "" || company_phone == null || company_phone == "null"){
            call_company.setVisibility(View.GONE);
        }
        if(dispatcher_phone == "" || dispatcher_phone == null || dispatcher_phone == "null"){
            call_dispatcher.setVisibility(View.GONE);
        }

        call_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("company_phone ",company_phone);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+company_phone));
                startActivity(intent);
            }
        });
        call_dispatcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("dispatcher_phone ",dispatcher_phone);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+dispatcher_phone));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        call_action();


                } else {
                    Toast.makeText(getActivity(),"You need Call Permission.",Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void fetchPhones(){
        String tag_json_obj = "phones";
        String url = UrlController.PHONES;
        showProgressBar();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                data_json = response.getJSONObject("data");
                                if(data_json != null){
                                    JSONObject company_json = data_json.getJSONObject("company");
                                    /*JSONObject dispatcher_json = data_json.getJSONObject("dispatcher");*/
                                    JSONObject dispatcher_json = data_json.optJSONObject("dispatcher");
                                    if(company_json != null){
                                        company_name = company_json.getString("company_name");
                                        company_phone = company_json.getString("phone_number");
                                    }

                                    if(dispatcher_json != null && dispatcher_json .length() > 0){
                                        dispatcher_name = dispatcher_json.getString("dispatcher_name");
                                        dispatcher_phone = dispatcher_json.getString("phone_number");
                                    }

                                }
                                hideProgressBar();
                                call_action();
                            }
                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

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

    public void cameraSettings() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);

            try {
                cameraId = manager.getCameraIdList()[0];
                characteristics = manager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                 Log.e("exception ", String.valueOf(e));
            }
        }
        else{
            hasFlashCameraOne = getActivity().getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if(!hasFlashCameraOne){
                if (camera == null) {
                    try {
                        camera = Camera.open();
                        params = camera.getParameters();
                    } catch (RuntimeException e) {
                        Log.e("Failed to Open. Error: ", e.getMessage());
                    }
                }
            }
        }

    }

    private void initIds(View view) {
        session = new SessionManager(context);
        networkValidator = new NetworkValidator(context);
        captureLayout = view.findViewById(R.id.capture_document_layout);
        uploadLayout = view.findViewById(R.id.upload_layout);
        loadLayout = view.findViewById(R.id.load_layout);
       // navigateLayout = view.findViewById(R.id.navigation_layout);
        callLayout = view.findViewById(R.id.call_layout);
        bottom_flash_text = view.findViewById(R.id.flash_text);
        bottom_lights = view.findViewById(R.id.lights);
        lights_layout = view.findViewById(R.id.lights_layout);
        lights_layout.setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        if(getActivity() != null){
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

    }

    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
        if(getActivity() != null){
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public boolean hasFlash(){
        boolean status = false;
        if(characteristics != null){
            Log.e("in",""+characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE));
            status = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        }
        return status;
    }


    protected void updatePreview() {
        if(is_flash_enabled){
            if(hasFlash()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        manager.setTorchMode(cameraId, true);
                    } catch (CameraAccessException e) {
                         Log.e("exception ", String.valueOf(e));
                    }
                }
            }
            else{
                Toast.makeText(getActivity(),getResources().getString(R.string.no_flash_available),Toast.LENGTH_LONG).show();
            }
        }
        else{
            if(hasFlash()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        manager.setTorchMode(cameraId, false);
                    } catch (CameraAccessException e) {
                         Log.e("exception ", String.valueOf(e));
                    }
                }
            }
        }

    }

    public void enableFlashlight(){
        is_flash_enabled = true;
        bottom_lights.setImageResource(R.drawable.headlight);
        bottom_flash_text.setText(getResources().getString(R.string.lights_on));

        if(Build.VERSION.SDK_INT > 21){
            updatePreview();
        }
        else{
            if(hasFlashCameraOne){
                params = camera.getParameters();
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                //camera.startPreview();
            }
            else{
                Toast.makeText(getActivity(),getResources().getString(R.string.no_flash_available),Toast.LENGTH_LONG).show();
            }
        }
    }

    public void disableFlashlight(){
        is_flash_enabled = false;
        bottom_lights.setImageResource(R.drawable.headlight_off);
        bottom_flash_text.setText(getResources().getString(R.string.lights_off));

        if(Build.VERSION.SDK_INT > 21){
            updatePreview();
        }
        else{
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.lights_layout:
                flashFunctionality();
                break;
        }
    }

    private void flashFunctionality() {
        if(is_flash_enabled){
            disableFlashlight();
        }
        else{
            enableFlashlight();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Bind(R.id.doc_count_header)
    RelativeLayout expiredDocHeader;

    @Bind(R.id.doc_count_text)
    TextView expiredDocText;

    @Bind(R.id.driver_name_text)
    TextView driverNameText;

    @Bind(R.id.company_name_text)
    TextView companyNameText;

    @OnClick(R.id.doc_count_header)
    void goToExpiredDoc(){
        Intent i = new Intent(context, ExpiredDocumentActivity.class);
        context.startActivity(i);
    }

}
