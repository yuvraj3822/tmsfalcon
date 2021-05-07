package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.AccidentDamageListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccidentDamageDetails extends NavigationBaseActivity implements CompoundButton.OnCheckedChangeListener {

    SessionManager sessionManager;
    CustomValidator customValidator;
    AlertDialog damageAlertDialog;
    TextView text_for_damage_type;
    LinearLayout checkbox_block;
    AppCompatRadioButton injury_radio,death_radio,property_radio;
    AccidentDamageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_accident_damage_details, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
       // list_view_damages.init(AccidentDamageDetails.this);
        adapter = new AccidentDamageListAdapter(AccidentDamageDetails.this);
        list_view_damages.setAdapter(adapter);
        //showDetailPopUp();
    }

    void initIds(){
        sessionManager = new SessionManager();
        customValidator = new CustomValidator(AccidentDamageDetails.this);
    }
    void showDetailPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_accident_damage_detail, null);
        dialogBuilder.setView(dialogView);

        ZoomLinearLayout zoomLinearLayout = dialogView.findViewById(R.id.zoom_linear_layout);
        zoomLinearLayout.init(AccidentDamageDetails.this);


        Button cancel_btn = dialogView.findViewById(R.id.cancel_btn);
        injury_radio = dialogView.findViewById(R.id.injury_radio);
        death_radio = dialogView.findViewById(R.id.death_radio);
        property_radio = dialogView.findViewById(R.id.property_radio);
        text_for_damage_type = dialogView.findViewById(R.id.text_for_damage_type);
        checkbox_block = dialogView.findViewById(R.id.checkbox_block);

        injury_radio.setOnCheckedChangeListener(AccidentDamageDetails.this);
        death_radio.setOnCheckedChangeListener(AccidentDamageDetails.this);
        property_radio.setOnCheckedChangeListener(AccidentDamageDetails.this);

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                damageAlertDialog.dismiss();

            }
        });

        damageAlertDialog = dialogBuilder.create();
        //emailAlertDialog.setTitle("Send Documents By Email");
        damageAlertDialog.show();
        damageAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);


    }

    @Bind(R.id.list_view_damages)
    ListView list_view_damages;

    @OnClick(R.id.add_damage_detail)
    void callFunc(){
        showDetailPopUp();
    }

    @OnClick(R.id.next_btn)
    void nextActivty(){
        Intent i = new Intent(AccidentDamageDetails.this,ReportedAccidentScreenFive.class);
        startActivity(i);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        if(b){
            Log.e("id",""+id);
            switch(id){
                case R.id.injury_radio:
                    Log.e("in","injury_radio");
                    text_for_damage_type.setText("Injured Person Details");
                    checkbox_block.setVisibility(View.VISIBLE);
                    break;
                case R.id.death_radio:
                    Log.e("in","death_radio");
                    text_for_damage_type.setText("Deceased Person Details");
                    checkbox_block.setVisibility(View.VISIBLE);
                    break;
                case R.id.property_radio:
                    Log.e("in","property_radio");
                    text_for_damage_type.setText("Property Owner Details");
                    checkbox_block.setVisibility(View.GONE);
                    break;

            }
        }

    }
}
