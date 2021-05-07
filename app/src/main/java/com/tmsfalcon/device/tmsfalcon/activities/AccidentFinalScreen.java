package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.ReportedAccidentListK;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails;
import com.tmsfalcon.device.tmsfalcon.database.AccidentCaptureImageTable;
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentBasicDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentRecordsRequest;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AccidentFinalScreen extends NavigationBaseActivity {

    NetworkValidator networkValidator;
    CustomValidator customValidator;
    SessionManager session;
    AccidentBasicDetails db;
    AccidentWitnessTable witnessTable;
    AccidentRecordsRequest accidentRecordsRequest;
    AccidentCaptureImageTable document_db;
    Map<String, File> document_hashmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_accident_final_screen, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        zoom_linear_layout.init(AccidentFinalScreen.this);
       /* if(networkValidator.isNetworkConnected()){
            upload_later_btn.setVisibility(View.GONE);
            upload_later_text.setVisibility(View.GONE);
        }
        else{
            upload_now_btn.setVisibility(View.VISIBLE);
        }*/
    }





    private void initIds(){
        networkValidator = new NetworkValidator(AccidentFinalScreen.this);
        customValidator = new CustomValidator(AccidentFinalScreen.this);
        session = new SessionManager(AccidentFinalScreen.this);
        db = new AccidentBasicDetails(AccidentFinalScreen.this);
        document_db = new AccidentCaptureImageTable(AccidentFinalScreen.this);
        witnessTable = new AccidentWitnessTable(AccidentFinalScreen.this);
        accidentRecordsRequest = new AccidentRecordsRequest();
    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("t" +
                "" +
                "" +
                "" +
                "ext/plain"), name);
    }


    private void sendData(){

        Log.e("in","sendData");

        AccidentRecordsRequest accidentRecordsRequest = new AccidentRecordsRequest();
        int accident_report_id = 0;
        ArrayList<AccidentWitnessModel> accidentWitnessModelArrayList = new ArrayList<>();
        ArrayList<AccidentVehicleDetailsModel> accidentVehicleDetailsModelArrayList = new ArrayList<>();

        Map<String, File> witness_audio_hashmap = null;

        JSONArray vehicle_json_array = new JSONArray();
        JSONArray witness_json_array = new JSONArray();
        JSONObject accident_json_object = new JSONObject();

        if(db.checkIfAccidentTableExists() && !db.checkAccidentBasicDetailEmptyTable()) {
            Log.e("in","checkIfAccidentTableExists");
            AccidentBasicDetailsModel accidentBasicDetailsModel = db.getAccidentBasicDetailById(AppController.accident_report_id);
            accidentRecordsRequest.setAccidentBasicDetailsModel(accidentBasicDetailsModel);

            try {
                accident_json_object.put("accident_date", (accidentBasicDetailsModel.getAccident_date()));
                accident_json_object.put("accident_time",(accidentBasicDetailsModel.getAccident_time()));
                accident_json_object.put("accident_location",(accidentBasicDetailsModel.getAccident_location()));
                accident_json_object.put("employer_name",(accidentBasicDetailsModel.getEmployer_name()));
                accident_json_object.put("employer_phone_number",(accidentBasicDetailsModel.getEmployer_phone_number()));
                accident_json_object.put("accident_lat",(accidentBasicDetailsModel.getAccident_lat()));
                accident_json_object.put("accident_lng",(accidentBasicDetailsModel.getAccident_long()));
                accident_json_object.put("employer_insurance_provider",(accidentBasicDetailsModel.getEmployer_insurance_provider()));
                accident_json_object.put("employer_insurance_policy_no",(accidentBasicDetailsModel.getEmployer_insurance_policy_number()));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            accident_report_id = accidentBasicDetailsModel.getId();

            if (db.checkIfVehicleTableExists() && !db.checkVehicleBasicDetailEmptyTable()) {
                Log.e("in","checkIfVehicleTableExists");
                accidentVehicleDetailsModelArrayList = db.getVehicleDetailById(accident_report_id);
                accidentRecordsRequest.setAccidentVehicleDetailsModelArrayList(accidentVehicleDetailsModelArrayList);

                for(AccidentVehicleDetailsModel model : accidentVehicleDetailsModelArrayList){

                    JSONObject vehicle_json_object = new JSONObject();

                    try {
                        vehicle_json_object.put("vehicle_insurance_provider",(model.getVehicle_insurance_provider()));
                        vehicle_json_object.put("vehicle_insurance_policy_number",(model.getVehicle_insurance_policy_number()));
                        vehicle_json_object.put("dot_number",(model.getVehicle_dot_number()));
                        vehicle_json_object.put("license_plate_number",(model.getVehicle_license_number()));
                        vehicle_json_object.put("vehicle_registration",(model.getVehicle_registration_number()));

                        vehicle_json_object.put("vo_name",(model.getVehicle_owner_name()));
                        vehicle_json_object.put("vo_phone_number",(model.getVehicle_owner_phone_number()));
                        vehicle_json_object.put("vo_insurance_provider",(model.getVehicle_owner_insurance_provider()));
                        vehicle_json_object.put("vo_insurance_policy_number",(model.getVehicle_owner_insurance_policy_number()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    vehicle_json_array.put(vehicle_json_object);
                    //vehicleDetails.add(vehicle_hashmap);
                }
            }

            if (witnessTable.checkIfTableExists() && !witnessTable.checkWitnessEmptyTable()) {
                Log.e("in","checkIfTableExists");
                accidentWitnessModelArrayList = witnessTable.getWitnessDetailById(accident_report_id);
                accidentRecordsRequest.setAccidentWitnessModelArrayList(accidentWitnessModelArrayList);
                for (AccidentWitnessModel model : accidentWitnessModelArrayList) {

                     JSONObject witness_json_object = new JSONObject();

                    try {
                        witness_json_object.put("witness_name",(model.getWitness_name()));
                        witness_json_object.put("witness_phone",(model.getWitness_phone()));
                        witness_json_object.put("witness_statement",(model.getWitness_statement()));

                        if (model.getWitness_audio_url() != null && model.getWitness_audio_url().length() >0) {
                            witness_audio_hashmap = new HashMap<>();
                            File file = new File(model.getWitness_audio_url());
                            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                                    .format(new Date());
                            witness_json_object.put("temp_name","witness-"+timeStamp);
                            witness_audio_hashmap.put("witness-"+timeStamp,file);

                        }
                        else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    witness_json_array.put(witness_json_object);

                }

            }
            Log.e("in","JSONObject");
            JSONObject data_json = new JSONObject();
            try {
                data_json.put("accident_details",accident_json_object);
                data_json.put("vehicle_details",vehicle_json_array);
                data_json.put("witness_details",witness_json_array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("data_json",data_json.toString());

            accident_progress_layout.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            photos_progress_bar.setProgress(0);
            final int finalAccident_report_id = accident_report_id;

            ANRequest.MultiPartBuilder getRequestBuilder = new ANRequest.MultiPartBuilder(UrlController.ACCIDENT_DETAIL);

            if(witness_audio_hashmap != null){
                Log.e("witness_audio_hashmap","not null");
                getRequestBuilder.addMultipartFile(witness_audio_hashmap);
            }
            else{
                Log.e("witness_audio_hashmap","null");
            }
            getRequestBuilder.addHeaders("Token", session.get_token());

            getRequestBuilder.addMultipartParameter("data",data_json.toString());

            ANRequest anRequest = getRequestBuilder.build();

            anRequest.setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            int percentage = (int) (bytesUploaded * 100 / totalBytes);

                           // int percentage = (int)(100 * bytesUploaded / totalBytes);
                            progressBar.setProgress(percentage);
                            progressBar.setText(""+percentage+" %");
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                           // accident_progress_layout.setVisibility(View.GONE);
                            Log.e("new response", " is "+String.valueOf(response));
                            try {
                                boolean status = response.getBoolean("status");
                                if(status){
                                    accident_data_status.setVisibility(View.VISIBLE);
                                    accident_data_status.setText("Uploaded");
                                    accident_data_status.setTextColor(getResources().getColor(R.color.green_dark));
                                    int server_accident_id = response.getInt("accident_id");

                                    if (document_db.checkIfTableExists() && !document_db.checkAccidentDocumentEmptyTable()) {
                                        sendDocuments(finalAccident_report_id,server_accident_id);

                                    }
                                    else{
                                        ArrayList<AccidentWitnessModel> witnessModelList = new ArrayList<>();
                                        witnessModelList = witnessTable.getWitnessDetailById(finalAccident_report_id);
                                        for(AccidentWitnessModel model : witnessModelList){
                                            String audio_url = model.getWitness_audio_url();
                                            if(audio_url != null ||audio_url != ""){

                                                File file = new File(audio_url);
                                                if(file.exists()){
                                                    file.delete();
                                                }
                                            }
                                        }

                                        // delete data from sqlite here
                                        AppController.accident_report_id = 0;
                                        db.delAccidentBasicById(String.valueOf(finalAccident_report_id));
                                        witnessTable.deleteWitnessById(finalAccident_report_id);
                                        db.delVehicleBasicById(String.valueOf(finalAccident_report_id));

                                    }

                                }
                                else{
                                    accident_data_status.setVisibility(View.VISIBLE);
                                    accident_data_status.setText("Failed");
                                    accident_data_status.setTextColor(getResources().getColor(R.color.red_dark));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            accident_progress_layout.setVisibility(View.GONE);
                            String error_body = anError.getErrorBody();
                            String msg = "";
                            if(error_body != null){
                                msg = error_body;
                            }
                            else{
                                msg = "Some Error Occured.Please Try Again Later!";
                            }
                            accident_data_status.setVisibility(View.VISIBLE);
                            accident_data_status.setText("Failed");
                            accident_data_status.setTextColor(getResources().getColor(R.color.red_dark));
                            Toast.makeText(AccidentFinalScreen.this,msg,Toast.LENGTH_LONG).show();
                            Log.e("new error"," is "+anError.getErrorBody());
                        }
                    });
        }

    }

    private void sendDocuments(final int accident_report_id, int server_accident_id){


        ArrayList<AccidentCaptureModel> accidentCaptureModelArrayList = new ArrayList<>();

        document_hashmap = new HashMap<>();

        JSONArray document_data_array = new JSONArray();

        if (document_db.checkIfTableExists() && !document_db.checkAccidentDocumentEmptyTable()) {

            accidentCaptureModelArrayList = document_db.getAllAccidentImagesById(accident_report_id);
            for (AccidentCaptureModel model : accidentCaptureModelArrayList) {

                JSONObject document_json_object = new JSONObject();

                try {
                    document_json_object.put("accident_id",server_accident_id);
                    document_json_object.put("doc_type",model.getDoc_type());


                    File file = new File(model.getImage_url());
                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                            .format(new Date());
                    document_json_object.put("temp_name","document-"+timeStamp);
                    document_hashmap.put("document-"+timeStamp,file);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                document_data_array.put(document_json_object);

            }

            photos_progress_layout.setVisibility(View.VISIBLE);
            photos_progress_bar.setProgress(0);

            ANRequest.MultiPartBuilder getRequestBuilder = new ANRequest.MultiPartBuilder(UrlController.ACCIDENT_DOCUMENTS);

            if(document_hashmap != null){
                getRequestBuilder.addMultipartFile(document_hashmap);
            }
            else{
                Log.e("witness_audio_hashmap","null");
            }
            getRequestBuilder.addHeaders("Token", session.get_token());

            if(document_data_array != null && document_data_array.length() > 0){
                getRequestBuilder.addMultipartParameter("data",document_data_array.toString());
            }
            else{
                getRequestBuilder.addMultipartParameter("data","");
            }

            ANRequest anRequest = getRequestBuilder.build();
            anRequest.setUploadProgressListener(new UploadProgressListener() {
                        @Override
                        public void onProgress(long bytesUploaded, long totalBytes) {
                             int percentage = (int) (bytesUploaded * 100 / totalBytes);
                           // int percentage = (int)(100 * bytesUploaded / totalBytes);
                            photos_progress_bar.setProgress(percentage);
                            photos_progress_bar.setText(""+percentage+" %");
                        }
                    })
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // accident_progress_layout.setVisibility(View.GONE);
                            Log.e("new response", " is "+String.valueOf(response));
                            try {
                                boolean status = response.getBoolean("status");
                                if(status){
                                    ArrayList<AccidentWitnessModel> witnessModelList = new ArrayList<>();
                                    witnessModelList = witnessTable.getWitnessDetailById(accident_report_id);
                                    for(AccidentWitnessModel model : witnessModelList){
                                        String audio_url = model.getWitness_audio_url();
                                        if(audio_url != null ||audio_url != ""){

                                            File file = new File(audio_url);
                                            if(file.exists()){
                                                file.delete();
                                            }
                                        }
                                    }
                                    ArrayList<AccidentCaptureModel> accidentCaptureModelArrayList = new ArrayList<>();
                                    accidentCaptureModelArrayList = document_db.getAllAccidentImagesById(accident_report_id);
                                    for (AccidentCaptureModel model : accidentCaptureModelArrayList) {
                                        String file_url = model.getImage_url();
                                        if(file_url != null ||file_url != ""){

                                            File file = new File(file_url);
                                            if(file.exists()){
                                                file.delete();
                                            }
                                        }
                                    }

                                    // delete data from sqlite here
                                    AppController.accident_report_id = 0;
                                    db.delAccidentBasicById(String.valueOf(accident_report_id));
                                    witnessTable.deleteWitnessById(accident_report_id);
                                    db.delVehicleBasicById(String.valueOf(accident_report_id));
                                    document_db.deleteImagesByAccidentId(String.valueOf(accident_report_id));

                                    photos_status.setVisibility(View.VISIBLE);
                                    photos_status.setText("Uploaded");
                                    photos_status.setTextColor(getResources().getColor(R.color.green_dark));

                                    reportStatusTextview.setVisibility(View.VISIBLE);
                                    reportStatusTextview.setTextColor(getResources().getColor(R.color.green_dark));
                                    reportStatusTextview.setText(getResources().getString(R.string.accident_report_uploaded_successfully));

                                    // delete data from sqlite here
                                    AppController.accident_report_id = 0;
                                  //  db.delAccidentBasicById(String.valueOf(accident_report_id));
                                   // db.delVehicleBasicById(String.valueOf(accident_report_id));
                                   // document_db.deleteImagesById(String.valueOf(accident_report_id));

                                }
                                else{
                                    photos_status.setVisibility(View.VISIBLE);
                                    photos_status.setText("Failed");
                                    photos_status.setTextColor(getResources().getColor(R.color.red_dark));


                                    reportStatusTextview.setVisibility(View.VISIBLE);
                                    reportStatusTextview.setTextColor(getResources().getColor(R.color.red_dark));
                                    reportStatusTextview.setText(getResources().getString(R.string.accident_report_upload_failed));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            photos_progress_layout.setVisibility(View.GONE);
                            photos_status.setVisibility(View.VISIBLE);
                            photos_status.setText("Failed");
                            photos_status.setTextColor(getResources().getColor(R.color.red_dark));

                            String error_body = anError.getErrorBody();
                            String msg = "";
                            if(error_body != null){
                                msg = error_body;
                            }
                            else{
                                msg = "Some Error Occured.Please Try Again Later!";
                            }
                            accident_data_status.setVisibility(View.VISIBLE);
                            accident_data_status.setText("Failed");
                            accident_data_status.setTextColor(getResources().getColor(R.color.red_dark));
                            Toast.makeText(AccidentFinalScreen.this,msg,Toast.LENGTH_LONG).show();
                            Log.e("new error"," is "+anError.getErrorBody());
                        }
                    });

        }


    }

    public static RequestBody audioToRequestBody(File file) { //for image file to request body
        return RequestBody.create(MediaType.parse("audio/*"),file);
    }

    public static RequestBody stringToRequestBody(String string){ // for string to request body
        return RequestBody.create(MediaType.parse("text/plain"),string);
    }

    @Bind(R.id.main_layout)
    ZoomLinearLayout zoom_linear_layout;

    @Bind(R.id.report_status_textview)
    TextView reportStatusTextview;

    @Bind(R.id.accident_progress_layout)
    LinearLayout accident_progress_layout;

    @Bind(R.id.progress_bar)
    public TextProgressBar progressBar;

    @Bind(R.id.photos_progress_layout)
    LinearLayout photos_progress_layout;

    @Bind(R.id.photos_progress_bar)
    public TextProgressBar photos_progress_bar;

    @Bind(R.id.accident_data_status)
    TextView accident_data_status;

    @Bind(R.id.photos_status)
    TextView photos_status;

    @Bind(R.id.upload_later)
    Button upload_later_btn;

    @Bind(R.id.upload_now)
    Button upload_now_btn;

    @Bind(R.id.upload_later_text)
    TextView upload_later_text;

    @OnClick(R.id.done_btn)
    void goToAccidentList(){
        Intent goTolist = new Intent(AccidentFinalScreen.this, ReportedAccidentListK.class);
        startActivity(goTolist);
    }

    @OnClick(R.id.upload_now)
    void uploadNow(){
        if(networkValidator.isNetworkConnected()){
            sendData();
            Log.e("in","upload now click");
           /* AccidentModuleCalls accidentModuleCalls = new AccidentModuleCalls();
            accidentModuleCalls.sendAccidentRequest(AccidentFinalScreen.this);*/
            //sendDataToServer();
        }
        else{
            reportStatusTextview.setText(getResources().getString(R.string.internet_not_connected));
            reportStatusTextview.setTextColor(getResources().getColor(R.color.red_dark));
            reportStatusTextview.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.upload_later)
    void uploadLater(){
        AppController.LOAD_ACCIDENT_DATA = true;
        String msg = "Data will be uploaded automatically whenever phone has internet connection.";
        //Toast.makeText(AccidentFinalScreen.this,msg,Toast.LENGTH_LONG).show();
        Intent goTolist = new Intent(AccidentFinalScreen.this,OfflineReportedAccident.class);
        startActivity(goTolist);
    }
}
