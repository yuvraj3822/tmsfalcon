package com.tmsfalcon.device.tmsfalcon.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotReportedAccidentScreenFiveFormData;
import com.tmsfalcon.device.tmsfalcon.adapters.AccidentWitnessListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportedAccidentScreenFive extends NavigationBaseActivity {

    AlertDialog witnessAlertDialog;
    SessionManager sessionManager;
    ArrayList<AccidentWitnessModel> witness_arrayList = new ArrayList<>();
    AccidentWitnessListAdapter adapter;
    CustomValidator customValidator;
    boolean isFormValid ;
    PermissionManager permissionManager;
    static final Integer RECORD_AUDIO = 5;
    static final Integer WRITE_EXTERNAL_STORAGE = 6;
    boolean audio_permission,storage_permission;

    private static Integer NEXTSCREEN =4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_reported_accident_screen_five, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        permissionManager = new PermissionManager();

        zoom_linear_layout.init(ReportedAccidentScreenFive.this);
        if (!permissionManager.checkPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.RECORD_AUDIO)) {
            permissionManager.askForPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.RECORD_AUDIO, RECORD_AUDIO);
        }
        else{
            audio_permission = true;
        }
        if(!permissionManager.checkPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionManager.askForPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        }
        else{
            storage_permission = true;
        }

    }

    @Bind(R.id.zoom_linear_layout)
    ZoomLinearLayout zoom_linear_layout;

    @OnClick(R.id.next_btn)
    void goToNext(){
        if (!permissionManager.checkPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.RECORD_AUDIO)) {
            permissionManager.askForPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.RECORD_AUDIO, RECORD_AUDIO);
        }
        else{
            audio_permission = true;
        }
        if(!permissionManager.checkPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionManager.askForPermission(ReportedAccidentScreenFive.this, ReportedAccidentScreenFive.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        }
        else{
            storage_permission = true;
        }
        startNew();

    }

    void startNew(){
        if(audio_permission && storage_permission){
            Intent i = new Intent(ReportedAccidentScreenFive.this, KotReportedAccidentScreenFiveFormData.class);
            startActivityForResult(i,NEXTSCREEN);
        }
        else{
           // Toast.makeText(ReportedAccidentScreenFive.this,"Please grant the audio and Storage Permission from Settings first.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEXTSCREEN){
            if (resultCode == Activity.RESULT_OK){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "result");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("permissions",""+permissions.length);
        Log.e("grantResults",""+grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {

                //Camera
                case 5:
                    audio_permission = true;
                    startNew();
                    break;
                case 6:
                    storage_permission = true;
                    startNew();
                    break;

            }

            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

}
