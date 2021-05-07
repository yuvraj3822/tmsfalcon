package com.tmsfalcon.device.tmsfalcon.customtools;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails;
import com.tmsfalcon.device.tmsfalcon.database.AccidentCaptureImageTable;
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentBasicDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AccidentModuleCalls {

    AccidentBasicDetails db;
    AccidentWitnessTable witnessTable;
    AccidentCaptureImageTable document_db;
    SessionManager session;
    Map<String, File> document_hashmap = null;
    Context context = null;

    public AccidentModuleCalls(Context context){
        this.context = context;
    }

    public void sendAccidentData(){

        db = new AccidentBasicDetails(context);
        document_db = new AccidentCaptureImageTable(context);
        witnessTable = new AccidentWitnessTable(context);
        session = new SessionManager();
        JSONObject data_json = new JSONObject();

        int accident_report_id = 0;
        ArrayList<AccidentWitnessModel> accidentWitnessModelArrayList = new ArrayList<>();
        ArrayList<AccidentVehicleDetailsModel> accidentVehicleDetailsModelArrayList = new ArrayList<>();

        Map<String, File> witness_audio_hashmap = null;

        JSONArray vehicle_json_array = new JSONArray();
        JSONArray witness_json_array = new JSONArray();
        JSONArray main_json_array = new JSONArray();

        if(db.checkIfAccidentTableExists() && !db.checkAccidentBasicDetailEmptyTable()) {

            ArrayList<AccidentBasicDetailsModel> arrayList = db.getAllAccidentBasicDetai();

            for(AccidentBasicDetailsModel accidentBasicDetailsModel : arrayList) {
                JSONObject accident_json_object = new JSONObject();
                try {
                    accident_json_object.put("accident_date", (accidentBasicDetailsModel.getAccident_date()));
                    accident_json_object.put("accident_time", (accidentBasicDetailsModel.getAccident_time()));
                    accident_json_object.put("accident_location", (accidentBasicDetailsModel.getAccident_location()));
                    accident_json_object.put("employer_name", (accidentBasicDetailsModel.getEmployer_name()));
                    accident_json_object.put("employer_phone_number", (accidentBasicDetailsModel.getEmployer_phone_number()));
                    accident_json_object.put("accident_lat", (accidentBasicDetailsModel.getAccident_lat()));
                    accident_json_object.put("accident_lng", (accidentBasicDetailsModel.getAccident_long()));
                    accident_json_object.put("employer_insurance_provider", (accidentBasicDetailsModel.getEmployer_insurance_provider()));
                    accident_json_object.put("employer_insurance_policy_no", (accidentBasicDetailsModel.getEmployer_insurance_policy_number()));
                    accident_report_id = accidentBasicDetailsModel.getId();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (db.checkIfVehicleTableExists() && !db.checkVehicleBasicDetailEmptyTable()) {

                    accidentVehicleDetailsModelArrayList = db.getVehicleDetailById(accident_report_id);

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

                if (witnessTable.checkIfTableExists() &&! witnessTable.checkWitnessEmptyTable()) {

                    accidentWitnessModelArrayList = witnessTable.getWitnessDetailById(accident_report_id);
                    for (AccidentWitnessModel model : accidentWitnessModelArrayList) {

                        JSONObject witness_json_object = new JSONObject();

                        try {
                            witness_json_object.put("witness_name",(model.getWitness_name()));
                            witness_json_object.put("witness_phone",(model.getWitness_phone()));
                            witness_json_object.put("witness_statement",(model.getWitness_statement()));


                            if (model.getWitness_audio_url() != null) {
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


                try {
                    data_json.put("accident_details",accident_json_object);
                    data_json.put("vehicle_details",vehicle_json_array);
                    data_json.put("witness_details",witness_json_array);
                    main_json_array.put(data_json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.e("data_json"," is "+data_json.toString());

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

            anRequest.getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // accident_progress_layout.setVisibility(View.GONE);
                Log.e("new response", " is "+String.valueOf(response));
                try {
                    boolean status = response.getBoolean("status");
                    JSONArray messagesList = response.optJSONArray("messages");
                    String messages = "";
                    for(int i = 0; i<messagesList.length();i++){
                        messages += messagesList.get(i);
                    }
                    if(status){

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

                    }
                    Log.e("messages",""+messages);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                String error_body = anError.getErrorBody();
                String msg = "";
                if(error_body != null){
                    msg = error_body;
                }
                else{
                    msg = "Some Error Occured.Please Try Again Later!";
                }
                Log.e("accident call","  ");
                Log.e("getErrorBody"," is "+anError.getErrorBody());
                Log.e("getErrorDetail"," is "+anError.getErrorDetail());
                Log.e("getErrorCode"," is "+anError.getErrorCode());
                Log.e("error"," is "+anError.getLocalizedMessage());
            }
        });
        }

    }

    private void sendDocuments(final int accident_report_id, int server_accident_id){

        db = new AccidentBasicDetails(context);
        document_db = new AccidentCaptureImageTable(context);
        witnessTable = new AccidentWitnessTable(context);

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


            anRequest.getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    // accident_progress_layout.setVisibility(View.GONE);
                    Log.e("new response", " is "+String.valueOf(response));
                    try {
                        boolean status = response.getBoolean("status");
                        if(status){

                            //delete audio file and images as well
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
                                        if(file.exists()){

                                            file.delete();
                                        }
                                    }


                                }

                            }


                            // delete data from sqlite here
                            AppController.accident_report_id = 0;
                            db.delAccidentBasicById(String.valueOf(accident_report_id));
                            witnessTable.deleteWitnessById(accident_report_id);
                            db.delVehicleBasicById(String.valueOf(accident_report_id));
                            document_db.deleteImagesByAccidentId(String.valueOf(accident_report_id));

                            AppController.LOAD_ACCIDENT_DATA = false;
                        }
                        else{

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {

                    String error_body = anError.getErrorBody();
                    String msg = "";
                    if(error_body != null){
                        msg = error_body;
                    }
                    else{
                        msg = "Some Error Occured.Please Try Again Later!";
                    }
                    Log.e("error",msg);
                    Log.e("accident doc call","  ");
                    Log.e("getErrorBody"," is "+anError.getErrorBody());
                    Log.e("getErrorDetail"," is "+anError.getErrorDetail());
                    Log.e("getErrorCode"," is "+anError.getErrorCode());
                }
            });

        }


    }

    public void sendAccidentDataById(int id){

        db = new AccidentBasicDetails(context);
        document_db = new AccidentCaptureImageTable(context);
        witnessTable = new AccidentWitnessTable(context);
        session = new SessionManager();
        JSONObject data_json = new JSONObject();

        int accident_report_id = 0;
        ArrayList<AccidentWitnessModel> accidentWitnessModelArrayList = new ArrayList<>();
        ArrayList<AccidentVehicleDetailsModel> accidentVehicleDetailsModelArrayList = new ArrayList<>();

        Map<String, File> witness_audio_hashmap = null;

        JSONArray vehicle_json_array = new JSONArray();
        JSONArray witness_json_array = new JSONArray();
        JSONArray main_json_array = new JSONArray();

        if(db.checkIfAccidentTableExists() && !db.checkAccidentBasicDetailEmptyTable()) {

            AccidentBasicDetailsModel accidentBasicDetailsModel = db.getAccidentBasicDetailById(id);

            JSONObject accident_json_object = new JSONObject();
            try {
                accident_json_object.put("accident_date", (accidentBasicDetailsModel.getAccident_date()));
                accident_json_object.put("accident_time", (accidentBasicDetailsModel.getAccident_time()));
                accident_json_object.put("accident_location", (accidentBasicDetailsModel.getAccident_location()));
                accident_json_object.put("employer_name", (accidentBasicDetailsModel.getEmployer_name()));
                accident_json_object.put("employer_phone_number", (accidentBasicDetailsModel.getEmployer_phone_number()));
                accident_json_object.put("accident_lat", (accidentBasicDetailsModel.getAccident_lat()));
                accident_json_object.put("accident_lng", (accidentBasicDetailsModel.getAccident_long()));
                accident_json_object.put("employer_insurance_provider", (accidentBasicDetailsModel.getEmployer_insurance_provider()));
                accident_json_object.put("employer_insurance_policy_no", (accidentBasicDetailsModel.getEmployer_insurance_policy_number()));
                accident_report_id = accidentBasicDetailsModel.getId();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (db.checkIfVehicleTableExists() && !db.checkVehicleBasicDetailEmptyTable()) {

                accidentVehicleDetailsModelArrayList = db.getVehicleDetailById(accident_report_id);

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

            if (witnessTable.checkIfTableExists() &&! witnessTable.checkWitnessEmptyTable()) {

                accidentWitnessModelArrayList = witnessTable.getWitnessDetailById(accident_report_id);
                for (AccidentWitnessModel model : accidentWitnessModelArrayList) {

                    JSONObject witness_json_object = new JSONObject();

                    try {
                        witness_json_object.put("witness_name",(model.getWitness_name()));
                        witness_json_object.put("witness_phone",(model.getWitness_phone()));
                        witness_json_object.put("witness_statement",(model.getWitness_statement()));


                        if (model.getWitness_audio_url() != null) {
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


            try {
                data_json.put("accident_details",accident_json_object);
                data_json.put("vehicle_details",vehicle_json_array);
                data_json.put("witness_details",witness_json_array);
                main_json_array.put(data_json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("data_json"," is "+data_json.toString());

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

            anRequest.getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    // accident_progress_layout.setVisibility(View.GONE);
                    Log.e("new response", " is "+String.valueOf(response));
                    try {
                        boolean status = response.getBoolean("status");
                        JSONArray messagesList = response.optJSONArray("messages");
                        String messages = "";
                        for(int i = 0; i<messagesList.length();i++){
                            messages += messagesList.get(i);
                        }
                        if(status){

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

                        }
                        Log.e("messages",""+messages);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    String error_body = anError.getErrorBody();
                    String msg = "";
                    if(error_body != null){
                        msg = error_body;
                    }
                    else{
                        msg = "Some Error Occured.Please Try Again Later!";
                    }
                    Log.e("accident call","  ");
                    Log.e("getErrorBody"," is "+anError.getErrorBody());
                    Log.e("getErrorDetail"," is "+anError.getErrorDetail());
                    Log.e("getErrorCode"," is "+anError.getErrorCode());
                    Log.e("error"," is "+anError.getLocalizedMessage());
                }
            });
        }
    }
}
