package com.tmsfalcon.device.tmsfalcon.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestCameraScreen;
import com.tmsfalcon.device.tmsfalcon.adapters.QueueAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ProgressRequestBodyMultiple;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.database.RequestedDocumentsTable;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class QueueActivity extends NavigationBaseActivity implements ProgressRequestBodyMultiple.UploadCallbacks{

    String intent_doc_belongs_to,intent_is_expired,intent_key,intent_document_request_id, intent_document_type, intent_document_name, intent_load_number, intent_due_date, intent_comment, intent_status;
    ArrayList<String> imagesList = new ArrayList<>();
    ArrayList<String> dbIdList = new ArrayList<>();
    RequestedDocumentsTable db;
    String document_name, title, comment, due_date;
    QueueAdapter adapter;
    String document_type, document_request_id;
    ArrayList<File> files_array = new ArrayList<>();
    NetworkValidator networkValidator;
    File temp_file;
    Call<PostResponse> call;
    long total_uploaded = 0;
    long total_files_length = 0;
    int previous_file_iterator = 0 ;
    static long file_uploaded_previous = 0;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build())*/;
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.capture_documents_queue, null, false);
        drawer.addView(contentView, 0);
        // setContentView(R.layout.capture_documents_queue);
        ButterKnife.bind(this);
        fetchIntent();
        if(CustomValidator.checkNullState(intent_comment)){
            commentLayout.setVisibility(View.GONE);
        }
        else {
            commentLayout.setVisibility(View.VISIBLE);
        }
        if(CustomValidator.checkNullState(intent_due_date)){
            dueDateLayout.setVisibility(View.GONE);
        }
        else {
            dueDateLayout.setVisibility(View.VISIBLE);
        }
        sessionManager = new SessionManager(QueueActivity.this);

        db = new RequestedDocumentsTable(QueueActivity.this);
        List<DocumentRequestModel> records = db.getAllDocumentsByKey(intent_key);
        networkValidator = new NetworkValidator(QueueActivity.this);
        for (DocumentRequestModel cn : records) {
            document_name = cn.getDocuments();
            title = cn.getTitle();
            due_date = cn.getDocument_due_date();
            comment = cn.getComment();
            document_request_id = cn.getDocument_request_id();
            document_type = cn.getDocument_code();
            Log.e("type","t : "+document_type);
            imagesList.add(cn.getFile_url());
            dbIdList.add(cn.getDb_id());
            /*try{
                temp_file = null;
                temp_file = new File(cn.getFile_url());
            }
            catch (Exception e){

            }
            files_array.add(temp_file);*/
        }

        titleTextView.setText(title);
        documentNameTextView.setText(document_name);
        dueDateTextView.setText(due_date);
        commentTextView.setText(comment);
        db.showRecords(db);
        adapter = new QueueAdapter(QueueActivity.this, QueueActivity.this, imagesList, dbIdList);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(serviceTaskReceiver,
                new IntentFilter("perform_job_intent_uploading_tasks"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceTaskReceiver);

    }

    private BroadcastReceiver serviceTaskReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String task = intent.getStringExtra("task");
            Log.e("job intent receiver", "Got message: " + task);
            if(task.equals(Utils.SHOW_PROGRESS_BAR)){
                showProgessBar();
            }
            else if(task.equals(Utils.HIDE_PROGRESS_BAR)){
                hideProgressBar();
            }

        }
    };

    @Override
    protected void onResume() {

        super.onResume();
        adapter.notifyDataSetChanged();
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            Runtime.getRuntime().gc();
        }
        else{
            System.gc();
        }
        */
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
        Log.e("fetch intent","intent_document_request_id "+intent_document_request_id+" intent_document_type "+
                intent_document_type+" intent_document_name "+intent_document_name+" intent_load_number "+intent_load_number+
                " intent_due_date "+intent_due_date+" intent_comment "+intent_comment+" intent_status "+intent_status+
                " intent_key "+intent_key+" intent_is_expired "+intent_is_expired+" intent_doc_belongs_to "+intent_doc_belongs_to);
    }

    public void showProgessBar() {
        progress_layout.setVisibility(View.VISIBLE);
        //progressBar.setVisibility(View.VISIBLE);
    }


    public void hideProgressBar() {
        progress_layout.setVisibility(View.GONE);
        // progressBar.setVisibility(View.GONE);￼
    }

    public void performBackAction(){
        new AlertDialog.Builder(this)
            .setTitle("Cancel")
            .setMessage("Do you really want to cancel this request?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    db.deleteAllRecords();
                    Intent i = new Intent(QueueActivity.this,DashboardActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    dialog.dismiss();
                }})

            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
    }

    @Bind(R.id.progress_barview)
    ProgressBar progressBarView;
    @Bind(R.id.grid_view)
    GridView gridView;
    @Bind(R.id.title)
    TextView titleTextView;
    @Bind(R.id.document_name)
    TextView documentNameTextView;
    @Bind(R.id.due_date)
    TextView dueDateTextView;
    @Bind(R.id.comment)
    TextView commentTextView;
    @Bind(R.id.progress_bar)
    public TextProgressBar progressBar;
    @Bind(R.id.progress_layout)
    public LinearLayout progress_layout;

    @Bind(R.id.second_layout)
    LinearLayout dueDateLayout;
    @Bind(R.id.third_layout)
    LinearLayout commentLayout;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(QueueActivity.this,DashboardActivity.class);
        startActivity(intent);
        //performBackAction();
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        Intent intent = new Intent(QueueActivity.this,DashboardActivity.class);
        startActivity(intent);
        //performBackAction();
    }

    @OnClick(R.id.add_more_images)
    void AddMoreImages() {

        if(imagesList.size() < 8){
//            Intent captureIntent = new Intent(QueueActivity.this, CaptureDocument.class);
            Intent captureIntent = new Intent(QueueActivity.this, TestCameraScreen.class);
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
            Toast.makeText(QueueActivity.this,R.string.maximum_limit_exceed,Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.upload_cancel)
    public void cancelUpload(){
        Utils.cancel_call = true;
        /*new AlertDialog.Builder(this)
            .setTitle("Cancel")
            .setMessage("Do you really want to cancel the uploading?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {
                    call.cancel();
                    hideProgressBar();
                    Log.e("in","request cancelled");
                    dialog.dismiss();
                }})
            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();*/

    }

    HashMap<String, File> azure_file_hashmap = new HashMap<>();
    String file_name="";
    public static ArrayList<String> url_arrayList = new ArrayList<>();


    @OnClick(R.id.upload_layout)
    void uploadDocuments() {

        if(networkValidator.isNetworkConnected()){
            if (!db.checkEmptyTable()){

//                int colorCodeDark = Color.parseColor("#EEEEEE");
//                temp_progress.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        temp_progress.setVisibility(View.VISIBLE);
//                    }
//                });



  //              postFields.put("document_id", requestBody(document_request_id));
//                postFields.put("document_type", requestBody(document_type));
//                postFields.put("document_belongs_to",requestBody(intent_doc_belongs_to));
//                postFields.put("is_expired",requestBody(intent_is_expired));
//
//


//                showProgessBar();


                final ProgressDialog dialog = new ProgressDialog(QueueActivity.this, ProgressDialog.THEME_HOLO_DARK);
                // this = YourActivity

                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Uploading");
                dialog.setMessage("Please wait. It will take a moment...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();



                Map<String, String> postFields = new HashMap<>();
                postFields.put("subject", "");
                postFields.put("device_type", "android");
                postFields.put("message", "");
                postFields.put("type", "upload_on_request");
                postFields.put("tags", "");
                postFields.put("is_expired",intent_is_expired);

                postFields.put("document_id",document_request_id);

                postFields.put("document_belongs_type", intent_document_type);
                postFields.put("document_belongs_to", intent_doc_belongs_to);
                postFields.put("document_type", document_type);
                List<MultipartBody.Part> multipartImg = new ArrayList<>();



                Log.e("data ", postFields.toString() + " tags ");


//                recordsForApi = db.getAllDocuments();

                List<DocumentRequestModel> recordsFoeApi = db.getAllDocumentsByKey(intent_key);
                int temp_i = 1;

                for (DocumentRequestModel cn : recordsFoeApi) {
                    try{
                        temp_file = null;
                        Log.e("check:pk ",": "+cn.getFile_url());
                        temp_file = new File(cn.getFile_url());
                        files_array.add(temp_file);

                        String temp_name = "";
                        temp_name = intent_doc_belongs_to + "-"
                        +sessionManager.get_driver_id()+"-"
                                + document_type + "-" + temp_i + ".jpg";

                        azure_file_hashmap.put(temp_name, temp_file);
                        temp_i++;
                    }
                    catch (Exception e){

                    }
                }





                Log.e("azuresSize",": "+recordsFoeApi.size());
                for (int i = 0; i < files_array.size(); i++) {
                    total_files_length += files_array.get(i).length();
                }




                final FileWriter[] fileWriter = {null};


                final JSONObject main_json_obj = new JSONObject();
                final JSONObject data_json_obj = new JSONObject();


                try {
                    data_json_obj.put("device_type", "android");
                    data_json_obj.put("message", "");
                    data_json_obj.put("type", "upload_on_request");
                    data_json_obj.put("tags", "");
                    data_json_obj.put("is_expired",intent_is_expired);
                    data_json_obj.put("document_id",document_request_id);
                    data_json_obj.put("document_belongs_type", (intent_document_type));
                    data_json_obj.put("document_belongs_to", (intent_doc_belongs_to));
                    data_json_obj.put("document_type", document_type);

                    main_json_obj.put("topic", "document-upload-andriod-app");
                    main_json_obj.put("subject", "Document Upload");
                    // driver uid
                    main_json_obj.put("id", sessionManager.get_driver_uid());
                    main_json_obj.put("eventType", "document-upload");
                    String date_n = new SimpleDateFormat("MM/dd/yyyy H:mm:ss", Locale.getDefault()).format(new Date());
                    main_json_obj.put("eventTime", "" + date_n);
                    main_json_obj.put("data", data_json_obj);
                    main_json_obj.put("dataVersion", "1.0");
                    main_json_obj.put("metadataVersion", "1.0");
                    main_json_obj.put("token", "" + sessionManager.get_token());


                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }

                    file_name = intent_doc_belongs_to + "-" +sessionManager.get_driver_id()+"-"+ document_type;
//                    file_name = sessionManager.getKeyDocumentBelongsTo() + "-" + sessionManager.getKeyDocumentType();

                final Handler handler = new Handler();
                Thread th = new Thread(new Runnable() {
                    public void run() {
                        try {
                            Log.e("azure_file_hashmap",": "+azure_file_hashmap.size());
                            url_arrayList.clear();
                            UploadImageFiles(azure_file_hashmap);
                            JSONArray url_json_array = new JSONArray();
                            for (String url : url_arrayList) {
                                url_json_array.put(url);
                            }
                            Log.e("url_json_array",": "+url_json_array.length());
                            data_json_obj.put("azure_event_trigger", 1);
                            data_json_obj.put("url", url_json_array);

                            File file = null;
                            try {

                                Log.e("file_name", " is " + file_name);

                                // Constructs a FileWriter given a file name, using the platform's default charset
                                file = new File(ImageHelper.root + "/" + file_name + ".json");
                                fileWriter[0] = new FileWriter(file);
                                fileWriter[0].write(main_json_obj.toString());

                                Log.e("data_json_obj",": "+data_json_obj.toString());
                                Log.e("main_json_obj",": "+main_json_obj.toString());

                            } catch (IOException e) {
                                Log.e("IOException",": "+e);
                                dialog.dismiss();
                                e.printStackTrace();

                            } finally {

                                try {
                                    if (fileWriter[0] != null) {
                                        Log.e("insideFileWriter",": ");
                                        fileWriter[0].flush();
                                        fileWriter[0].close();
                                    }

                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("IOException",": "+e);
                                    e.printStackTrace();
                                }
                            }

                            final String name = UploadJsonFile(file, file_name);

                            handler.post(new Runnable() {

                                public void run() {
                                    RequestedDocumentsTable db = new RequestedDocumentsTable(QueueActivity.this);
                                    db.deleteAllRecords();
                                    dialog.dismiss();
                                    Intent i = new Intent(QueueActivity.this, DashboardActivity.class);
                                    startActivity(i);
                                    finishAffinity();
                                    Toast.makeText(QueueActivity.this, "Data Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception ex) {
                            Log.e("Exception",": "+ex);
                            final String exceptionMessage = ex.getMessage();
                            handler.post(new Runnable() {
                                public void run() {
                                    RequestedDocumentsTable db = new RequestedDocumentsTable(QueueActivity.this);
                                    db.deleteAllRecords();
                                    dialog.dismiss();
                                    Toast.makeText(QueueActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                th.start();
                reInitializeUtilVariables();
                sessionManager.clearSessionForDirectUpload();
                hideProgressBar();
//                if (temp_progress != null) {
//                    temp_progress.setVisibility(View.GONE);
//                }


            }
        }
    }



    public void reInitializeUtilVariables() {
        Utils.Task_Type = "";
        Utils.Service_Hashmap = null;
        Utils.Service_Files_Array = null;
        Utils.Service_EMAIL_Hashmap = null;
        Utils.Service_Upload_Callback = null;
        Utils.activity_context = null;

        Utils.confirm_cancel_call = false;
        Utils.cancel_call = false;
        Utils.pause_call = false;
    }


    public static String UploadJsonFile(File file, String name) throws Exception {
        CloudBlobContainer container = getJsonContainer("traveloko-documents");
        container.createIfNotExists();

        CloudBlockBlob imageBlob = container.getBlockBlobReference(name + ".json");
        imageBlob.uploadFromFile(file.getAbsolutePath());

        return name;

    }

    public static void UploadImageFiles(HashMap<String, File> files) throws Exception {
        CloudBlobContainer container = getJsonContainer("images");

        container.createIfNotExists();
        //container.setMetadata(meta_data);
        Iterator it = files.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            CloudBlockBlob imageBlob = container.getBlockBlobReference(entry.getKey().toString());

            File file = (File) entry.getValue();
            String file_uri = imageBlob.getUri().toString();
            url_arrayList.add(file_uri);
            Log.e("uri", " is " + imageBlob.getUri());

            imageBlob.uploadFromFile(file.getAbsolutePath());

            it.remove(); // avoids a ConcurrentModificationException
        }

    }

    private static CloudBlobContainer getJsonContainer(String container_name) throws Exception {
        // Retrieve storage account from connection-string.

        CloudStorageAccount storageAccount = CloudStorageAccount
                .parse(Utils.storageConnectionString);

        // Create the blob client.
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        // Get a reference to a container.
        // The container name must be lower case
        CloudBlobContainer container = blobClient.getContainerReference(container_name);

        return container;
    }


//    @OnClick(R.id.upload_layout)
//    void uploadDocuments() {
//        if(networkValidator.isNetworkConnected()){
//            if(!db.checkEmptyTable()){
//
//
//                final ProgressDialog dialog = new ProgressDialog(QueueActivity.this, ProgressDialog.THEME_HOLO_DARK);
//                // this = YourActivity
//
//                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                dialog.setTitle("Fetching Response");
//                dialog.setMessage("Please wait. It will take a moment...");
//                dialog.setIndeterminate(true);
//                dialog.setCanceledOnTouchOutside(false);
//
//
//                showProgessBar();
//                Map<String, RequestBody> postFields = new HashMap<>();
//                postFields.put("document_id", requestBody(document_request_id));
//                postFields.put("document_type", requestBody(document_type));
//                postFields.put("document_belongs_to",requestBody(intent_doc_belongs_to));
//                postFields.put("is_expired",requestBody(intent_is_expired));
//
//
//                List<DocumentRequestModel> recordsFoeApi = db.getAllDocumentsByKey(intent_key);
//                for (DocumentRequestModel cn : recordsFoeApi) {
//                    try{
//                        temp_file = null;
//                        temp_file = new File(cn.getFile_url());
//                    }
//                    catch (Exception e){
//
//                    }
//                    files_array.add(temp_file);
//                }
//
//                for (int i = 0; i < files_array.size(); i++) {
//                    total_files_length += files_array.get(i).length();
//                }
//
//
//                Log.e("in upload method ","document_request_id "+document_request_id+" document_type "+document_type+
//                        " intent_doc_belongs_to "+intent_doc_belongs_to+" is_expired "+intent_is_expired+" image list "+files_array);
//
//// working right
//
//                FileUploader fileUploader = new FileUploader();
//
//                fileUploader.uploadFiles("document_upload", "", files_array, SessionManager.getInstance().get_token(),postFields,new FileUploader.FileUploaderCallback() {
//                    @Override
//                    public void onError() {
//                        Log.e("error","error");
//                        hideProgressBar();
//                    }
//
//                    @Override
//                    public void onFinish(String[] responses,String resp,boolean isSuccesful) {
//                        hideProgressBar();
////                        progressBarView.setVisibility(View.GONE);
//                        dialog.dismiss();
//                        RequestedDocumentsTable db = new RequestedDocumentsTable(QueueActivity.this);
//                        db.deleteAllRecords();
//
//
//
//
//                       if (isSuccesful){
//                            Intent goToDashBoard = new Intent(QueueActivity.this, DashboardActivity.class);
//                            startActivity(goToDashBoard);
//                            Toast.makeText(QueueActivity.this, resp, Toast.LENGTH_SHORT).show();
//                            finishAffinity();
//
//                        }
//                        else {
//                            Intent goToDashBoard = new Intent(QueueActivity.this, DashboardActivity.class);
//                            startActivity(goToDashBoard);
//                            Toast.makeText(QueueActivity.this, resp, Toast.LENGTH_SHORT).show();
//                            finishAffinity();
//                        }
//
//
//
////                        for(int i=0; i< responses.length; i++){
////                            String str = responses[i];
////                            Log.e("RESPONSE "+i, responses[i]);
////                        try {
////                          String strObj =  responses[0];
////                          JSONObject object = new JSONObject(strObj);
////                          Boolean response = (Boolean) object.get("status");
////                        if (response){
////                            Intent goToDashBoard = new Intent(QueueActivity.this, DashboardActivity.class);
////                            startActivity(goToDashBoard);
////                            Toast.makeText(QueueActivity.this, "Document Uploaded Successfully", Toast.LENGTH_SHORT).show();
////                            finishAffinity();
////
////                        }
////                        else {
////                            Intent goToDashBoard = new Intent(QueueActivity.this, DashboardActivity.class);
////                            startActivity(goToDashBoard);
////                            Toast.makeText(QueueActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
////                            finishAffinity();
////                        }
////                        }catch (Exception e){
////                            Log.e("Exception",": "+e);
////                            Intent goToDashBoard = new Intent(QueueActivity.this, DashboardActivity.class);
////                            startActivity(goToDashBoard);
////                            Toast.makeText(QueueActivity.this, "Internal server error", Toast.LENGTH_SHORT).show();
////
////                            finishAffinity();
////                        }
////                        }
//
//
//                    }
//
//                    @Override
//                    public void onProgressUpdate(int currentpercent, int totalpercent, int filenumber) {
//                        updateProgress(currentpercent,"Uploading file "+filenumber,"");
//                        Log.e("Progress Status", currentpercent+" "+totalpercent+" "+filenumber);
//                        if (currentpercent == 100){
////                            progressBarView.setVisibility(View.VISIBLE);
//                             dialog.show();
//                        }
//                        else {
//                            dialog.dismiss();
//                            progressBarView.setVisibility(View.GONE);
//                        }
//                    }
//                });
//
//
//
//
//
////                Intent mIntent = new Intent(this, FileUploadingJobIntentService.class);
////                Utils.activity_context = QueueActivity.this;
////                Utils.Task_Type = Utils.TASK_UPLOAD_REQUESTED_DOCUMENTS;
////                Utils.Service_Hashmap = postFields;
////                Utils.Service_Files_Array = files_array;
////                Utils.Service_Upload_Callback = QueueActivity.this;
////                FileUploadingJobIntentService.enqueueWork(this, mIntent);
////                AppController.total_upload = new long[files_array.size()];
//
//
//
//
//            }
//            else{
//                Toast.makeText(QueueActivity.this, ""+getResources().getString(R.string.no_data_in_table), Toast.LENGTH_SHORT).show();
//            }
//        }
//        else{
//            Toast.makeText(QueueActivity.this, ""+getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
//        }
//
//    }



    public void updateProgress(int val, String title, String msg){
        progressBar.setProgress(val);
        progressBar.setText(""+val+" %");
    }




//
//    @OnClick(R.id.upload_layout)
//    void uploadDocuments() {
//        if(networkValidator.isNetworkConnected()){
//            if(!db.checkEmptyTable()){
//                Map<String, RequestBody> postFields = new HashMap<>();
//                postFields.put("document_id", requestBody(document_request_id));
//                postFields.put("document_type", requestBody(document_type));
//                postFields.put("document_belongs_to",requestBody(intent_doc_belongs_to));
//                postFields.put("is_expired",requestBody(intent_is_expired));
//
//                Log.e("in upload method ","document_request_id "+document_request_id+" document_type "+document_type+
//                        " intent_doc_belongs_to "+intent_doc_belongs_to+" is_expired "+intent_is_expired);
//
//                List<DocumentRequestModel> recordsFoeApi = db.getAllDocumentsByKey(intent_key);
//                for (DocumentRequestModel cn : recordsFoeApi) {
//                    try{
//                        temp_file = null;
//                        temp_file = new File(cn.getFile_url());
//                    }
//                    catch (Exception e){
//
//                    }
//                    files_array.add(temp_file);
//                }
//
//                for (int i = 0; i < files_array.size(); i++) {
//                    total_files_length += files_array.get(i).length();
//                }
//
//                Intent mIntent = new Intent(this, FileUploadingJobIntentService.class);
//                Utils.activity_context = QueueActivity.this;
//                Utils.Task_Type = Utils.TASK_UPLOAD_REQUESTED_DOCUMENTS;
//                Utils.Service_Hashmap = postFields;
//                Utils.Service_Files_Array = files_array;
//                Utils.Service_Upload_Callback = QueueActivity.this;
//                FileUploadingJobIntentService.enqueueWork(this, mIntent);
//                AppController.total_upload = new long[files_array.size()];
//
//            }
//            else{
//                Toast.makeText(QueueActivity.this, ""+getResources().getString(R.string.no_data_in_table), Toast.LENGTH_SHORT).show();
//            }
//        }
//        else{
//            Toast.makeText(QueueActivity.this, ""+getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
//        }
//
//    }
//




    public void setCustomProgressZero(){
        hideProgressBar();
        progressBar.setProgress(0);
    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    @Override
    public void onProgressUpdate(long file_uploaded,int current_iteration) {


        long sum_file_total;
        if(current_iteration > previous_file_iterator){
            previous_file_iterator +=1;
            total_uploaded += file_uploaded_previous;
            //Log.e("in","iteration "+current_iteration);
        }
        sum_file_total = total_uploaded + file_uploaded;
        int percentage = (int)(100 * sum_file_total / total_files_length);
        //Log.e("in","uploaded => "+sum_file_total+" previous upload "+file_uploaded_previous+" total => "+total_files_length+" percentage => "+percentage);
        file_uploaded_previous = file_uploaded;
        progressBar.setProgress(percentage);
        progressBar.setText(""+percentage+" %");

        Log.e("onProgressUpdate ",": "+file_uploaded);

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        Log.e("onFinish ",": onFinish");

        progressBar.setProgress(100);
        progressBar.setText(""+100+" %");

    }

}
