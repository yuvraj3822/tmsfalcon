package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.AccidentVehicleListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccidentVehicleDetails extends NavigationBaseActivity {

    EditText vehicle_insurance_provider_edittext,vehicle_insurance_no_edittext,vehicle_dot_number_edittext,vehicle_license_no_edittext,vehicle_license_state_edt,policy_holder_name_edt,policy_holder_lastname_edt;
    AppCompatCheckBox checkIsSameAsDriver;
    AlertDialog vehicleAlertDialog;
    EditText vehicle_year_edt,vehicle_make_edt,vehicle_owner_name_edt,
            vehicle_owner_lastname_edt,vehicle_owner_phoneno_edt,
            vehicle_owner_dob_edt,vehile_owner_city_edt,
            vehicle_owner_state_edt,vehicle_owner_zipcode_edt,edt_company_name,edt_company_phoneno;
    AccidentBasicDetails db;
    SessionManager sessionManager;
    ArrayList<AccidentVehicleDetailsModel> vehicleDetailArrayList;
    boolean isFormValid;
    CustomValidator customValidator;
    TextView error_vehicle_insurance_provider,error_vehicle_insurance_no,error_vehicle_license_no;
    boolean popUpOpened = false;
    AppCompatCheckBox is_towed_checkbox;
    LinearLayout tow_block;

    ScrollView scroll_view;
    NetworkValidator networkValidator;
    AccidentVehicleListAdapter vehicleListAdapter;
    JSONArray vehicle_json_array = new JSONArray();
    JSONObject driver_json = new JSONObject();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_accident_vehicle_details, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        initIds();
        checkInternet();

    }

    void initIds(){
        db = new AccidentBasicDetails(AccidentVehicleDetails.this);
        sessionManager = new SessionManager();
        customValidator = new CustomValidator(AccidentVehicleDetails.this);
        vehicleDetailArrayList = new ArrayList<>();
        networkValidator = new NetworkValidator(AccidentVehicleDetails.this);
    }

    public void checkInternet(){
        if(networkValidator.isNetworkConnected()){
            progressBar.setVisibility(View.VISIBLE);
            AndroidNetworking.get(UrlController.DRIVER_INFO)
                    .addHeaders("Token", sessionManager.get_token())
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("response"," is "+response);
                            boolean status = false;
                            JSONObject data_json = null;
                            try {
                                status = response.getBoolean("status");
                                data_json = response.getJSONObject("data");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(status){
                                try {
                                    data_json = response.getJSONObject("data");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if(data_json != null && data_json.length() > 0) {
                                    try {
                                        JSONObject truck_json_obj = data_json.getJSONObject("driver_truck_info");
                                        JSONObject driver_json_object = data_json.getJSONObject("driver_info");
                                        JSONArray trailer_json_array = data_json.getJSONArray("driver_trailer_info");

//                                        if(truck_json_obj != null && truck_json_obj.length() > 0) {
//                                            vehicle_json_array.put(truck_json_obj);
//                                        }
                                        if(driver_json_object != null && driver_json_object.length() > 0) {
                                            driver_json = driver_json_object;
                                        }

                                        if(trailer_json_array != null && trailer_json_array.length() > 0) {
                                            for (int i = 0; i < trailer_json_array.length(); i++) {
                                                JSONObject jEmpObj = trailer_json_array.getJSONObject(i);
                                                vehicle_json_array.put(jEmpObj);
                                            }
                                        }


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            else{

                            }
                            main_layout.setVisibility(View.VISIBLE);
                            bottom_linear.setVisibility(View.VISIBLE);
                            vehicleListAdapter = new AccidentVehicleListAdapter(AccidentVehicleDetails.this);
                            listView.setAdapter(vehicleListAdapter);
                        }
                        @Override
                        public void onError(ANError error) {
                            progressBar.setVisibility(View.GONE);
                            Log.e("error"," is "+error);
                            // handle error
                        }
                    });
        }
        else{
            Toast.makeText(AccidentVehicleDetails.this,getResources().getString(R.string.internet_not_connected),Toast.LENGTH_LONG).show();
        }
    }

    public boolean validateForm(){

        if(popUpOpened){
            if(!customValidator.setRequired(vehicle_insurance_provider_edittext.getText().toString())){

                error_vehicle_insurance_provider.setVisibility(View.VISIBLE);
                isFormValid = false;
            }
            else{
                error_vehicle_insurance_provider.setVisibility(View.GONE);
                isFormValid = true;
            }

            if(!customValidator.setRequired(vehicle_insurance_no_edittext.getText().toString())){

                error_vehicle_insurance_no.setVisibility(View.VISIBLE);
                isFormValid = false;
            }
            else{
                error_vehicle_insurance_no.setVisibility(View.GONE);
                isFormValid = true;
            }

            if(!customValidator.setRequired(vehicle_license_no_edittext.getText().toString())){

                error_vehicle_license_no.setVisibility(View.VISIBLE);
                isFormValid = false;
            }
            else{
                error_vehicle_license_no.setVisibility(View.GONE);
                isFormValid = true;
            }

        }
        return isFormValid;
    }


    public boolean validationEdtText(){

        //if (popUpOpened){
            if (vehicle_year_edt.getText().toString().trim().length()==0){

                Toast.makeText(this, "Please enter your vehicle year", Toast.LENGTH_SHORT).show();
                return false;
            }
        else if (vehicle_make_edt.getText().toString().trim().length()==0){
            Toast.makeText(this, "Please enter your vehicle make", Toast.LENGTH_SHORT).show();
            return false;
        }

       else if (vehicle_dot_number_edittext.getText().toString().trim().length()==0){
            Toast.makeText(this, "Please enter Dot No", Toast.LENGTH_SHORT).show();
            return false;
        }
       else if (vehicle_license_no_edittext.getText().toString().trim().length()==0){
            Toast.makeText(this, "Please enter license no", Toast.LENGTH_SHORT).show();
            return false;
        }
       else if (vehicle_license_state_edt.getText().toString().trim().length() == 0){
            Toast.makeText(this, "Please enter license state", Toast.LENGTH_SHORT).show();
            return false;
       }
       else if (vehicle_insurance_provider_edittext.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter insurance company", Toast.LENGTH_SHORT).show();
                return false;
       }
       else if (vehicle_insurance_no_edittext.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter insurance no", Toast.LENGTH_SHORT).show();
                return false;
       }
       else if (policy_holder_name_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter policy holder name", Toast.LENGTH_SHORT).show();
                return false;
       }else if (policy_holder_lastname_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter policy holder last name", Toast.LENGTH_SHORT).show();
                return false;
       }


//        vehicle_owner_name_edt = owner_first_name;
//        vehicle_owner_lastname_edt = owner_last_name;
//        vehicle_owner_phoneno_edt = owner_phone_no;
//        vehicle_owner_dob_edt = owner_dob;
//        vehile_owner_city_edt    =owner_city;
//        vehicle_owner_state_edt = owner_state;
//        vehicle_owner_zipcode_edt = owner_zipcode;




       else if (vehicle_owner_name_edt.getText().toString().trim().length()==0){
                Toast.makeText(this, "Please enter vehicle owner name", Toast.LENGTH_SHORT).show();
                return false;
            } else if (vehicle_owner_lastname_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter vehicle owner name", Toast.LENGTH_SHORT).show();
                return false;
            }
       else if (vehicle_owner_phoneno_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter vehicle owner phone no", Toast.LENGTH_SHORT).show();
                return false;
            }
       else if (vehicle_owner_dob_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter owner date of birth", Toast.LENGTH_SHORT).show();
                return false;
            } else if (vehile_owner_city_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter vehicle owner phone no", Toast.LENGTH_SHORT).show();
                return false;

            }
       else if (vehicle_owner_state_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter owner state", Toast.LENGTH_SHORT).show();
                return false;
            }

       else if (vehicle_owner_zipcode_edt.getText().toString().trim().length() == 0){
                Toast.makeText(this, "Please enter owner zipcode", Toast.LENGTH_SHORT).show();
                return false;
            }

       else if (is_towed_checkbox.isChecked()){

                if (edt_company_name.getText().toString().trim().length() == 0){
                    Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (edt_company_phoneno.getText().toString().trim().length() == 0){
                    Toast.makeText(this, "Please enter company phone no", Toast.LENGTH_SHORT).show();
                    return false;
                }else {
                    return  true;
                }

            }

       else {
                return true;
            }



    }

    void showVehicleDetailPopUp(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_accident_vehicle_detail, null);
        dialogBuilder.setView(dialogView);

        popUpOpened = true;

        is_towed_checkbox = dialogView.findViewById(R.id.is_towed_checkbox);
        scroll_view = dialogView.findViewById(R.id.scroll_view);
        tow_block = dialogView.findViewById(R.id.tow_block);

        ZoomLinearLayout zoomLinearLayout = dialogView.findViewById(R.id.zoom_linear_layout);
        zoomLinearLayout.init(AccidentVehicleDetails.this);

        final Spinner vehicle_unit_no_spinner = dialogView.findViewById(R.id.vehicle_unit_no);

        final EditText vehicle_year = dialogView.findViewById(R.id.vehicle_year);
        final EditText vehicle_make = dialogView.findViewById(R.id.vehicle_make);
        final EditText vehicle_dot_number = dialogView.findViewById(R.id.vehicle_dot_number);
        final EditText vehicle_license_no = dialogView.findViewById(R.id.vehicle_license_no);
        final EditText vehicle_license_state = dialogView.findViewById(R.id.vehicle_license_state);
        final EditText vehicle_insurance_company = dialogView.findViewById(R.id.automob_insurancename);
        final EditText vehicle_insurance_no = dialogView.findViewById(R.id.vehicle_insurance_no);

        final EditText policy_holder_first_name = dialogView.findViewById(R.id.policy_holder_phone_no);
        final EditText policy_holder_last_name = dialogView.findViewById(R.id.policy_holder_email);

        final EditText towing_company_name = dialogView.findViewById(R.id.towing_company_name);

        final EditText towing_company_phone = dialogView.findViewById(R.id.towing_company_phone);




//        final EditText owner_first_name = dialogView.findViewById(R.id.owner_first_name);
//        final EditText owner_last_name = dialogView.findViewById(R.id.owner_last_name);
//
//        final EditText owner_phone_no = dialogView.findViewById(R.id.owner_phone_no);
//
//        final EditText owner_dob = dialogView.findViewById(R.id.owner_dob);
//        final EditText owner_city = dialogView.findViewById(R.id.owner_city);
//
//        final EditText owner_state = dialogView.findViewById(R.id.owner_state);
//        final EditText owner_zipcode = dialogView.findViewById(R.id.owner_zipcode);






        edt_company_name = towing_company_name;
        edt_company_phoneno = towing_company_phone;

        policy_holder_name_edt = policy_holder_first_name;
        policy_holder_lastname_edt = policy_holder_last_name;
        vehicle_license_state_edt = vehicle_license_state;
        vehicle_dot_number_edittext  = vehicle_dot_number;
        vehicle_make_edt = vehicle_make;
        vehicle_year_edt =  vehicle_year;
        vehicle_license_no_edittext = vehicle_license_no;
        vehicle_insurance_provider_edittext = vehicle_insurance_company;
        vehicle_insurance_no_edittext = vehicle_insurance_no;

//        final Button select_trailer_no = dialogView.findViewById(R.id.select_trailer_no);
//        select_trailer_no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                    vehicle_unit_no_spinner.performClick();
//            }
//        });


        AppCompatCheckBox same_as_driver_checkbox = dialogView.findViewById(R.id.same_as_driver_checkbox);

        final EditText owner_first_name = dialogView.findViewById(R.id.owner_first_name);
        final EditText owner_last_name = dialogView.findViewById(R.id.owner_phone_no);
        final EditText owner_phone_no = dialogView.findViewById(R.id.owner_phone_no);
        final EditText owner_dob = dialogView.findViewById(R.id.owner_dob);
        final EditText owner_city = dialogView.findViewById(R.id.owner_city);
        final EditText owner_state = dialogView.findViewById(R.id.owner_state);
        final EditText owner_zipcode = dialogView.findViewById(R.id.owner_zipcode);

        checkIsSameAsDriver = same_as_driver_checkbox;


        vehicle_owner_name_edt = owner_first_name;
        vehicle_owner_lastname_edt = owner_last_name;
        vehicle_owner_phoneno_edt = owner_phone_no;
        vehicle_owner_dob_edt = owner_dob;
        vehile_owner_city_edt    =owner_city;
        vehicle_owner_state_edt = owner_state;
        vehicle_owner_zipcode_edt = owner_zipcode;

        same_as_driver_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    try {
                        String first_name = driver_json.getString("first_name");
                        String last_name = driver_json.getString("last_name");
                        String cell = driver_json.getString("cell");
                        String dob = driver_json.getString("dob");
                        String city = driver_json.getString("city");
                        String zip_code = driver_json.getString("zip_code");
                        String state = driver_json.getString("state");

                        owner_first_name.setText(first_name);
                        owner_last_name.setText(last_name);
                        owner_phone_no.setText(cell);
                        owner_dob.setText(dob);
                        owner_city.setText(city);
                        owner_state.setText(state);
                        owner_zipcode.setText(zip_code);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    owner_first_name.setText("");
                    owner_last_name.setText("");
                    owner_phone_no.setText("");
                    owner_dob.setText("");
                    owner_city.setText("");
                    owner_state.setText("");
                    owner_zipcode.setText("");
                }
            }
        });

        ArrayList<String> unit_no_array = new ArrayList<>();
        Log.e("size_of_array :",""+vehicle_json_array.length());
        for (int i = 0; i < vehicle_json_array.length(); i++) {
            try {
                JSONObject jsonObject = vehicle_json_array.getJSONObject(i);
                String unit_no = jsonObject.getString("unit_number");
                unit_no = "Trailer No:"+" "+unit_no;
                Log.e("unitNo :",""+unit_no);
                if (!unit_no.trim().equalsIgnoreCase("Trailer No:".trim()))
                unit_no_array.add(unit_no);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }

//        ArrayAdapter adapter = new ArrayAdapter(AccidentVehicleDetails.this, android.R.layout.simple_list_item_1,unit_no_array);
//        ArrayAdapter adapter = new ArrayAdapter(AccidentVehicleDetails.this,android.R.layout.simple_list_item_1,unit_no_array){
//            @Override
//            public View getDropDownView(int position, View convertView,
//                                        ViewGroup parent) {
//                View view = super.getDropDownView(position, convertView, parent);
//                TextView tv = (TextView) view;
//
//                // Set the Text color
//                tv.setTextColor(getResources().getColor(R.color.light_greyish));
//
//                return view;
//            }
//        };


        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,unit_no_array);

        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        vehicle_unit_no_spinner.setAdapter(adapter);

        vehicle_unit_no_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {

//                    TextView selectedText = (TextView) adapterView.getChildAt(0);
//                    if (selectedText != null) {
//                        selectedText.setTextColor(getResources().getColor(R.color.light_greyish));
//                    }
//


//                    select_trailer_no.setText(adapterView.getItemAtPosition(i).toString());



                    JSONObject jsonObject = (JSONObject) vehicle_json_array.get(i);
                    String year = jsonObject.getString("year");
                    String make = jsonObject.getString("make");
                    String license_plate = jsonObject.getString("license_plate_number");
                    String license_state = jsonObject.getString("registered_state");
                    String insurance_name = jsonObject.getString("insurance_name");
                    String insurance_policy_number = jsonObject.getString("insurance_policy_number");

                    vehicle_year.setText(year);
                    vehicle_make.setText(make);
                    vehicle_license_no.setText(license_plate);
                    vehicle_license_state.setText(license_state);
                    vehicle_insurance_company.setText(insurance_name);
                    vehicle_insurance_no.setText(insurance_policy_number);
                    String dot_no = driver_json.getString("us_dot");
                    if(dot_no != null){
                        vehicle_dot_number.setText(dot_no);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        is_towed_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){ //checked
                    scroll_view.post(new Runnable() {
                        @Override
                        public void run() {
                            scroll_view.smoothScrollTo(0,scroll_view.getBottom());
                        }
                    });
                    tow_block.setVisibility(View.VISIBLE);
                }
                else{
                    edt_company_name.setText("");
                    edt_company_phoneno.setText("");
                    tow_block.setVisibility(View.GONE);
                }
            }
        });

        /*vehicle_insurance_provider_edittext = dialogView.findViewById(R.id.vehicle_insurance_provider);
        vehicle_insurance_no_edittext = dialogView.findViewById(R.id.vehicle_insurance_no);
        vehicle_dot_number_edittext = dialogView.findViewById(R.id.vehicle_dot_number);
        vehicle_license_no_edittext = dialogView.findViewById(R.id.vehicle_license_no);
        vehicle_registration_no_edittext = dialogView.findViewById(R.id.vehicle_registration_no);

        vehicle_insurance_provider_edittext.addTextChangedListener(new GenericTextWatcher(vehicle_insurance_provider_edittext));
        vehicle_insurance_no_edittext.addTextChangedListener(new GenericTextWatcher(vehicle_insurance_no_edittext));
        vehicle_license_no_edittext.addTextChangedListener(new GenericTextWatcher(vehicle_license_no_edittext));

        error_vehicle_insurance_provider = dialogView.findViewById(R.id.error_vehicle_insurance_provider);
        error_vehicle_insurance_no = dialogView.findViewById(R.id.error_vehicle_insurance_no);
        error_vehicle_license_no = dialogView.findViewById(R.id.error_vehicle_license_no);

        vehicle_owner_name_edittext = dialogView.findViewById(R.id.vehicle_owner_name_edittext);
        vehicle_owner_contact_number_edittext = dialogView.findViewById(R.id.vehicle_owner_contact_number_edittext);
        vehicle_owner_insurance_provider_edittext = dialogView.findViewById(R.id.vehicle_owner_insurance_provider_edittext);
        vehicle_owner_insurance_policy_no_edittext = dialogView.findViewById(R.id.vehicle_owner_insurance_policy_no_edittext);
*/
        Button add_btn = dialogView.findViewById(R.id.add_vehicle_detail);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                validateForm();
//                validationEdtText();

                if(validationEdtText()){








                    /*LayoutInflater inflater = (LayoutInflater)getBaseContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = inflater.inflate(R.layout.vehicle_detail_item, null);

                    vehicle_detail_container.addView(view);

                    TextView insurance_provider_textview,insurance_policy_number_textview,dot_number_textview,vehicle_registration_textview,vehicle_license_no_textview;
                    ImageView cancel_icon;

                    TextView vehicle_owner_name_textview,vehicle_owner_phone_textview,vehicle_owner_insurance_provider_textview,vehicle_owner_insurance_policy_textview;

                    cancel_icon = view.findViewById(R.id.cancel_icon);
                    insurance_provider_textview = view.findViewById(R.id.insurance_provider_textview);
                    insurance_policy_number_textview = view.findViewById(R.id.insurance_policy_number_textview);
                    dot_number_textview = view.findViewById(R.id.dot_number_textview);
                    vehicle_registration_textview = view.findViewById(R.id.vehicle_registration_textview);
                    vehicle_license_no_textview = view.findViewById(R.id.vehicle_license_no_textview);

                    vehicle_owner_name_textview = view.findViewById(R.id.vehicle_owner_name);
                    vehicle_owner_phone_textview = view.findViewById(R.id.vehicle_owner_contact_number);
                    vehicle_owner_insurance_provider_textview = view.findViewById(R.id.vehicle_owner_insurance_provider_textview);
                    vehicle_owner_insurance_policy_textview = view.findViewById(R.id.vehicle_owner_insurance_policy_number_textview);


                    insurance_provider_textview.setText(vehicle_insurance_provider_edittext.getText().toString());
                    insurance_policy_number_textview.setText(vehicle_insurance_no_edittext.getText().toString());
                    dot_number_textview.setText(vehicle_dot_number_edittext.getText().toString());
                    vehicle_registration_textview.setText(vehicle_registration_no_edittext.getText().toString());
                    vehicle_license_no_textview.setText(vehicle_license_no_edittext.getText().toString());

                    vehicle_owner_name_textview.setText(vehicle_owner_name_edittext.getText().toString());
                    vehicle_owner_phone_textview.setText(vehicle_owner_contact_number_edittext.getText().toString());
                    vehicle_owner_insurance_provider_textview.setText(vehicle_owner_insurance_provider_edittext.getText().toString());
                    vehicle_owner_insurance_policy_textview.setText(vehicle_owner_insurance_policy_no_edittext.getText().toString());
*/
                   /* AccidentVehicleDetailsModel model = new AccidentVehicleDetailsModel();
                    model.setDriver_id(sessionManager.get_driver_id());
                    model.setVehicle_dot_number(vehicle_dot_number_edittext.getText().toString());
                    model.setVehicle_insurance_policy_number(vehicle_insurance_no_edittext.getText().toString());
                    model.setVehicle_insurance_provider(vehicle_insurance_provider_edittext.getText().toString());
                    model.setVehicle_license_number(vehicle_license_no_edittext.getText().toString());
                    model.setVehicle_registration_number(vehicle_registration_no_edittext.getText().toString());
                    model.setVehicle_owner_name(vehicle_owner_name_edittext.getText().toString());
                    model.setVehicle_owner_phone_number(vehicle_owner_contact_number_edittext.getText().toString());
                    model.setVehicle_owner_insurance_provider(vehicle_owner_insurance_provider_edittext.getText().toString());
                    model.setVehicle_owner_insurance_policy_number(vehicle_owner_insurance_policy_no_edittext.getText().toString());

                    vehicleDetailArrayList.add(model);*/

                   /* cancel_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vehicle_detail_container.removeView((View) view.getParent());
                        }
                    });*/

                }
            }
        });

        Button cancel_btn =  dialogView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehicleAlertDialog.dismiss();

            }
        });

        vehicleAlertDialog = dialogBuilder.create();
        //emailAlertDialog.setTitle("Send Documents By Email");
        if(vehicleAlertDialog != null){
            vehicleAlertDialog.show();
            vehicleAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        }

    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch(view.getId()){

                case R.id.vehicle_insurance_provider:
                    String str5 = vehicle_insurance_provider_edittext.getText().toString();
                    if(str5 != "" && str5.length() >0){
                        error_vehicle_insurance_provider.setVisibility(View.GONE);
                    }
                    else{
                        error_vehicle_insurance_provider.setVisibility(View.VISIBLE);
                    }

                    break;

                case R.id.vehicle_insurance_no:
                    String str6 = vehicle_insurance_no_edittext.getText().toString();
                    if(str6 != "" && str6.length() >0){
                        error_vehicle_insurance_no.setVisibility(View.GONE);
                    }
                    else{
                        error_vehicle_insurance_no.setVisibility(View.VISIBLE);
                    }

                    break;

                case R.id.vehicle_license_no:
                    String str7 = vehicle_insurance_no_edittext.getText().toString();
                    if(str7 != "" && str7.length() > 0){
                        error_vehicle_license_no.setVisibility(View.GONE);
                    }
                    else{
                        error_vehicle_license_no.setVisibility(View.VISIBLE);
                    }

                    break;

            }
        }
    }

    @Bind(R.id.list_view_vehicles)
    ListView listView;

    @Bind(R.id.main_layout)
    LinearLayout main_layout;

    @Bind(R.id.bottom_layout)
    LinearLayout bottom_linear;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @OnClick(R.id.next_btn)
    void nextActivity(){
        Intent i = new Intent(AccidentVehicleDetails.this, OtherPartyAccidentDetails.class);
        startActivity(i);
    }


    @OnClick(R.id.add_vehicle_detail)
    void callFunc(){
        showVehicleDetailPopUp();
    }
}
