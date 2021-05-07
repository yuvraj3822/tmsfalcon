package com.tmsfalcon.device.tmsfalcon.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotAccidentVehicleDetails;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class AccidentBasicFormData extends NavigationBaseActivity {

    DatePickerDialog.OnDateSetListener incidentDateListener;
    TimePickerDialog.OnTimeSetListener incidentTimeListener;
    SessionManager sessionManager;
    boolean isFormValid;
    CustomValidator customValidator;
    List<String> accident_types_arrayList = new ArrayList<>();
    NetworkValidator networkValidator;
    Spinner accident_type_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.accident_details_basic_form_data, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        zoom_linear_layout.init(AccidentBasicFormData.this);
        accident_types_arrayList = Arrays.asList(getResources().getStringArray(R.array.accident_types));
        setListeners();
        String date_n = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        Log.e("current date",date_n);
        accident_date.setText(date_n);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());
        accident_time.setText(currentTime);
        Log.e("currentTime date",currentTime);
        if(sessionManager.getKeyCurrentLatitude() == null ||sessionManager.getKeyCurrentLatitude() == ""){
            getLastLocation();
        }
//        initliseSpinnerAndListener();
    }


    public void getLastLocation()   {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        Log.e("MapDemoActivity", "in location " + location);
                        if (location != null) {

                            sessionManager.storeCurrentLocation(Double.toString(location.getLatitude()),Double.toString(location.getLongitude()));


                            Geocoder gcd = new Geocoder(getBaseContext(),
                                    Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(location.getLatitude(),
                                        location.getLongitude(), 1);
                                if (addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String locality = addresses.get(0).getLocality();
                                    String subLocality = addresses.get(0).getSubLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();


                                    sessionManager.storeCurrentAddress(state,locality);
                                    sessionManager.storeCurrentStreetAddress(address);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

//    private void initliseSpinnerAndListener(){
//
//        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(AccidentBasicFormData.this,
//                android.R.layout.simple_spinner_dropdown_item, accident_types_arrayList);
//
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        accident_type_spinner.setAdapter(spinnerAdapter);
//
//
//        accident_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.e("selected_item",": "+adapterView.getSelectedItem());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//    }

    void initIds(){
        sessionManager = new SessionManager();
        customValidator = new CustomValidator(AccidentBasicFormData.this);
        networkValidator = new NetworkValidator(AccidentBasicFormData.this);

//        accident_type_spinner = findViewById(R.id.accident_type_spinner);
    }

    private void setListeners(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_spinner_dropdown_item, accident_types_arrayList);

        accident_type.setThreshold(2);
        accident_type.setAdapter(adapter);
        accident_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    accident_type.showDropDown();
            }
        });

        accident_type.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                accident_type.showDropDown();
                return false;
            }
        });

        incidentDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
                accident_date.setText(date);
            }
        };

        incidentTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                String startTime = String.format("%02d:%02d", hourOfDay, minute);
                //String startTime = hourOfDay+":"+minute;
                accident_time.setText(startTime);
            }
        };
        accident_date.addTextChangedListener(new GenericTextWatcher(accident_date));
        accident_time.addTextChangedListener(new GenericTextWatcher(accident_time));
        driver_employer_name.addTextChangedListener(new GenericTextWatcher(driver_employer_name));
        driver_employer_phone_no.addTextChangedListener(new GenericTextWatcher(driver_employer_phone_no));

    }

    public boolean validateForm(){

        if(!customValidator.setRequired(accident_date.getText().toString())){

            error_accident_date.setVisibility(View.VISIBLE);
            isFormValid = false;

        }
        else{
            error_accident_date.setVisibility(View.GONE);
            isFormValid = true;
        }
        if(!customValidator.setRequired(accident_time.getText().toString())){

            error_accident_time.setVisibility(View.VISIBLE);
            isFormValid = false;
        }
        else{
            error_accident_time.setVisibility(View.GONE);
            isFormValid = true;
        }
        if(!customValidator.setRequired(driver_employer_name.getText().toString())){

            error_employer_name.setVisibility(View.VISIBLE);
            isFormValid = false;
        }
        else{
            error_employer_name.setVisibility(View.GONE);
            isFormValid = true;
        }
        if(!customValidator.setRequired(driver_employer_phone_no.getText().toString())){

            error_employer_phone.setVisibility(View.VISIBLE);
            isFormValid = false;
        }
        else{
            error_employer_phone.setVisibility(View.GONE);
            isFormValid = true;
        }

        return isFormValid;
    }

    private void saveDataToDb(){
       // validateForm();
        if(isFormValid){
           /* AccidentBasicDetailsModel accidentBasicDetailsModel = new AccidentBasicDetailsModel();
            accidentBasicDetailsModel.setDriver_id(sessionManager.get_driver_id());
            accidentBasicDetailsModel.setAccident_date(accident_date.getText().toString());
            accidentBasicDetailsModel.setAccident_lat(sessionManager.getKeyCurrentLatitude());
            accidentBasicDetailsModel.setAccident_location(sessionManager.getKeyCurrentStreetAddress());
            accidentBasicDetailsModel.setAccident_long(sessionManager.getKeyCurrentLongitude());
            accidentBasicDetailsModel.setAccident_time(accident_time.getText().toString());
            accidentBasicDetailsModel.setEmployer_name(driver_employer_name.getText().toString());
            accidentBasicDetailsModel.setEmployer_phone_number(driver_employer_phone_no.getText().toString());
            accidentBasicDetailsModel.setEmployer_insurance_provider(driver_employer_insurance_provider.getText().toString());
            accidentBasicDetailsModel.setEmployer_insurance_policy_number(driver_employer_insurance_policy_number.getText().toString());

            long result = db.saveAccidentBasicDetails(accidentBasicDetailsModel);

            if(result != -1) {
                int accident_report_id = (int) result;
                AppController.accident_report_id = accident_report_id;

                for (AccidentVehicleDetailsModel accidentVehicleDetailsModel : vehicleDetailArrayList) {
                    accidentVehicleDetailsModel.setAccident_report_id(AppController.accident_report_id);
                    db.saveVehicleDetails(accidentVehicleDetailsModel);
                }
            }*/
            Intent i = new Intent(AccidentBasicFormData.this, ReportedAccidentScreenFive.class);
            startActivity(i);
        }
        Intent i = new Intent(AccidentBasicFormData.this, KotAccidentVehicleDetails.class);
        startActivity(i);
    }

    @Bind(R.id.event_type_radio_button)
    RadioGroup eventTypeRadioButton;

    @Bind(R.id.accident_radio_button)
    AppCompatRadioButton accidentRadioButton;

    @Bind(R.id.incident_radio_button)
    AppCompatRadioButton incidentRadioButton;

    @Bind(R.id.accident_date)
    EditText accident_date;

    @Bind(R.id.accident_time)
    EditText accident_time;

    @Bind(R.id.accident_description)
    EditText accident_description;

    @Bind(R.id.accident_type)
    AutoCompleteTextView accident_type;

    @Bind(R.id.is_private_property_checkbox)
    AppCompatCheckBox is_private_property_checkbox;

    @Bind(R.id.has_employer_checkbox)
    AppCompatCheckBox has_employer_checkbox;

    @Bind(R.id.driver_employer_block)
    LinearLayout driver_employer_block;

    @Bind(R.id.driver_employer_name)
    EditText driver_employer_name;

    @Bind(R.id.driver_employer_phone_no)
    EditText driver_employer_phone_no;

    @Bind(R.id.driver_employer_insurance_provider)
    EditText driver_employer_insurance_provider;

    @Bind(R.id.driver_employer_insurance_policy_number)
    EditText driver_employer_insurance_policy_number;

    @Bind(R.id.error_accident_date)
    TextView error_accident_date;

    @Bind(R.id.error_accident_time)
    TextView error_accident_time;

    @Bind(R.id.error_employer_name)
    TextView error_employer_name;

    @Bind(R.id.error_employer_phone)
    TextView error_employer_phone;

    @Bind(R.id.scroll_view)
    ScrollView scroll_view;

    @Bind(R.id.zoom_linear_layout)
    ZoomLinearLayout zoom_linear_layout;

    @OnClick(R.id.accident_time)
    void setincidentTime(){
        Calendar time = Calendar.getInstance();

        int minute = time.get(Calendar.MINUTE);
        //12 hour format
        int hour = time.get(Calendar.HOUR_OF_DAY);
        Log.e("hour",""+hour);
        Log.e("minute",""+minute);
        TimePickerDialog tpd = TimePickerDialog.newInstance(incidentTimeListener,time.HOUR_OF_DAY,time.MINUTE,true);
        tpd.setStartTime(hour,minute);
        tpd.show(AccidentBasicFormData.this.getFragmentManager(),"TimePickerDialog");
    }

    @OnCheckedChanged(R.id.has_employer_checkbox)
    void onRadioButtonCheckChanged(CompoundButton button, boolean checked){
        if(checked){
            driver_employer_block.setVisibility(View.VISIBLE);
            scroll_view.post(new Runnable() {
                @Override
                public void run() {
                    scroll_view.smoothScrollTo(0,scroll_view.getBottom());
                }
            });
        }
        else{
            driver_employer_block.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.next_btn)
    void goToNext() {
        saveDataToDb();

    }

    @OnClick(R.id.accident_date)
    void setPickUpDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                incidentDateListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(AccidentBasicFormData.this.getFragmentManager(), "DatePickerDialog");


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
                case R.id.accident_date:
                    String str1 = accident_date.getText().toString();
                    if(str1 != "" && str1.length() >0){
                        error_accident_date.setVisibility(View.GONE);
                    }
                    else{
                        error_accident_date.setVisibility(View.VISIBLE);
                    }

                    break;

                case R.id.accident_time:
                    String str2 = accident_time.getText().toString();
                    if(str2 != "" && str2.length() >0){
                        error_accident_time.setVisibility(View.GONE);
                    }
                    else{
                        error_accident_time.setVisibility(View.VISIBLE);
                    }

                    break;

                case R.id.driver_employer_name:
                    String str3 = driver_employer_name.getText().toString();
                    if(str3 != "" && str3.length() >0){
                        error_employer_name.setVisibility(View.GONE);
                    }
                    else{
                        error_employer_name.setVisibility(View.VISIBLE);
                    }

                    break;

                case R.id.driver_employer_phone_no:
                    String str4 = driver_employer_phone_no.getText().toString();
                    if(str4 != "" && str4.length() >0){
                        error_employer_phone.setVisibility(View.GONE);
                    }
                    else{
                        error_employer_phone.setVisibility(View.VISIBLE);
                    }

                    break;


            }
        }
    }
}
