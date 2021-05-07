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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentBasicDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ReportedAccidentScreenFourFormData extends NavigationBaseActivity {


    DatePickerDialog.OnDateSetListener incidentDateListener;
    TimePickerDialog.OnTimeSetListener incidentTimeListener;
    EditText vehicle_insurance_provider_edittext,vehicle_insurance_no_edittext,vehicle_dot_number_edittext,vehicle_license_no_edittext;
    EditText vehicle_registration_no_edittext;
    AlertDialog vehicleAlertDialog;
    EditText vehicle_owner_name_edittext,vehicle_owner_contact_number_edittext,vehicle_owner_insurance_provider_edittext,vehicle_owner_insurance_policy_no_edittext;
    AccidentBasicDetails db;
    SessionManager sessionManager;
    ArrayList<AccidentVehicleDetailsModel> vehicleDetailArrayList;
    boolean isFormValid;
    CustomValidator customValidator;
    TextView error_vehicle_insurance_provider,error_vehicle_insurance_no,error_vehicle_license_no;
    boolean popUpOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_reported_accident_screen_four_form_data, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        db = new AccidentBasicDetails(ReportedAccidentScreenFourFormData.this);
        sessionManager = new SessionManager();
        customValidator = new CustomValidator(ReportedAccidentScreenFourFormData.this);
        vehicleDetailArrayList = new ArrayList<>();
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
    }

    private void setListeners(){
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

    void showVehicleDetailPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_vehicle_detail, null);
        dialogBuilder.setView(dialogView);

        popUpOpened = true;

        vehicle_insurance_provider_edittext = dialogView.findViewById(R.id.vehicle_insurance_provider);
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

        Button add_btn = dialogView.findViewById(R.id.add_destination_location);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateForm();
                if(isFormValid){
                    vehicleAlertDialog.dismiss();

                    LayoutInflater inflater = (LayoutInflater)getBaseContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

                    AccidentVehicleDetailsModel model = new AccidentVehicleDetailsModel();
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

                    vehicleDetailArrayList.add(model);

                    cancel_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            vehicle_detail_container.removeView((View) view.getParent());
                        }
                    });

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
        vehicleAlertDialog.show();
        vehicleAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


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

    private void saveDataToDb(){
        validateForm();
        if(isFormValid){
            AccidentBasicDetailsModel accidentBasicDetailsModel = new AccidentBasicDetailsModel();
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
            }
            Intent i = new Intent(ReportedAccidentScreenFourFormData.this, ReportedAccidentScreenFive.class);
            startActivity(i);
        }
    }

    @Bind(R.id.accident_date)
    EditText accident_date;

    @Bind(R.id.accident_time)
    EditText accident_time;

    @Bind(R.id.vehicle_detail_container)
    LinearLayout vehicle_detail_container;

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

        dpd.show(ReportedAccidentScreenFourFormData.this.getFragmentManager(), "DatePickerDialog");


    }

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
        tpd.show(ReportedAccidentScreenFourFormData.this.getFragmentManager(),"TimePickerDialog");
    }

    @OnClick(R.id.add_vehicle_img)
    void callFunc(){
        showVehicleDetailPopUp();
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
}
