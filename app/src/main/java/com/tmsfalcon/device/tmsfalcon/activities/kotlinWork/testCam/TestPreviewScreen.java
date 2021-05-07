package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.scanlibrary.ScanAppController;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.BlankActivity;
import com.tmsfalcon.device.tmsfalcon.activities.QueueActivity;
import com.tmsfalcon.device.tmsfalcon.activities.directUploadModule.DirectUploadQueueScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;
import com.tmsfalcon.device.tmsfalcon.database.RequestedDocumentsTable;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestPreviewScreen extends NavigationBaseActivity {

    private static final int WRITE_EXTERNAL_STORAGE = 123;
    PermissionManager permissionsManager = new PermissionManager();
    ImageHelper imageHelper = new ImageHelper();
    @SuppressWarnings("unused")
    String intent_doc_belongs_to,intent_is_expired,intent_key,intent_document_request_id,intent_document_type,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status;
    DirectUploadTable documentsTable;
    SessionManager sessionManager;
    List<DocumentRequestModel> records;
    ArrayList<String> imagesList = new ArrayList<>();
    private Bitmap original;
    RequestedDocumentsTable reqDocumentsTable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_direct_upload_preview_screen);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_direct_upload_preview_screen, null, false);
        drawer.addView(contentView, 0);
        Log.e("checkscreens","TestPReviewLayout");

        ButterKnife.bind(this);

        sessionManager = new SessionManager(TestPreviewScreen.this);
//        original = applySharpeingeffect(Objects.requireNonNull(getBitmap()));
        original = ScanAppController.nowbitmap;
        captured_image.setImageBitmap(original);

        isItFromNotification=fetchIntent();

        documentsTable = new DirectUploadTable(TestPreviewScreen.this);

        reqDocumentsTable = new RequestedDocumentsTable(TestPreviewScreen.this);


        if (isItFromNotification){
          records =   reqDocumentsTable.getAllDocuments();
        }
        else {
            records = documentsTable.getAllDocuments();
        }


        for (DocumentRequestModel cn : records) {
            imagesList.add(cn.getFile_url());
        }
    }


private Bitmap applySharpeingeffect(Bitmap original){
    Mat src = new Mat(original.getHeight(), original.getWidth(), CvType.CV_8UC4);
//                Mat and =  Utils.bitmapToMat(original, src);

    org.opencv.android.Utils.bitmapToMat(original, src);
    Mat kernel = new Mat(3,3, CvType.CV_16SC1);
//                kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);
//                kernel.put(0, 0, 1, -2, 1, 2, -4, 2, 1, -2, 1);
    kernel.put(0, 0, 0, -1, 0, -1, 5, -1, 0, -1, 0);

    Imgproc.filter2D(src, src, src.depth(), kernel);

    org.opencv.android.Utils.matToBitmap(src,original);

//    captured_image.setImageBitmap(original);

    return original;

}

    private Bitmap getBitmap() {
//        Uri uri = ScanAppController.nowUri;
        //            Log.e("uri: ",": "+uri);
//           Bitmap original = Utils.getBitmap(this, uri);
//            getContentResolver().delete(uri, null, null);

        Bitmap original =  ScanAppController.nowbitmap;

        return original;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent i = new Intent(TestPreviewScreen.this,DirectUploadCropScreen.class);
//        startActivity(i);
    }




    private boolean isItFromNotification= false;

    private Boolean fetchIntent()  {
        boolean isDataPresent = false;
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
            isDataPresent = true;
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
            isDataPresent = false;
        }

        return isDataPresent;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {

                case WRITE_EXTERNAL_STORAGE:
                    if (isItFromNotification) {

                        String saved_image_url = imageHelper.saveImageBitmap(AppController.nowbitmap, "captured", imageHelper.uniqueFileName());
                        Log.e("saved image url", saved_image_url);
                    }else {

                        String saved_image_url = imageHelper.saveImageBitmap(AppController.nowbitmap, "directUpload", imageHelper.uniqueFileName());
                        Log.e("saved image url", saved_image_url);
                    }
                    break;

            }
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Bind(R.id.captured_image)
    ImageView captured_image;

    @SuppressWarnings("unused")
    @OnClick(R.id.proceed_layout)
    void proceedFurther(){
        /*Intent nextScreen = new Intent(DirectUploadPreviewScreen.this,BlankActivity.class);
        startActivity(nextScreen);*/
        //save image here anxd save save record in db
        if (!permissionsManager.checkPermission(TestPreviewScreen.this, TestPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsManager.askForPermission(TestPreviewScreen.this, TestPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        } else {
            //documentsTable.deleteAllRecords();
            Log.e("TAG", "proceedFurther: " );

            if (isItFromNotification){
                String saved_image_url = imageHelper.saveImageBitmap(original,"captured",imageHelper.uniqueFileName());
                long inserted_status = reqDocumentsTable.insertDocument(new DocumentRequestModel(intent_document_request_id,intent_document_type,saved_image_url,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status,intent_key));

                if(inserted_status != -1){
                    Log.e("checkNot","withdata");

                    Intent queueScreen = new Intent(TestPreviewScreen.this, QueueActivity.class);
                    queueScreen.putExtra("document_request_id",intent_document_request_id);
                    queueScreen.putExtra("document_type",intent_document_type);
                    queueScreen.putExtra("document_name",intent_document_name);
                    queueScreen.putExtra("load_number",intent_load_number);
                    queueScreen.putExtra("due_date",intent_due_date);
                    queueScreen.putExtra("comment",intent_comment);
                    queueScreen.putExtra("status",intent_status);
                    queueScreen.putExtra("key",intent_key);
                    queueScreen.putExtra("is_expired",intent_is_expired);
                    queueScreen.putExtra("document_belongs_to",intent_doc_belongs_to);
                    startActivity(queueScreen);

                }
                else{
                    Toast.makeText(TestPreviewScreen.this,"Record Not Inserted",Toast.LENGTH_LONG).show();
                }

            } else {
                String saved_image_url = imageHelper.saveImageBitmap(original, "directUpload", imageHelper.uniqueFileName());
                long inserted_status = documentsTable.insertDocument(new DocumentRequestModel(saved_image_url));

                List<DocumentRequestModel> records = documentsTable.getAllDocuments();

                if (inserted_status != -1) {
                    //check if document info (document belongs to,load no,document type )has been stored already or not
                    String doc_belongs_to = sessionManager.getKeyDocumentBelongsTo();
                    Log.e("doc_belongs_to ", "" + doc_belongs_to);
                    if (doc_belongs_to == "" || (Objects.equals(doc_belongs_to, null))) {
                        Log.e("checkNot","withoutdata");
                        Intent nextScreen = new Intent(TestPreviewScreen.this, BlankActivity.class);
                        startActivity(nextScreen);
                    } else {
                        Log.e("checkNot","withoutdata");
                        Intent queueScreen = new Intent(TestPreviewScreen.this, DirectUploadQueueScreen.class);
                        startActivity(queueScreen);
                    }


                } else {
                    Toast.makeText(TestPreviewScreen.this, "Record Not Inserted", Toast.LENGTH_LONG).show();
                }
            }

        }

    }

    @OnClick(R.id.add_new_image_layout)
    void addNewImage(){
        if (!permissionsManager.checkPermission(TestPreviewScreen.this, TestPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsManager.askForPermission(TestPreviewScreen.this, TestPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        } else {


            if (isItFromNotification){

                String saved_image_url = imageHelper.saveImageBitmap(original,"captured",imageHelper.uniqueFileName());
                long inserted_status = reqDocumentsTable.insertDocument(new DocumentRequestModel(intent_document_request_id,intent_document_type,saved_image_url,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status,intent_key));

                if(inserted_status != -1){

                    if(imagesList.size() < 8){

                        Log.e("checkNot","withdata");

                        //Intent captureIntent = new Intent(TestPreviewScreen.this, CaptureDocument.class);
                        Intent captureIntent = new Intent(TestPreviewScreen.this, TestCameraScreen.class);

                        captureIntent.putExtra("document_request_id", intent_document_request_id);
                        captureIntent.putExtra("document_type", intent_document_type);
                        captureIntent.putExtra("document_name", intent_document_name);
                        captureIntent.putExtra("load_number", intent_load_number);
                        captureIntent.putExtra("due_date", intent_due_date);
                        captureIntent.putExtra("comment", intent_comment);
                        captureIntent.putExtra("status", intent_status);
                        captureIntent.putExtra("key", intent_key);
                        captureIntent.putExtra("is_expired",intent_is_expired);
                        captureIntent.putExtra("document_belongs_to",intent_doc_belongs_to);
                        startActivity(captureIntent);
                    }
                    else{
                        Toast.makeText(TestPreviewScreen.this,R.string.maximum_limit_exceed,Toast.LENGTH_LONG).show();
                    }

                }
                else{
                    Toast.makeText(TestPreviewScreen.this,"Record Not Inserted",Toast.LENGTH_LONG).show();
                }

            }else {

                //documentsTable.deleteAllRecords();
                String saved_image_url = imageHelper.saveImageBitmap(original, "directUpload", imageHelper.uniqueFileName());
                long inserted_status = documentsTable.insertDocument(new DocumentRequestModel(saved_image_url));

                if (inserted_status != -1) {
                    if (imagesList.size() < 8) {
                        Log.e("checkNot","withoutdata");

                        Intent captureIntent = new Intent(TestPreviewScreen.this, TestCameraScreen.class);
                        startActivity(captureIntent);
                    } else {
                        Toast.makeText(TestPreviewScreen.this, R.string.maximum_limit_exceed, Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(TestPreviewScreen.this, "Record Not Inserted", Toast.LENGTH_LONG).show();
                }
            }
        }


    }
}
