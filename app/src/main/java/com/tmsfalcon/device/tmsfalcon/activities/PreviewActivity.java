package com.tmsfalcon.device.tmsfalcon.activities;

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
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.database.RequestedDocumentsTable;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PreviewActivity extends NavigationBaseActivity {

    private static final int WRITE_EXTERNAL_STORAGE = 123;
    PermissionManager permissionsManager = new PermissionManager();
    ImageHelper imageHelper = new ImageHelper();
    ArrayList<String> imagesList = new ArrayList<>();
    String intent_doc_belongs_to,intent_is_expired,intent_key,intent_document_request_id,intent_document_type,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status;
    RequestedDocumentsTable documentsTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_preview, null, false);
        drawer.addView(contentView, 0);
       // setContentView(R.layout.activity_preview);
        ButterKnife.bind(this);

        captured_image.setImageBitmap(AppController.nowbitmap);
        fetchIntent();
        documentsTable = new RequestedDocumentsTable(PreviewActivity.this);
        List<DocumentRequestModel> records = documentsTable.getAllDocumentsByKey(intent_key);
        for (DocumentRequestModel cn : records) {

            imagesList.add(cn.getFile_url());

        }

    }

    private void fetchIntent() {
        intent_document_request_id = getIntent().getExtras().getString("document_request_id");
        intent_document_type = getIntent().getExtras().getString("document_type");
        intent_document_name = getIntent().getExtras().getString("document_name");
        intent_load_number = getIntent().getExtras().getString("load_number");
        intent_due_date = getIntent().getExtras().getString("due_date");
        intent_comment = getIntent().getExtras().getString("comment");
        intent_status = getIntent().getExtras().getString("status");
        intent_key = getIntent().getExtras().getString("key");
        intent_is_expired = getIntent().getExtras().getString("is_expired");
        intent_doc_belongs_to = getIntent().getExtras().getString("document_belongs_to");
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
        }
        else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }


    @Bind(R.id.captured_image)
    ImageView captured_image;

    @OnClick(R.id.proceed_layout)
    void proceedFurther(){
        //save image here anxd save save record in db
        if (!permissionsManager.checkPermission(PreviewActivity.this, PreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsManager.askForPermission(PreviewActivity.this, PreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        } else {
            //documentsTable.deleteAllRecords();
            String saved_image_url = imageHelper.saveImageBitmap(AppController.nowbitmap,"captured",imageHelper.uniqueFileName());
            long inserted_status = documentsTable.insertDocument(new DocumentRequestModel(intent_document_request_id,intent_document_type,saved_image_url,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status,intent_key));

            if(inserted_status != -1){

                Intent queueScreen = new Intent(PreviewActivity.this,QueueActivity.class);
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
                Toast.makeText(PreviewActivity.this,"Record Not Inserted",Toast.LENGTH_LONG).show();
            }

        }

    }

    @OnClick(R.id.add_new_image_layout)
    void addNewImage(){
        if (!permissionsManager.checkPermission(PreviewActivity.this, PreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsManager.askForPermission(PreviewActivity.this, PreviewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
        } else {
            //documentsTable.deleteAllRecords();



            String saved_image_url = imageHelper.saveImageBitmap(AppController.nowbitmap,"captured",imageHelper.uniqueFileName());
            long inserted_status = documentsTable.insertDocument(new DocumentRequestModel(intent_document_request_id,intent_document_type,saved_image_url,intent_document_name,intent_load_number,intent_due_date,intent_comment,intent_status,intent_key));

            if(inserted_status != -1){

                if(imagesList.size() < 8){
                    Intent captureIntent = new Intent(PreviewActivity.this, CaptureDocument.class);
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
                    Toast.makeText(PreviewActivity.this,R.string.maximum_limit_exceed,Toast.LENGTH_LONG).show();
                }

            }
            else{
                Toast.makeText(PreviewActivity.this,"Record Not Inserted",Toast.LENGTH_LONG).show();
            }

        }

    }

}
