package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotAccidentDamageDetails;
import com.tmsfalcon.device.tmsfalcon.adapters.AccidentVehicleListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OtherPartyVehicleDetails extends NavigationBaseActivity {

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
    AppCompatCheckBox is_towed_checkbox;
    LinearLayout tow_block;
    ScrollView scroll_view;

    AccidentVehicleListAdapter vehicleListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_other_party_vehicle_details, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
       // listView.init(OtherPartyVehicleDetails.this);
        vehicleListAdapter = new AccidentVehicleListAdapter(OtherPartyVehicleDetails.this);
        listView.setAdapter(vehicleListAdapter);
        //showVehicleDetailPopUp();

    }

    void initIds(){

        db = new AccidentBasicDetails(OtherPartyVehicleDetails.this);
        sessionManager = new SessionManager();
        customValidator = new CustomValidator(OtherPartyVehicleDetails.this);
        vehicleDetailArrayList = new ArrayList<>();

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

    void showVehicleDetailPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_other_party_accident_vehicle_detail, null);
        dialogBuilder.setView(dialogView);

        popUpOpened = true;

        ZoomLinearLayout zoomLinearLayout = dialogView.findViewById(R.id.zoom_linear_layout);
        zoomLinearLayout.init(OtherPartyVehicleDetails.this);




        is_towed_checkbox = dialogView.findViewById(R.id.is_towed_checkbox);
        scroll_view = dialogView.findViewById(R.id.scroll_view);
        tow_block = dialogView.findViewById(R.id.tow_block);
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

                // validateForm();
                if(isFormValid){
                    vehicleAlertDialog.dismiss();

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
        vehicleAlertDialog.show();
        vehicleAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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

    @OnClick(R.id.next_btn)
    void nextActivity(){
        Intent i = new Intent(OtherPartyVehicleDetails.this, KotAccidentDamageDetails.class);
        startActivity(i);
    }

    @OnClick(R.id.add_vehicle_detail)
    void callFunc(){
        showVehicleDetailPopUp();
    }
}
