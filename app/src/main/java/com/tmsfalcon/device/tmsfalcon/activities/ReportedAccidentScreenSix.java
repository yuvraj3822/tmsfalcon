package com.tmsfalcon.device.tmsfalcon.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotAccidentCaptureScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportedAccidentScreenSix extends NavigationBaseActivity {

    PermissionManager permissionsManager = new PermissionManager();
    int has_camera_permission = 0;
    int has_storage_permission = 0;
    static final Integer CAMERA = 5;
    private static final int WRITE_EXTERNAL_STORAGE = 123;
    private static int nextscreen = 1;


    private final int localCameraPermission = 127;
    private final int localWritePermission = 124;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == nextscreen){
            if (resultCode == Activity.RESULT_OK){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "result");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            }
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_reported_accident_screen_six, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        zoom_linear_layout.init(ReportedAccidentScreenSix.this);

    }


    private void navigationScreen(){
        Intent i = new Intent(ReportedAccidentScreenSix.this, KotAccidentCaptureScreen.class);
        startActivityForResult(i,nextscreen);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {


                case localCameraPermission:
                    Log.e("permission",":  permission provided");
                    checkPermission();
                    break;

                case localWritePermission:
                    Log.e("permission",": permission provided ");
                    checkPermission();
                    break;

//                //Camera
//                case 5:
//                    has_camera_permission = 1;
//                    if(has_camera_permission == 1 && has_storage_permission == 1){
////                        Intent i = new Intent(ReportedAccidentScreenSix.this, KotAccidentCaptureScreen.class);
////                        startActivity(i);
//                        navigationScreen();
//                    }
//                    else{
//                        Toast.makeText(ReportedAccidentScreenSix.this,"Please allow Camera permissions first from Settings.",Toast.LENGTH_LONG).show();
//                    }
//                    break;
//                case 123:
//                    has_storage_permission = 1;
//                    if(has_camera_permission == 1 && has_storage_permission == 1){
////                        Intent i = new Intent(ReportedAccidentScreenSix.this,KotAccidentCaptureScreen.class);
////                        startActivity(i);
//                        navigationScreen();
//                    }
//                    else{
//                        Toast.makeText(ReportedAccidentScreenSix.this,"Please allow Camera permissions first from Settings.",Toast.LENGTH_LONG).show();
//                    }
//                    break;
//
//                default:
//                    if(has_camera_permission == 1 && has_storage_permission == 1){
////                        Intent i = new Intent(ReportedAccidentScreenSix.this,KotAccidentCaptureScreen.class);
////                        startActivity(i);
//                        navigationScreen();
//                    }
//                    else{
//                        Toast.makeText(ReportedAccidentScreenSix.this,"Please allow Camera permissions first from Settings.",Toast.LENGTH_LONG).show();
//                    }
//                    break;
//
            }

            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Please provide the permission else you wont be able to proceeed further.", Toast.LENGTH_LONG).show();
        }
    }

    @Bind(R.id.zoom_linear_layout)
    ZoomLinearLayout zoom_linear_layout;


    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(ReportedAccidentScreenSix.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            Log.e("permission: ","denied");
            ActivityCompat.requestPermissions(ReportedAccidentScreenSix.this, new String[] {Manifest.permission.CAMERA}, localCameraPermission);

        }else if (ContextCompat.checkSelfPermission(ReportedAccidentScreenSix.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED){
            Log.e("permission: ","denied");
            ActivityCompat.requestPermissions(ReportedAccidentScreenSix.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, localWritePermission);
        } else {
            navigationScreen();
        }
    }

    @OnClick(R.id.next_btn)
    void goToNext() {

      checkPermission();

//        navigationScreen();



//        if (!permissionsManager.checkPermission(ReportedAccidentScreenSix.this, ReportedAccidentScreenSix.this, Manifest.permission.CAMERA)) {
//            permissionsManager.askForPermission(ReportedAccidentScreenSix.this, ReportedAccidentScreenSix.this, Manifest.permission.CAMERA, CAMERA);
//        }
//        else{
//            has_camera_permission = 1;
//        }
//        if (!permissionsManager.checkPermission(ReportedAccidentScreenSix.this, ReportedAccidentScreenSix.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            permissionsManager.askForPermission(ReportedAccidentScreenSix.this, ReportedAccidentScreenSix.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
//        }
//        else {
//            has_storage_permission = 1;
//        }
//
//        if(has_camera_permission == 1 && has_storage_permission == 1){
////            Intent i = new Intent(ReportedAccidentScreenSix.this,KotAccidentCaptureScreen.class);
////            startActivity(i);
//            navigationScreen();
//        }
//        else{
//            Toast.makeText(ReportedAccidentScreenSix.this,"Please allow Camera permissions first from Settings.",Toast.LENGTH_LONG).show();
//        }
//    }
    }
}
