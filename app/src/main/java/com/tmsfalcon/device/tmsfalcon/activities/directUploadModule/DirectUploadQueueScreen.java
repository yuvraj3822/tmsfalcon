package com.tmsfalcon.device.tmsfalcon.activities.directUploadModule;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.EmailSuggestionResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.adapters.DirectCameraQueueAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ProgressRequestBodyMultiple;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;
import com.tmsfalcon.device.tmsfalcon.services.FileUploadingJobIntentService;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mabbas007.tagsedittext.TagsEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectUploadQueueScreen extends NavigationBaseActivity implements ProgressRequestBodyMultiple.UploadCallbacks {

    ArrayList<String> imagesList = new ArrayList<>();
    ArrayList<String> dbIdList = new ArrayList<>();
    DirectUploadTable db;
    DirectCameraQueueAdapter adapter;
    ArrayList<File> files_array = new ArrayList<>();
    NetworkValidator networkValidator;
    AlertDialog emailAlertDialog, faxAlertDialog, result_alert, dispatchEmailSuggestionsAlert;
    CustomValidator customValidator = new CustomValidator(DirectUploadQueueScreen.this);
    List<String> emailSuggestionList = new ArrayList<>();
    List<String> dispachEmailSuggestionList = new ArrayList<>();
    String[] emailArr, dispatchEmailArr;
    String fetchedDisptachEmail = "";
    Call<PostResponse> call;
    long total_uploaded = 0;
    long total_files_length = 0;
    int previous_file_iterator = 0;
    static long file_uploaded_previous = 0;
    List<DocumentRequestModel> records, recordsForApi;
    SessionManager sessionManager;
    Map<String, List<File>> files_hashmap = new HashMap<>();
    Intent mIntent;
    boolean has_to_be_uploaded = false;
    public static ArrayList<String> url_arrayList = new ArrayList<>();

    String file_name = "";
    File file = null;
    HashMap<String, File> azure_file_hashmap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_direct_upload_queue_screen);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_direct_upload_queue_screen, null, false);
        drawer.addView(contentView, 0);
Log.e("here it is","");
        ButterKnife.bind(this);

        db = new DirectUploadTable(DirectUploadQueueScreen.this);
        sessionManager = new SessionManager(DirectUploadQueueScreen.this);
        records = db.getAllDocuments();
        networkValidator = new NetworkValidator(DirectUploadQueueScreen.this);
        for (DocumentRequestModel cn : records) {

            imagesList.add(cn.getFile_url());
            dbIdList.add(cn.getDb_id());
//            if(cn.getFile_url() != null){
//                try{
//                    File file = new File(cn.getFile_url());
//                    files_array.add(file);
//                }
//                catch (Exception e){
//
//                }
//            }
        }

        adapter = new DirectCameraQueueAdapter(DirectUploadQueueScreen.this, DirectUploadQueueScreen.this, imagesList, dbIdList);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        LocalBroadcastManager.getInstance(this).registerReceiver(serviceTaskReceiver,
                new IntentFilter("perform_job_intent_uploading_tasks"));


        Log.e("well", "well----------------------------------------------------------------------------------------------------"+imagesList.size());
    }

    private BroadcastReceiver serviceTaskReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String task = intent.getStringExtra("task");
            // Log.e("job intent receiver", "Got message: " + task);
            if (task.equals(Utils.SHOW_PROGRESS_BAR)) {
                showProgessBar();
            } else if (task.equals(Utils.HIDE_PROGRESS_BAR)) {
                Log.e("hide","broadcast");
                hideProgressBar();
            } else if (task.equals(Utils.SHOW_PROGRESS_TEXT)) {
                int percentage = intent.getIntExtra("percentage", 0);
                showProgressPercentage(percentage);
            }

        }
    };


    @Override
    protected void onResume() {

        super.onResume();
        adapter.notifyDataSetChanged();

    }

    public void showProgessBar() {
        Log.e("in", "showing progressbar");
        progress_layout.setVisibility(View.VISIBLE);
        //progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        Log.e("in", "hiding progressbar");
        progress_layout.setVisibility(View.GONE);
        // progressBar.setVisibility(View.GONE);
    }

    public void showProgressPercentage(int percentage) {
        progressBar.setProgress(percentage);
        progressBar.setText("" + percentage + " %");
    }

    @Override
    public void onProgressUpdate(long file_uploaded, int current_iteration) {
        long sum_file_total;
        if (current_iteration > previous_file_iterator) {
            previous_file_iterator += 1;
            total_uploaded += file_uploaded_previous;
            // Log.e("in","iteration "+current_iteration);
        }
        sum_file_total = total_uploaded + file_uploaded;
        int percentage = (int) (100 * sum_file_total / total_files_length);
        //Log.e("in","uploaded => "+sum_file_total+" previous upload "+file_uploaded_previous+" total => "+total_files_length+" percentage => "+percentage);
        file_uploaded_previous = file_uploaded;
        progressBar.setProgress(percentage);
        progressBar.setText("" + percentage + " %");
    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        //Log.e("in","onFinish");
        //progressBar.setProgress(100);
        //progressBar.setText(""+100+" %");

    }

    void showEmailPopUp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_email, null);
        dialogBuilder.setView(dialogView);
        Log.e("in ", Arrays.toString(emailArr));
        /*final MultiAutoCompleteTextView emailsTextView = (MultiAutoCompleteTextView) dialogView.findViewById(R.id.emailMultiAutoComplete);*/

        final TagsEditText emailsTextView = dialogView.findViewById(R.id.emailEditText);
        emailsTextView.setSeparator(",");
        emailsTextView.setTagsTextColor(R.color.blackOlive);
        emailsTextView.setTagsBackground(R.drawable.square_default);
        emailsTextView.setHintTextColor(getResources().getColor(R.color.blackOlive));
        emailsTextView.setHint("Enter Email");
        emailsTextView.setCloseDrawableRight(R.drawable.ic_close_black);
        if (emailArr != null && emailArr.length > 0) {
            ArrayAdapter<String> TopicName = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, emailArr);
            emailsTextView.setAdapter(TopicName);
            emailsTextView.setThreshold(2);
        }

        final EditText subject = dialogView.findViewById(R.id.subject);
        final EditText message = dialogView.findViewById(R.id.message);
        Button send_btn = dialogView.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailsTextView.getText().toString().length() == 0) {
                    emailsTextView.setError("Please Enter Email.");
                } else if (subject.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter Subject",
                            Toast.LENGTH_SHORT).show();
                }
           /* else if (message.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Please enter Message",
                        Toast.LENGTH_SHORT).show();
            }*/
                else {
                    String[] fax_array = emailsTextView.getText().toString().split(",");
                    for (int i = 0; i < fax_array.length; i++) {
                        fax_array[i] = fax_array[i].trim();
                        if (customValidator.isValidEmail(fax_array[i])) {
                            if (fax_array.length == i + 1) {
                                emailDocuments(emailsTextView.getText().toString(), subject.getText().toString(), message.getText().toString(), "email");
                                emailAlertDialog.dismiss();
                            }
                        } else {
                            emailsTextView.setError("Please Enter Valid Email.");
                        }
                    }
                }
            }
        });

        Button cancel_btn = dialogView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAlertDialog.dismiss();
            }
        });

        emailAlertDialog = dialogBuilder.create();
        //emailAlertDialog.setTitle("Send Documents By Email");
        emailAlertDialog.show();

    }

    void showDispatcherPopUp() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_email, null);
        dialogBuilder.setView(dialogView);
        final TagsEditText emailsTextView = dialogView.findViewById(R.id.emailEditText);
        emailsTextView.setSeparator(",");
        emailsTextView.setTagsTextColor(R.color.blackOlive);
        emailsTextView.setTagsBackground(R.drawable.square_default);
        emailsTextView.setHintTextColor(getResources().getColor(R.color.blackOlive));
        emailsTextView.setHint("Enter Email");
        emailsTextView.setCloseDrawableRight(R.drawable.ic_close_black);
        emailsTextView.setThreshold(2);

        emailsTextView.setText(fetchedDisptachEmail);

        final EditText subject = dialogView.findViewById(R.id.subject);
        final EditText message = dialogView.findViewById(R.id.message);
        Button send_btn = dialogView.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailsTextView.getText().toString().length() == 0) {
                    emailsTextView.setError("Please Enter Email.");
                } else if (subject.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter Subject",
                            Toast.LENGTH_SHORT).show();
                }
               /* else if (message.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter Message",
                            Toast.LENGTH_SHORT).show();
                }*/
                else {
                    String[] fax_array = emailsTextView.getText().toString().split(",");
                    for (int i = 0; i < fax_array.length; i++) {
                        fax_array[i] = fax_array[i].trim();
                        if (customValidator.isValidEmail(fax_array[i])) {
                            if (fax_array.length == i + 1) {
                                emailDocuments(emailsTextView.getText().toString(), subject.getText().toString(), message.getText().toString(), "dispatcher");
                                emailAlertDialog.dismiss();
                            }
                        } else {
                            emailsTextView.setError("Please Enter Valid Email.");
                        }
                    }
                }
            }
        });

        Button cancel_btn = dialogView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAlertDialog.dismiss();
            }
        });

        emailAlertDialog = dialogBuilder.create();
        //emailAlertDialog.setTitle("Send Documents By Email");
        emailAlertDialog.show();
    }

    public void showDispatchEmailListView() {
        if (dispatchEmailArr != null && dispatchEmailArr.length > 0) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_listview, null);
            dialogBuilder.setView(dialogView);
            final ListView listView = dialogView.findViewById(R.id.list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dispatchEmailArr);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    fetchedDisptachEmail = (String) listView.getItemAtPosition(position);
                    dispatchEmailSuggestionsAlert.dismiss();
                    showDispatcherPopUp();
                }
            });
            dispatchEmailSuggestionsAlert = dialogBuilder.create();
            dispatchEmailSuggestionsAlert.show();
        } else {
            fetchedDisptachEmail = "";
            showDispatcherPopUp();
            Toast.makeText(DirectUploadQueueScreen.this, "No Dispatchers Found.", Toast.LENGTH_SHORT).show();
        }
    }

    public void performBackAction() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Do you really want to cancel this request?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.deleteAllRecords();
                        sessionManager.clearSessionForDirectUpload();
                        Intent i = new Intent(DirectUploadQueueScreen.this, DashboardActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }


    @Bind(R.id.grid_view)
    GridView gridView;
    @Bind(R.id.progress_bar)
    TextProgressBar progressBar;
    @Bind(R.id.progress_layout)
    LinearLayout progress_layout;
    @Bind(R.id.temp_progress)
    ProgressBar temp_progress;
   /* @Bind(R.id.add_more)
    LinearLayout add_more_images;*/
    /*@Bind(R.id.send_to_dispatcher_layout)
    LinearLayout send_to_dispatcher_layout;*/


    @Override
    public void onBackPressed() {
        //performBackAction();
        db.deleteAllRecords();
        sessionManager.clearSessionForDirectUpload();
        /*if(has_to_be_uploaded ){
            FileUploadingJobIntentService.enqueueWork(this, mIntent);
        }*/
        Intent intent = new Intent(DirectUploadQueueScreen.this, DashboardActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        db.deleteAllRecords();
        sessionManager.clearSessionForDirectUpload();
       /* if(has_to_be_uploaded){
            FileUploadingJobIntentService.enqueueWork(this, mIntent);
        }*/
        Intent intent = new Intent(DirectUploadQueueScreen.this, DashboardActivity.class);
        startActivity(intent);
        //performBackAction();
    }

    /*@OnClick(R.id.send_to_dispatcher_layout)
    void fetchDispatchers(){
        temp_progress.setVisibility(View.VISIBLE);
        Log.e("token ",SessionManager.getInstance().get_token());
        RestClient.get().fetchDispatchersEmail(SessionManager.getInstance().get_token()).enqueue(new Callback<FetchDispatcherResponse>() {
            @Override
            public void onResponse(Call<FetchDispatcherResponse> call, Response<FetchDispatcherResponse> response) {
                temp_progress.setVisibility(View.GONE);
                //Log.e("dipatcher response ", new Gson().toJson(response));
                if(response.body() != null || response.isSuccessful()){
                    if(response.body().getStatus()){
                        dispachEmailSuggestionList = response.body().getMessages().getData();
                        dispatchEmailArr = dispachEmailSuggestionList.toArray(new String[dispachEmailSuggestionList.size()]);
                        if(dispatchEmailArr.length == 1){
                            fetchedDisptachEmail = dispatchEmailArr[0];
                            showDispatcherPopUp();
                        }
                        else{
                            showDispatchEmailListView();
                        }
                        //showDispatcherPopUp();
                    }
                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(DirectUploadQueueScreen.this,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<FetchDispatcherResponse> call, Throwable t) {
                temp_progress.setVisibility(View.GONE);
                Log.e("server call error",t.getMessage());
            }
        });
    }*/

    @OnClick(R.id.send_to_upload_layout)
    void uploadDocs() {

//        File temp_file = null;
//        Map<String, String> postFields = new HashMap<>();
//        postFields.put("subject", "");
//        postFields.put("device_type", "android");
//        postFields.put("message", "");
//        postFields.put("type", "upload");
//        postFields.put("tags", "");
//        postFields.put("document_belongs_type", (sessionManager.getKeyDocumentId()));
//        postFields.put("document_belongs_to", (sessionManager.getKeyDocumentBelongsTo()));
//        postFields.put("document_type", (sessionManager.getKeyDocumentType()));
//
//
//        List<DocumentRequestModel> recordsFoeApi = db.getAllDocuments();
//        for (String cn : imagesList) {
//            try {
//
//                temp_file = new File(cn);
//            } catch (Exception e) {
//
//            }
//            files_array.add(temp_file);
//
//
//
//        }
//
//        for (int i = 0; i < files_array.size(); i++) {
//            total_files_length += files_array.get(i).length();
//        }
//
//

        emailDocuments("", "", "", "upload");

    }

  /*  @OnClick(R.id.add_more_images)
    void AddMoreImages() {

        if(imagesList.size() < 8){
            Intent captureIntent = new Intent(DirectUploadQueueScreen.this, DirectUploadCameraScreen.class);
            startActivity(captureIntent);
        }
        else{
            Toast.makeText(DirectUploadQueueScreen.this,R.string.maximum_limit_exceed,Toast.LENGTH_LONG).show();
        }


    }*/

    @OnClick(R.id.email_layout)
    void fetchEmailSuggestions() {
        temp_progress.setVisibility(View.VISIBLE);
        RestClient.get().fetchEmailSuggestions(SessionManager.getInstance().get_token()).enqueue(new Callback<EmailSuggestionResponse>() {
            @Override
            public void onResponse(Call<EmailSuggestionResponse> call, Response<EmailSuggestionResponse> response) {
                temp_progress.setVisibility(View.GONE);
                Log.e("emails suggestions ", new Gson().toJson(response));
                if (response.body() != null || response.isSuccessful()) {

                    if (response.body().getStatus()) {
                        Log.e("response", String.valueOf(response.body()));
                        emailSuggestionList = response.body().getData().getEmails();
                        emailArr = emailSuggestionList.toArray(new String[emailSuggestionList.size()]);

                        showEmailPopUp();
                    } else {

                    }
                } else {
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(DirectUploadQueueScreen.this, error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showEmailPopUp();
                }
            }

            @Override
            public void onFailure(Call<EmailSuggestionResponse> call, Throwable t) {
                temp_progress.setVisibility(View.GONE);
                Log.e("server call error", t.getMessage());
                showEmailPopUp();
            }
        });
    }

    @OnClick(R.id.fax_layout)
    void showFaxPopUp() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_fax, null);
        dialogBuilder.setView(dialogView);

       /* final MultiAutoCompleteTextView mTagsEditText = (MultiAutoCompleteTextView) dialogView.findViewById(R.id.faxMultiAutoComplete);
        mTagsEditText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());*/
      /*  final EditText subject = dialogView.findViewById(R.id.subject);
        final EditText message = dialogView.findViewById(R.id.message);*/
        final EditText mTagsEditText = dialogView.findViewById(R.id.tagsEditText);

        mTagsEditText.setHintTextColor(getResources().getColor(R.color.blackOlive));
        mTagsEditText.setHint("Enter Fax Number");
        Button send_btn = dialogView.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTagsEditText.getText().toString().length() == 0) {
                    mTagsEditText.setError("Please Enter Fax Number");
                }
               /*else if (subject.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter Subject",
                            Toast.LENGTH_SHORT).show();
               }*/
               /*else if (message.getText().toString().length() == 0) {
                       Toast.makeText(getApplicationContext(),
                               "Please enter Message",
                               Toast.LENGTH_SHORT).show();
               }*/
                else {
                    String fax_no = mTagsEditText.getText().toString();
                    if (customValidator.isValidFax(fax_no)) {
                        emailDocuments(mTagsEditText.getText().toString(), "", "", "fax");
                        faxAlertDialog.dismiss();
                    } else {
                        mTagsEditText.setError("Please Enter Valid Fax Number");
                    }
                }

            }
        });

        Button cancel_btn = dialogView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                faxAlertDialog.dismiss();
            }
        });

        faxAlertDialog = dialogBuilder.create();
        faxAlertDialog.show();
    }

    void sendBroadcastForCancelUploading(String task) {
        Intent intent = new Intent("perform_cancel_call_request");
        // You can also include some extra data.
        intent.putExtra("task", task);
        //Log.e("check instance 2",""+AppController.getInstance().getApplicationContext());
        LocalBroadcastManager.getInstance(AppController.getInstance().getApplicationContext()).sendBroadcast(intent);
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

    @OnClick(R.id.upload_cancel)
    void cancelUpload() {

        Log.e("hide","aler");
        Utils.pause_call = true;
        new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Do you really want to cancel the uploading?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Utils.confirm_cancel_call = true;
                        sendBroadcastForCancelUploading(Utils.CANCEL_UPLOADING);
                        hideProgressBar();
                        Log.e("in", "request cancelled");
                        dialog.dismiss();
                        reInitializeUtilVariables();
                        Intent i = new Intent(DirectUploadQueueScreen.this, DashboardActivity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    public void emailDocument(String email_str, String subject, String message, String type) {
        if (networkValidator.isNetworkConnected()) {
            if (!db.checkEmptyTable()) {

               // Map<String, RequestBody> postFields = new HashMap<>();
//                postFields.put("subject", requestBody(subject));
//                postFields.put("message", requestBody(message));
//                postFields.put("type", requestBody(type));
//                postFields.put("tags", requestBody(email_str));
//                postFields.put("document_belongs_type", requestBody(sessionManager.getKeyDocumentId()));
//                postFields.put("document_belongs_to", requestBody(sessionManager.getKeyDocumentBelongsTo()));
//                postFields.put("document_type", requestBody(sessionManager.getKeyDocumentType()));

                Map<String, String> postFields = new HashMap<>();
                                postFields.put("subject", subject);
                postFields.put("message", message);
                postFields.put("type", type);
                postFields.put("tags", email_str);
                postFields.put("document_belongs_type", sessionManager.getKeyDocumentId());
                postFields.put("document_belongs_to", sessionManager.getKeyDocumentBelongsTo());
                postFields.put("document_type", sessionManager.getKeyDocumentType());


                mIntent = new Intent(this, FileUploadingJobIntentService.class);
                Utils.activity_context = DirectUploadQueueScreen.this;
                Utils.Task_Type = Utils.Task_UPLOAD_CAPTURED_DOCUMENTS;
                Utils.Service_EMAIL_Hashmap = postFields;
                Utils.Service_Files_Array = files_array;
                Utils.Service_Upload_Callback = DirectUploadQueueScreen.this;
                FileUploadingJobIntentService.enqueueWork(this, mIntent);
                AppController.total_upload = new long[files_array.size()];


            }
        } else {
            Toast.makeText(DirectUploadQueueScreen.this, "" + getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }


    public void emailDocuments(String email_str, String subject, String message, String type) {
        if (networkValidator.isNetworkConnected()) {
            if (!db.checkEmptyTable()) {

               /* Map<String, RequestBody> postFields = new HashMap<>();
                postFields.put("subject", requestBody(subject));
                postFields.put("message", requestBody(message));
                postFields.put("type", requestBody(type));
                postFields.put("tags", requestBody(email_str));
                postFields.put("document_belongs_type", requestBody(sessionManager.getKeyDocumentId()));
                postFields.put("document_belongs_to", requestBody(sessionManager.getKeyDocumentBelongsTo()));
                postFields.put("document_type", requestBody(sessionManager.getKeyDocumentType()));*/

//                int colorCodeDark = Color.parseColor("#EEEEEE");
//                temp_progress.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        temp_progress.setVisibility(View.VISIBLE);
//                    }
//                });


                final ProgressDialog dialog = new ProgressDialog(DirectUploadQueueScreen.this, ProgressDialog.THEME_HOLO_DARK);
                // this = YourActivity

                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setTitle("Uploading");
                dialog.setMessage("Please wait. It will take a moment...");
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();




                Map<String, String> postFields = new HashMap<>();
                postFields.put("subject", (subject));
                postFields.put("device_type", "android");
                postFields.put("message", (message));
                postFields.put("type", (type));
                postFields.put("tags", (email_str));
                postFields.put("document_belongs_type", (sessionManager.getKeyDocumentId()));
                postFields.put("document_belongs_to", (sessionManager.getKeyDocumentBelongsTo()));
                postFields.put("document_type", (sessionManager.getKeyDocumentType()));
                List<MultipartBody.Part> multipartImg = new ArrayList<>();

                recordsForApi = db.getAllDocuments();
                Log.e("data ", postFields.toString() + " tags " + email_str);
                int temp_i = 1;
                for (DocumentRequestModel cn : recordsForApi) {
                    File file;
                    if (cn.getFile_url() != null) {
                        try {
                            file = new File(cn.getFile_url());
                            files_array.add(file);
                            String temp_name = "";
                            if (sessionManager.getKeyDocumentId() != null && sessionManager.getKeyDocumentId() != "" && sessionManager.getKeyDocumentId().length() > 0) {
                                temp_name = sessionManager.getKeyDocumentBelongsTo() + "-"
                                        + sessionManager.getKeyDocumentId() + "-"
                                        + sessionManager.getKeyDocumentType() + "-" + temp_i + ".jpg";
                            } else {
                                temp_name = sessionManager.getKeyDocumentBelongsTo() + "-"
                                        + sessionManager.getKeyDocumentType() + "-" + temp_i + ".jpg";
                            }

                            azure_file_hashmap.put(temp_name, file);
                            temp_i++;
                        } catch (Exception e) {

                            dialog.dismiss();
                        }
                    }
                }

                Log.e("azuresSize",": "+recordsForApi.size());
                for (int i = 0; i < files_array.size(); i++) {
                    total_files_length += files_array.get(i).length();
                }
                final FileWriter[] fileWriter = {null};


                final JSONObject main_json_obj = new JSONObject();
                final JSONObject data_json_obj = new JSONObject();


                try {
                    data_json_obj.put("device_type", "android");
                    data_json_obj.put("message", (message));
                    data_json_obj.put("type", (type));
                    data_json_obj.put("tags", (email_str));
                    data_json_obj.put("document_belongs_type", (sessionManager.getKeyDocumentId()));
                    data_json_obj.put("document_belongs_to", (sessionManager.getKeyDocumentBelongsTo()));
                    data_json_obj.put("document_type", (sessionManager.getKeyDocumentType()));

                    main_json_obj.put("topic", "document-upload-andriod-app");
                    main_json_obj.put("subject", "Document Upload");
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

                if (sessionManager.getKeyDocumentId() != null && sessionManager.getKeyDocumentId() != "" && sessionManager.getKeyDocumentId().length() > 0) {
                    file_name = sessionManager.getKeyDocumentBelongsTo() + "-" + sessionManager.getKeyDocumentId() + "-" + sessionManager.getKeyDocumentType();
                } else {
                    file_name = sessionManager.getKeyDocumentBelongsTo() + "-" + sessionManager.getKeyDocumentType();
                }


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

                            try {

                                Log.e("file_name", " is " + file_name);

                                // Constructs a FileWriter given a file name, using the platform's default charset
                                file = new File(ImageHelper.root + "/" + file_name + ".json");
                                fileWriter[0] = new FileWriter(file);
                                fileWriter[0].write(main_json_obj.toString());

                                Log.e("data_json_obj",": "+data_json_obj.toString());
                                Log.e("main_json_obj",": "+main_json_obj.toString());

                            } catch (IOException e) {
                                dialog.dismiss();
                                Log.e("IOException",": "+e);
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
                                    dialog.dismiss();
                                    Intent i = new Intent(DirectUploadQueueScreen.this, DashboardActivity.class);
                                    startActivity(i);
                                    finishAffinity();
                                    Toast.makeText(DirectUploadQueueScreen.this, "Data Uploaded Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception ex) {
                            dialog.dismiss();
                            Log.e("Exception",": "+ex);
                            final String exceptionMessage = ex.getMessage();
                            handler.post(new Runnable() {
                                public void run() {

                                    Toast.makeText(DirectUploadQueueScreen.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                th.start();
                reInitializeUtilVariables();
                sessionManager.clearSessionForDirectUpload();
                if (temp_progress != null) {
                    temp_progress.setVisibility(View.GONE);
                }

/*
                mIntent = new Intent(this, FileUploadingJobIntentService.class);
                Utils.activity_context = DirectUploadQueueScreen.this;
                Utils.Task_Type = Utils.Task_UPLOAD_CAPTURED_DOCUMENTS;
                Utils.Service_EMAIL_Hashmap = postFields;
                Utils.Service_Files_Array = files_array;
                Utils.Service_Upload_Callback = DirectUploadQueueScreen.this;
                FileUploadingJobIntentService.enqueueWork(this, mIntent);
                AppController.total_upload = new long[files_array.size()];*/


            }
        } else {
            Toast.makeText(DirectUploadQueueScreen.this, "" + getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceTaskReceiver);

    }

}
