package com.tmsfalcon.device.tmsfalcon.activities.directUploadModule;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.BlankActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DirectUploadPreviewScreen extends NavigationBaseActivity {

    private static final int WRITE_EXTERNAL_STORAGE = 123;
    PermissionManager permissionsManager = new PermissionManager();
    ImageHelper imageHelper = new ImageHelper();
    @SuppressWarnings("unused")
    String intent_document_request_id,intent_document_type,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status;
    DirectUploadTable documentsTable;
    SessionManager sessionManager;
    List<DocumentRequestModel> records;
    ArrayList<String> imagesList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_direct_upload_preview_screen);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_direct_upload_preview_screen, null, false);
        drawer.addView(contentView, 0);

        ButterKnife.bind(this);

        sessionManager = new SessionManager(DirectUploadPreviewScreen.this);
        captured_image.setImageBitmap(AppController.nowbitmap);
        documentsTable = new DirectUploadTable(DirectUploadPreviewScreen.this);
        records = documentsTable.getAllDocuments();
        for (DocumentRequestModel cn : records) {

            imagesList.add(cn.getFile_url());
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DirectUploadPreviewScreen.this,DirectUploadCropScreen.class);
        startActivity(i);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {

                case WRITE_EXTERNAL_STORAGE:
                    String saved_image_url = imageHelper.saveImageBitmap(AppController.nowbitmap,"captured",imageHelper.uniqueFileName());
                    Log.e("saved image url",saved_image_url);
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
        if (!permissionsManager.checkPermission(DirectUploadPreviewScreen.this, DirectUploadPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsManager.askForPermission(DirectUploadPreviewScreen.this, DirectUploadPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        } else {
            //documentsTable.deleteAllRecords();
            String saved_image_url = imageHelper.saveImageBitmap(AppController.nowbitmap,"directUpload",imageHelper.uniqueFileName());
            long inserted_status = documentsTable.insertDocument(new DocumentRequestModel(saved_image_url));

            List<DocumentRequestModel> records = documentsTable.getAllDocuments();

            if(inserted_status != -1){

                //check if document info (document belongs to,load no,document type )has been stored already or not
                String doc_belongs_to = sessionManager.getKeyDocumentBelongsTo();
                Log.e("doc_belongs_to ",""+doc_belongs_to);
                if(doc_belongs_to == "" || (Objects.equals(doc_belongs_to,null))){
                    Intent nextScreen = new Intent(DirectUploadPreviewScreen.this,BlankActivity.class);
                    startActivity(nextScreen);
                }
                else{
                    Intent queueScreen = new Intent(DirectUploadPreviewScreen.this,DirectUploadQueueScreen.class);
                    startActivity(queueScreen);

                }


            }
            else{
                Toast.makeText(DirectUploadPreviewScreen.this,"Record Not Inserted",Toast.LENGTH_LONG).show();
            }

        }

    }

    @OnClick(R.id.add_new_image_layout)
    void addNewImage(){
        if (!permissionsManager.checkPermission(DirectUploadPreviewScreen.this, DirectUploadPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsManager.askForPermission(DirectUploadPreviewScreen.this, DirectUploadPreviewScreen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        } else {
            //documentsTable.deleteAllRecords();
            String saved_image_url = imageHelper.saveImageBitmap(AppController.nowbitmap,"directUpload",imageHelper.uniqueFileName());
            long inserted_status = documentsTable.insertDocument(new DocumentRequestModel(saved_image_url));

            if(inserted_status != -1){
                if(imagesList.size() < 8){
                    Intent captureIntent = new Intent(DirectUploadPreviewScreen.this, DirectUploadCameraScreen.class);
                    startActivity(captureIntent);
                }
                else{
                    Toast.makeText(DirectUploadPreviewScreen.this,R.string.maximum_limit_exceed,Toast.LENGTH_LONG).show();
                }

            }
            else{
                Toast.makeText(DirectUploadPreviewScreen.this,"Record Not Inserted",Toast.LENGTH_LONG).show();
            }

        }


    }
}
