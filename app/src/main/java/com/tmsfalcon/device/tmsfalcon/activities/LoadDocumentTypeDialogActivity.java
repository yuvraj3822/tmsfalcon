package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.directUploadModule.DirectUploadPreviewScreen;
import com.tmsfalcon.device.tmsfalcon.activities.directUploadModule.DirectUploadQueueScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadDocumentTypeDialogActivity extends AppCompatActivity {

    String selected_option = "";
    HashMap<String,String> data_hashmap;
    List<String> spinner_arraylist = new ArrayList();
    HashMap<String, String> hmap = new HashMap<String, String>();
    private static final int WRITE_EXTERNAL_STORAGE = 123;
    PermissionManager permissionsManager = new PermissionManager();
    ImageHelper imageHelper = new ImageHelper();
    DirectUploadTable documentsTable;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_load_document_type_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);

        documentsTable = new DirectUploadTable(LoadDocumentTypeDialogActivity.this);

        selected_option = getIntent().getStringExtra("selected_option");
        sessionManager = new SessionManager(LoadDocumentTypeDialogActivity.this);
        Log.e("selected_option",""+selected_option);
        loadDocuments();

    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoadDocumentTypeDialogActivity.this,DirectUploadPreviewScreen.class);
        startActivity(i);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void loadDocuments() {
        // Tag used to cancel the request
        String tag_load_document_types = "load_document_types";
        String url = UrlController.GET_DOCUMENT_TYPES;

        Map<String, String> params = new HashMap<>();
        String document_belong_to = selected_option.toLowerCase();
        params.put("document_belongs_to", document_belong_to);

        showProgessBar();
        Log.e("session ", SessionManager.getInstance().get_token()+" document_belong_to "+document_belong_to);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        JSONArray id_json_array;
                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            mainLayout.setVisibility(View.VISIBLE);
                            if(status){
                                data_json = response.getJSONObject("data");
                                id_json_array = response.getJSONArray("belongs_to_ids");
                                Iterator<?> keys = data_json.keys();
                                data_hashmap = new HashMap<>();
                                while(keys.hasNext() ) {
                                    String key = (String)keys.next();
                                    //Log.e("key ",""+key+ " value  "+data_json.get(key));
                                    data_hashmap.put(key,data_json.get(key).toString());

                                }
                                if(id_json_array != null && id_json_array.length() > 0){
                                    if(selected_option.toLowerCase().equals("driver")){
                                        for (int i = 0; i < id_json_array.length(); i++) {
                                            JSONObject jEmpObj = id_json_array.getJSONObject(i);
                                            String driver_uid = jEmpObj.getString("driver_uid");
                                            String first_name = jEmpObj.optString("first_name");
                                            String last_name = jEmpObj.optString("last_name");
                                            String driver_id = jEmpObj.optString("driver_id");
                                            //spinner_arraylist.add(driver_uid);
                                            spinner_arraylist.add(first_name+" "+last_name+" #"+driver_uid);
                                            hmap.put(first_name+" "+last_name+" #"+driver_uid,driver_id);

                                        }
                                    }
                                    else if(selected_option.toLowerCase().equals("truck")){
                                        for (int i = 0; i < id_json_array.length(); i++) {
                                            JSONObject jEmpObj = id_json_array.getJSONObject(i);
                                            String unit_number = jEmpObj.getString("unit_number");
                                            String make = jEmpObj.optString("make");
                                            String year = jEmpObj.optString("year");
                                            String company_truck_id = jEmpObj.optString("company_truck_id");
                                            //spinner_arraylist.add(company_truck_id);
                                            spinner_arraylist.add(make+"-"+unit_number+"-"+year);
                                            hmap.put(make+"-"+unit_number+"-"+year,company_truck_id);

                                        }
                                    }
                                    else if(selected_option.toLowerCase().equals("trailer")){
                                        for (int i = 0; i < id_json_array.length(); i++) {
                                            JSONObject jEmpObj = id_json_array.getJSONObject(i);
                                            String unit_number = jEmpObj.getString("unit_number");
                                            String make = jEmpObj.optString("make");
                                            String year = jEmpObj.optString("year");
                                            String company_trailer_id = jEmpObj.optString("company_trailer_id");
                                            //spinner_arraylist.add(company_trailer_id);
                                            spinner_arraylist.add(make+"-"+unit_number+"-"+year);
                                            hmap.put(make+"-"+unit_number+"-"+year,company_trailer_id);


                                        }
                                    }
                                    else if(selected_option.toLowerCase().equals("load")){
                                        for (int i = 0; i < id_json_array.length(); i++) {
                                            JSONObject jEmpObj = id_json_array.getJSONObject(i);
//                                            String load_id = jEmpObj.getString("load_id");
//                                            //spinner_arraylist.add(trip_id);
//                                            Log.e("load id ",": "+load_id);
//                                            spinner_arraylist.add("Id : "+load_id);
//                                            hmap.put("Id : "+load_id,load_id);

                                            setIdAndHashMap(jEmpObj);

                                        }
                                    }

                                }
                                else{
                                    String msg = "";
                                    /*
                                    if(selected_option.toLowerCase().equals("driver")){
                                        msg = "Data not Avaialble.Please select other option";
                                    }
                                    else if(selected_option.toLowerCase().equals("truck")){
                                        msg = "Truck not assigned.Please select different option other than Truck";
                                    }
                                    else if(selected_option.toLowerCase().equals("trailer")){
                                        msg = "Trailer not assigned.Please select different option other than Trailer";
                                    }
                                    else if(selected_option.toLowerCase().equals("load")){
                                        msg = "Load not assigned.Please select different option other than Load";
                                    }
                                    Toast.makeText(LoadDocumentTypeDialogActivity.this,msg,Toast.LENGTH_LONG).show();
                                    Intent nextScreen = new Intent(LoadDocumentTypeDialogActivity.this,BlankActivity.class);
                                    startActivity(nextScreen);
                                    */
                                }
                                Log.e("spinner_arraylist", ""+spinner_arraylist);

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(LoadDocumentTypeDialogActivity.this, android.R.layout.simple_spinner_dropdown_item, spinner_arraylist);

                                spinner.setAdapter(adapter);

                                for (Map.Entry<String, String> entry : data_hashmap.entrySet()) {
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    AppCompatRadioButton rdbtn = new AppCompatRadioButton(LoadDocumentTypeDialogActivity.this);
                                    // rdbtn.setId(Integer.parseInt(key));
                                    rdbtn.setText(value);
                                    //rdbtn.setButtonTintList(getResources().getColor(R.color.blue_logo));
                                    radioDocumentType.addView(rdbtn);

                                    /*

                                    //code to adjust dialog activity layout
                                    DisplayMetrics displaymetrics = new DisplayMetrics();
                                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                                    int width = (int) ((int)displaymetrics.widthPixels * 0.9);
                                    int height = (int) ((int)displaymetrics.heightPixels * 0.8);
                                    getWindow().setLayout(width,height);

                                    */


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

                ErrorHandler.setVolleyMessage(LoadDocumentTypeDialogActivity.this,error);
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
                headers.put("Token", SessionManager.getInstance().get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_load_document_types);
    }


    private void setIdAndHashMap(JSONObject jEmpObj){
        String load_id = null;
        try {
            String loadNo = "";
            load_id = jEmpObj.getString("load_id");
            if (jEmpObj.getString("trip_id")!= null){
                loadNo = jEmpObj.getString("trip_id");
            }else if(jEmpObj.getString("trip_number")!=null) {
                 loadNo = jEmpObj.getString("trip_number");
            } else {
                 loadNo = jEmpObj.getString("load_id");

            }

            Log.e("load id ",": "+load_id);
            spinner_arraylist.add("Id : "+loadNo);
            hmap.put("Id : "+loadNo,load_id);




        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.radioDocumentType)
    RadioGroup radioDocumentType;

    @Bind(R.id.select_id)
    Spinner spinner;

    @Bind(R.id.main_layout)
    RelativeLayout mainLayout;

    @OnClick(R.id.next_btn)
    void proceedFurther(){
         //documentsTable.deleteAllRecords();
        if(spinner != null && spinner.getSelectedItem() != null){
            String spinner_selected_id = spinner.getSelectedItem().toString();
            Log.e("spinner_selected_id",spinner_selected_id);
            String document_belong_to = selected_option.toLowerCase();
            String spinner_selected_value = hmap.get(spinner_selected_id);
            Log.e("spinner_selected_value",spinner_selected_value);
            int selectedRadioBtnId = radioDocumentType.getCheckedRadioButtonId(); //get selected radio button id
            if(selectedRadioBtnId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedRadioBtnId);
                String selectedRadioBtnText = selectedRadioButton.getText().toString();  //fetch text of selected radio button
                String document_type = getKeyByValue(data_hashmap,selectedRadioBtnText);

                //save in session manager
                sessionManager.storeDocumentInfoForDirectUpload(spinner_selected_value,document_belong_to,document_type);
                Intent nextScreen = new Intent(LoadDocumentTypeDialogActivity.this,DirectUploadQueueScreen.class);
                startActivity(nextScreen);
            }
            else{
                Toast.makeText(LoadDocumentTypeDialogActivity.this,"Please select Document Type first.",Toast.LENGTH_LONG).show();
            }

        }
        else{
            String document_belong_to = selected_option.toLowerCase();

            int selectedRadioBtnId = radioDocumentType.getCheckedRadioButtonId(); //get selected radio button id
            if(selectedRadioBtnId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedRadioBtnId);
                String selectedRadioBtnText = selectedRadioButton.getText().toString();  //fetch text of selected radio button
                String document_type = getKeyByValue(data_hashmap,selectedRadioBtnText);

                //save in session manager

                sessionManager.storeDocumentInfoForDirectUpload("",document_belong_to,document_type);
                Intent nextScreen = new Intent(LoadDocumentTypeDialogActivity.this,DirectUploadQueueScreen.class);
                startActivity(nextScreen);
            }
            else{
                Toast.makeText(LoadDocumentTypeDialogActivity.this,"Please select Document Type first.",Toast.LENGTH_LONG).show();
            }
        }

    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
