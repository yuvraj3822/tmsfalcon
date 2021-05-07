package com.tmsfalcon.device.tmsfalcon.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.activities.QueueActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.ProgressRequestBodyMultiple;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;
import com.tmsfalcon.device.tmsfalcon.database.RequestedDocumentsTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dell on 5/6/2019.
 */

public class FileUploadingJobIntentService extends JobIntentService {

    private static final String TAG = "FileUploadingJobIntentService";
    private static final int JOB_ID = 2;
    final Handler mHandler = new Handler();
    String task_type;
    Map<String,RequestBody> task_params;
    Map<String,String> task_email_params;
    ArrayList<File> files_array = new ArrayList<>();
    long total_files_length = 0;
    Call<PostResponse> call;
    ProgressRequestBodyMultiple.UploadCallbacks callback_listener;
    Context activity_context;
    QueueActivity queueActivity = new QueueActivity();
    List<MultipartBody.Part> multipartImg = new ArrayList<>();
    private static BroadcastReceiver serviceTaskReceiver;
    Map<String, List<File>> files_hashmap = new HashMap<>();

    public  FileUploadingJobIntentService(){
        super();
        task_type = Utils.Task_Type;
        task_params = Utils.Service_Hashmap;
        task_email_params = Utils.Service_EMAIL_Hashmap;
        files_array = Utils.Service_Files_Array;
        callback_listener = Utils.Service_Upload_Callback;
        activity_context = Utils.activity_context;
    }

    public static void enqueueWork(Context context, Intent intent) {

        enqueueWork(context, FileUploadingJobIntentService.class, JOB_ID, intent);
    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        serviceTaskReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String task = intent.getStringExtra("task");
                Log.e("job intent receiver", "Got message: " + task);
                if(task.equals(Utils.CANCEL_UPLOADING)){
                    Log.e("CANCEL_UPLOADING","yes");
                    call.cancel();
                    if(call.isCanceled()){
                        Log.e("isCanceled","yes");
                    }
                    else{
                        Log.e("isCanceled","no");
                    }
                }
                else{
                    Log.e("CANCEL_UPLOADING","no");
                }

            }
        };
        Log.e("Utils.cancel_call",""+Utils.cancel_call);
        //Log.e("check instance 1",""+AppController.getInstance().getApplicationContext());
        LocalBroadcastManager.getInstance(AppController.getInstance().getApplicationContext()).registerReceiver(serviceTaskReceiver,
                new IntentFilter("perform_cancel_call_request"));

        if(Utils.cancel_call){
            //showCancelDialog();
        }
        else{
            if(task_type == Utils.TASK_UPLOAD_REQUESTED_DOCUMENTS){
                uploadDocuments(Utils.Task_Type,task_params,files_array);
            }
            else if(task_type == Utils.Task_UPLOAD_CAPTURED_DOCUMENTS){
                emailDocuments(Utils.Task_Type,task_email_params,files_array);
            }

        }
    }

    @Override
    public void setInterruptIfStopped(boolean interruptIfStopped) {
        super.setInterruptIfStopped(interruptIfStopped);
    }

    @Override
    public boolean isStopped() {
        return super.isStopped();
    }

    @Override
    public boolean onStopCurrentWork() {
        return super.onStopCurrentWork();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //showToast("Job Execution Started");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("FileUploadingService","in destroy");
        LocalBroadcastManager.getInstance(AppController.getInstance().getApplicationContext()).unregisterReceiver(serviceTaskReceiver);
        // showToast("Job Execution Finished");
    }

    void showToast(final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FileUploadingJobIntentService.this, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    void sendBroadcast(String task){
        Intent intent = new Intent("perform_job_intent_uploading_tasks");
        // You can also include some extra data.
        intent.putExtra("task", task);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void sendProgressUpdateBroadcast(String task,int percentage){
        Intent intent = new Intent("perform_job_intent_uploading_tasks");
        // You can also include some extra data.
        intent.putExtra("task", task);
        intent.putExtra("percentage",percentage);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    void showCancelDialog() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(activity_context)
                        .setTitle("Cancel")
                        .setMessage("Do you really want to cancel the uploading?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                call.cancel();
                                setInterruptIfStopped(true);
                                sendBroadcast(Utils.HIDE_PROGRESS_BAR);

                                Log.e("in","request cancelled");
                                dialog.dismiss();
                            }})
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    void uploadDocuments(final String task_type, Map<String,RequestBody> params, ArrayList<File> files_array) {

        Map<String, RequestBody> postFields = params;




        handleFileProcessing();

        sendBroadcast(Utils.SHOW_PROGRESS_BAR);

        Log.e("session token ", SessionManager.getInstance().get_token());
        call = RestClient.get().uploadDocument(SessionManager.getInstance().get_token(), multipartImg, postFields);




        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                // Log.e("gson to json success",new Gson().toJson(response));
                handleOnRespnse(response);
                sendBroadcast(Utils.HIDE_PROGRESS_BAR);
                //queueActivity.progressBar.setProgress(0);
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                sendBroadcast(Utils.HIDE_PROGRESS_BAR);
                //queueActivity.progressBar.setProgress(0);
                handleOnFailure(t);

            }
        });
        reInitializeUtilVariables();

    }

    void emailDocuments(final String task_type, Map<String,String> params, ArrayList<File> files_array) {

        Map<String, String> postFields = params;

        sendBroadcast(Utils.SHOW_PROGRESS_BAR);

        Log.e("session token ", SessionManager.getInstance().get_token());
        files_hashmap.put("fileUpload[]",files_array);

        ANRequest.MultiPartBuilder getRequestBuilder = new ANRequest.MultiPartBuilder(UrlController.SEND_EMAIL);
        getRequestBuilder.addHeaders("Token", SessionManager.getInstance().get_token());

        getRequestBuilder.addMultipartParameter(params);
        Log.e("files_array"," is "+files_array.size());
        getRequestBuilder.addMultipartFileList(files_hashmap);

        ANRequest anRequest = getRequestBuilder.build();

        anRequest.setUploadProgressListener(new UploadProgressListener() {
            @Override
            public void onProgress(long bytesUploaded, long totalBytes) {
                int percentage = (int) (bytesUploaded * 100 / totalBytes);
                sendProgressUpdateBroadcast(Utils.SHOW_PROGRESS_TEXT,percentage);

            }
        })
        .getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                // accident_progress_layout.setVisibility(View.GONE);
                Log.e("new response", " is "+String.valueOf(response));
                try {
                    boolean status = response.getBoolean("status");
                    JSONArray messagesList = response.optJSONArray("messages");
                    Log.e("response",response.toString());
                    String messages = "";
                    for(int i = 0; i<messagesList.length();i++){
                        messages += messagesList.get(i);
                    }
                    if(status){

                        DirectUploadTable db = new DirectUploadTable(activity_context);
                        db.deleteAllRecords();
                        SessionManager sessionManager = new SessionManager(activity_context);
                        sessionManager.clearSessionForDirectUpload();
                        showToast(messages);
                        Intent goToDashBoard = new Intent(activity_context, DashboardActivity.class);
                        goToDashBoard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(goToDashBoard);

                    }
                    else{
                        showToast(messages);
                    }
                    sendBroadcast(Utils.HIDE_PROGRESS_BAR);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                sendBroadcast(Utils.HIDE_PROGRESS_BAR);
                Log.e("error",anError.getErrorBody());
                Log.e("error detail",anError.getErrorDetail());
                String error_body = anError.getErrorBody();
                String msg = "";
                if(error_body != null){
                    msg = error_body;
                }
                else{
                    msg = "Some Error Occured.Please Try Again Later!";
                }
                showToast(msg);

            }
        });

        /*call = RestClient.get().emailDocuments(SessionManager.getInstance().get_token(), multipartImg, postFields);

        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                // Log.e("gson to json success",new Gson().toJson(response));
                handleOnRespnse(response);

                sendBroadcast(Utils.HIDE_PROGRESS_BAR);
                //queueActivity.progressBar.setProgress(0);
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                sendBroadcast(Utils.HIDE_PROGRESS_BAR);
                //queueActivity.progressBar.setProgress(0);
                handleOnFailure(t);

            }
        });*/
        reInitializeUtilVariables();

    }


    public void handleFileProcessing(){

        AppController.total_upload = new long[files_array.size()];
        for (int i = 0; i < files_array.size(); i++) {
            total_files_length += files_array.get(i).length();
            ProgressRequestBodyMultiple fileBody = null;

            fileBody = new ProgressRequestBodyMultiple(files_array.get(i),i, callback_listener);

//
//            TestToProgressRequestBodyObservable fileBody = null;
//            fileBody = new TestToProgressRequestBodyObservable(files_array.get(i));

            //RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), files_array.get(i));
            MultipartBody.Part multipart = MultipartBody.Part.createFormData("fileUpload[" + i + "]", files_array.get(i).getName(), fileBody);
            multipartImg.add(multipart);

        }
    }

    public void reInitializeUtilVariables(){
        Utils.Task_Type = "";
        Utils.Service_Hashmap = null;
        Utils.Service_Files_Array = null;
        Utils.Service_Upload_Callback = null;
        Utils.activity_context = null;
        Utils.cancel_call = false;
    }

    public void handleOnFailure(Throwable t){
        Log.e("server error cause"," "+t.getCause());
        Log.e("server call error"," "+t.getMessage());
        Log.e("getLocalizedMessage"," "+t.getLocalizedMessage()+" cause "+t.getCause()+" call "+call);
        showToast("Response Call Failed " + t.getMessage());
    }

    public void handleOnRespnse(Response<PostResponse> response){
        Log.e("response body ",response.toString());
        if(response.body() != null || response.isSuccessful()){
            List messagesList = response.body().getMessages();
            String messages = "";
            for(int i = 0; i<messagesList.size();i++){
                messages += messagesList.get(i);
            }
            if (response.body().getStatus()) {
                if(task_type == Utils.TASK_UPLOAD_REQUESTED_DOCUMENTS){
                    RequestedDocumentsTable db = new RequestedDocumentsTable(activity_context);
                    db.deleteAllRecords();
                }
                else if(task_type == Utils.Task_UPLOAD_CAPTURED_DOCUMENTS){
                    DirectUploadTable db = new DirectUploadTable(activity_context);
                    db.deleteAllRecords();
                    SessionManager sessionManager = new SessionManager(activity_context);
                    sessionManager.clearSessionForDirectUpload();
                }

                Intent goToDashBoard = new Intent(activity_context, DashboardActivity.class);
                goToDashBoard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(goToDashBoard);

            }
            showToast(messages);

        }
        else {
            try {
                String error_string = response.errorBody().string();
                ErrorHandler.setRestClientMessage(activity_context,error_string);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
