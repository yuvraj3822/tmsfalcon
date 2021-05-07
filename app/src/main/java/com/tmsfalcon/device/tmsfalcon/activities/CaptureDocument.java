package com.tmsfalcon.device.tmsfalcon.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.IScanner;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.ScanHint;
import com.tmsfalcon.device.tmsfalcon.database.RequestedDocumentsTable;
import com.tmsfalcon.device.tmsfalcon.widgets.ProgressDialogFragment;
import com.tmsfalcon.device.tmsfalcon.widgets.ScanSurfaceView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

public class CaptureDocument extends NavigationBaseActivity implements IScanner {

    private static final String TAG = CaptureDocument.class.getSimpleName();

    static final Integer CAMERA = 0x5;
    PermissionManager permissionsManager = new PermissionManager();
    private String cameraId;
    protected CameraDevice cameraDevice;
    @SuppressWarnings("unused")
    private File file;
    @SuppressWarnings("unused")
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    CameraManager manager = null;
    boolean is_flash_enabled = false;
    CameraCharacteristics characteristics;
    TextureView.SurfaceTextureListener textureListener;
    List<String> focuslist;
    //Camera one varibles
    private Camera camera;

    int has_camera_permission = 0;
    private static final int GALLERY = 1;
    RequestedDocumentsTable db;

    private ViewGroup containerScan;
    private FrameLayout cameraPreviewLayout;
    private ScanSurfaceView mImageSurfaceView;
    private boolean isPermissionNotGranted;
    private static final String mOpenCvLibrary = "opencv_java3";
    private static ProgressDialogFragment progressDialogFragment;
    private TextView captureHintText;
    private LinearLayout captureHintLayout;
    private Bitmap copyBitmap;

    private boolean hasFlashCameraOne;

    private boolean first_flash_call = true;

    Camera.Parameters params;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    String intent_doc_belongs_to,intent_is_expired,intent_key,intent_document_request_id,intent_document_type,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("test","test");
        //closeCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //closeCamera();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(CaptureDocument.this,DashboardActivity.class);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_capture_document, null, false);
        drawer.addView(contentView, 0);
        //setContentView(R.layout.activity_capture_document);
        ButterKnife.bind(this);
        if(savedInstanceState != null){
            Log.e("in bundle not null","yes");
            Log.e("test bundle",savedInstanceState.getString("test"));
        }

        fetchIntent();
        init();
        db = new RequestedDocumentsTable(CaptureDocument.this);
        if (!permissionsManager.checkPermission(CaptureDocument.this, CaptureDocument.this, Manifest.permission.CAMERA)) {
            permissionsManager.askForPermission(CaptureDocument.this, CaptureDocument.this, Manifest.permission.CAMERA, CAMERA);
        } else {
            has_camera_permission = 1;
            cameraSettings();
        }

    }
    private void init() {
        containerScan = findViewById(R.id.container_scan);
        cameraPreviewLayout = findViewById(R.id.camera_one_preview);
        captureHintLayout = findViewById(R.id.capture_hint_layout);
        captureHintText = findViewById(R.id.capture_hint_text);

    }
    public void cameraSettings() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

            try {
                cameraId = manager.getCameraIdList()[0];
                characteristics = manager.getCameraCharacteristics(cameraId);
            } catch (CameraAccessException e) {
                Log.e("exception ", String.valueOf(e));
            }
        }
        else{
            hasFlashCameraOne = getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
            if(!hasFlashCameraOne){
                if (camera == null) {
                    try {
                        camera = Camera.open();
                        params = camera.getParameters();
                    } catch (RuntimeException e) {
                        Log.e("Failed to Open. Error: ", e.getMessage());
                    }
                }
            }
        }
        int docs_count = db.getCount();
        if(docs_count > 0) {
            DocCountBadgeTextView.setText(""+docs_count);
        }
        else{
            DocCountBadgeTextView.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mImageSurfaceView = new ScanSurfaceView(CaptureDocument.this, CaptureDocument.this);
                        cameraPreviewLayout.addView(mImageSurfaceView);
                    }
                });
            }
        }, 500);


    }

    static {
        System.loadLibrary(mOpenCvLibrary);
    }

    private void fetchIntent() {

        if(getIntent().getExtras() != null){
            intent_document_request_id = getIntent().getExtras().getString("document_request_id","");
            intent_document_type = getIntent().getExtras().getString("document_type","");
            intent_document_name = getIntent().getExtras().getString("document_name","");
            intent_load_number = getIntent().getExtras().getString("load_number","");
            intent_due_date = getIntent().getExtras().getString("due_date","");
            intent_comment = getIntent().getExtras().getString("comment","");
            intent_status = getIntent().getExtras().getString("status","");
            intent_key = getIntent().getExtras().getString("key","");
            intent_is_expired = getIntent().getExtras().getString("is_expired","");
            intent_doc_belongs_to = getIntent().getExtras().getString("document_belongs_to","");
        }
        else{
            intent_document_request_id = "";
            intent_document_type = "";
            intent_document_name = "";
            intent_load_number = "";
            intent_due_date = "";
            intent_comment = "";
            intent_status = "";
            intent_key = "";
            intent_is_expired = "";
            intent_doc_belongs_to = "";

        }

    }

    public int checkDeviceCameraSupport(){
        int capability = 0;
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                capability = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                Log.d("Img", "INFO_SUPPORTED_HARDWARE_LEVEL " + characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL));
            }
        } catch (CameraAccessException e) {
             Log.e("exception ", String.valueOf(e));
        }
        return capability;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {

                //Camera
                case 5:
                    has_camera_permission = 1;
                    cameraSettings();
                    break;

            }

            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Bind(R.id.camera_one_preview) FrameLayout cameraOnePreview;
    @Bind(R.id.bottom_lights)  ImageView bottom_lights;
    @Bind(R.id.flash_text)
    TextView bottom_flash_text;
    @Bind(R.id.capture_linear_layout)
    LinearLayout captureLinearLayout;
    @Bind(R.id.doc_count_badge)
    TextView DocCountBadgeTextView;

    @OnClick(R.id.import_layout)
    void performImportFunction(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    AppController.nowbitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), contentURI);
                    AppController.nowbitmap = ImageHelper.resizeImageScaled(AppController.nowbitmap);
                    Intent goToCropScreen = new Intent(CaptureDocument.this, CropActivity.class);
                    goToCropScreen.putExtra("document_request_id",intent_document_request_id);
                    goToCropScreen.putExtra("document_type",intent_document_type);
                    goToCropScreen.putExtra("document_name",intent_document_name);
                    goToCropScreen.putExtra("load_number",intent_load_number);
                    goToCropScreen.putExtra("due_date",intent_due_date);
                    goToCropScreen.putExtra("comment",intent_comment);
                    goToCropScreen.putExtra("status",intent_status);
                    goToCropScreen.putExtra("key",intent_key);
                    goToCropScreen.putExtra("is_expired",intent_is_expired);
                    goToCropScreen.putExtra("document_belongs_to",intent_doc_belongs_to);
                    goToCropScreen.putExtra("imported_from_gallery",true);
                    startActivity(goToCropScreen);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(CaptureDocument.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }


    @OnClick(R.id.capture_layout)
    void txtClick() {
        if(has_camera_permission == 1){

            mImageSurfaceView.autoCapture(ScanHint.CAPTURING_IMAGE);

        }
        else{
            Toast.makeText(CaptureDocument.this, "Please Enable Camera Permissions First.", Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.flash_layout)
    void flashFunction(){
        if(first_flash_call){
            first_flash_call = false;
            mImageSurfaceView.turnOnFlash = true;
            mImageSurfaceView.flashMode();
        }
        if(is_flash_enabled){

            is_flash_enabled = false;
            mImageSurfaceView.turnOnFlash = false;
            mImageSurfaceView.flashMode();
            bottom_lights.setImageResource(R.drawable.headlight_off);
            bottom_flash_text.setText(getResources().getString(R.string.lights_off));
        }
        else{
            is_flash_enabled = true;
            mImageSurfaceView.turnOnFlash = true;
            mImageSurfaceView.flashMode();
            bottom_lights.setImageResource(R.drawable.headlight);
            bottom_flash_text.setText(getResources().getString(R.string.lights_on));

        }

    }

    @OnClick(R.id.back_btn)
    void goToPreviousScreen(){
        //super.onBackPressed();
        Intent i = new Intent(CaptureDocument.this,DashboardActivity.class);
        startActivity(i);
    }

    @Override
    public void displayHint(ScanHint scanHint) {
        captureHintLayout.setVisibility(View.VISIBLE);
        switch (scanHint) {
            case MOVE_CLOSER:
                captureHintText.setText(getResources().getString(R.string.move_closer));
                captureHintLayout.setBackground(getResources().getDrawable(R.drawable.hint_red));
                break;
            case MOVE_AWAY:
                captureHintText.setText(getResources().getString(R.string.move_away));
                captureHintLayout.setBackground(getResources().getDrawable(R.drawable.hint_red));
                break;
            case ADJUST_ANGLE:
                captureHintText.setText(getResources().getString(R.string.adjust_angle));
                captureHintLayout.setBackground(getResources().getDrawable(R.drawable.hint_red));
                break;
            case FIND_RECT:
                captureHintText.setText(getResources().getString(R.string.finding_rect));
                captureHintLayout.setBackground(getResources().getDrawable(R.drawable.hint_white));
                break;
            case CAPTURING_IMAGE:
                captureHintText.setText(getResources().getString(R.string.hold_still));
                captureHintLayout.setBackground(getResources().getDrawable(R.drawable.hint_green));
                break;
            case NO_MESSAGE:
                captureHintLayout.setVisibility(GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPictureClicked(Bitmap bitmap,byte[] bytes) {
        try {
            copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
            int width = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();

            //COMMENTED FOR HIGH RESOLUTION PICTURE
            //copyBitmap = ScanUtils.resizeToScreenContentSize(copyBitmap, width, height);

            AppController.nowbitmap = copyBitmap;
            /* if (Build.MANUFACTURER.equalsIgnoreCase("samsung") || Build.MANUFACTURER.equalsIgnoreCase("OnePlus")) {
                AppController.nowbitmap = rotateImage(copyBitmap,90);
            }
            else{
                AppController.nowbitmap = copyBitmap;
            }
*/
            Intent goToCropScreen = new Intent(CaptureDocument.this,CropActivity.class);
            goToCropScreen.putExtra("document_request_id",intent_document_request_id);
            goToCropScreen.putExtra("document_type",intent_document_type);
            goToCropScreen.putExtra("document_name",intent_document_name);
            goToCropScreen.putExtra("load_number",intent_load_number);
            goToCropScreen.putExtra("due_date",intent_due_date);
            goToCropScreen.putExtra("comment",intent_comment);
            goToCropScreen.putExtra("status",intent_status);
            goToCropScreen.putExtra("key",intent_key);
            goToCropScreen.putExtra("is_expired",intent_is_expired);
            goToCropScreen.putExtra("document_belongs_to",intent_doc_belongs_to);
            goToCropScreen.putExtra("imported_from_gallery",false);
            startActivity(goToCropScreen);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
