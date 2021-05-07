package com.tmsfalcon.device.tmsfalcon.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestCameraScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentDetail extends NavigationBaseActivity{

    SessionManager session;
    NetworkValidator networkValidator;
    String intent_module,intent_doc_id;
    String name;
    String document_name;
    String doc_name;
    String due_date;
    String rejected_on;
    String reason;
    String code;
    String doc_type;
    String document_request_id;
    String file_url;
    String file_type_string;
    PermissionManager permissionsManager = new PermissionManager();
    public static final int PERMISSIONS_REQUEST_CODE = 123;
    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    String uploadedDirPath = root+"/"+"downloaded_docs";
    String title,comment,doc_status,key,document_id,encrypted_doc_id;

    private static final int REQUEST_CAMERA_PERMISSION = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_document_detail);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_document_detail, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        intent_module = getIntent().getExtras().getString("module","");
        intent_doc_id = getIntent().getExtras().getString("doc_id","");
        Log.e("data ","intent_module "+intent_module+" intent_doc_id "+intent_doc_id);
        if(networkValidator.isNetworkConnected()){
           if(intent_doc_id != null){
               getDocumentData();
           }
           else{
                no_data_textview.setVisibility(View.VISIBLE);
                no_data_textview.setText("Invalid Data to proceed further.");
           }
        }
        else {
           Toast.makeText(DocumentDetail.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    private void getDocumentData() {

        showProgessBar();
        String tag_document_detail = "fetch_document_detail";
        String url = UrlController.FETCH_DOCUMENT_DETAIL;
        Map<String, String> params = new HashMap<>();
        params.put("document_id", intent_doc_id);
        Log.e("rejected",": "+intent_doc_id);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){

                                data_json = response.getJSONObject("data");

                                if(data_json != null && data_json.length() > 0){
                                    main_layout.setVisibility(View.VISIBLE);
                                    doc_name = data_json.optString("documents");
                                    document_id  =data_json.optString("document_id");
                                    due_date = data_json.optString("document_due_date");
                                    rejected_on = data_json.optString("updated_at");
                                    reason = data_json.optString("reason");
                                    code = data_json.optString("document_code");
                                    doc_type = data_json.optString("document_belongs_to");
                                    document_request_id = data_json.optString("document_request_id");
                                    title = data_json.optString("title");
                                    comment = data_json.optString("comment");
                                    doc_status = data_json.optString("status");
                                    encrypted_doc_id = data_json.optString("encrypted_doc_id");

                                    JSONObject view_object = data_json.getJSONObject("view");
                                    file_url = view_object.optString("url");
                                    file_type_string = view_object.optString("type");

                                    doc_name_textview.setText(doc_name);
                                    document_due_date_textview.setText(due_date);
                                    rejected_on_textview.setText(rejected_on);
                                    rejected_reason_textview.setText(reason);
                                    code_textview.setText(code);
                                    doc_type_textview.setText(doc_type);

                                }
                                else{
                                    no_data_textview.setVisibility(View.VISIBLE);
                                }


                            }
                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        hideProgressBar();
                    }
                }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error","volleryerror");
                ErrorHandler.setVolleyMessage(DocumentDetail.this,error);
                hideProgressBar();

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_document_detail);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(networkValidator.isNetworkConnected()){
                       view_function();
                    }
                    else {
                        Toast.makeText(DocumentDetail.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(DocumentDetail.this, "Allow Permission to view files.", Toast.LENGTH_SHORT).show();
                }
            break;
            }
            case REQUEST_CAMERA_PERMISSION: {
                grantPermisions(grantResults);
                break;
            }




        }
    }



    private void  grantPermisions(int[] grantResults){

        if(grantResults.length == 2) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                Log.e("Granted", "Granted");
//                selectImage(this@TestDashboardActivity)
//
//                val i = Intent(this@DashboardActivity, TestCameraScreen::class.java)
//                startActivity(i)
                redirection();



            } else {
                Toast.makeText(DocumentDetail.this, "Please provide the permission to access Camera", Toast.LENGTH_SHORT).show();
            }
        }else{
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.e("Granted", "Granted");
                //  selectImage(this@TestDashboardActivity)
//                val i = Intent(DocumentDetail.this, TestCameraScreen::class.java)
//                startActivity(i)

                redirection();


            }else {
                Toast.makeText(DocumentDetail.this, "Please provide the permission to access Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void initIds(){

        networkValidator = new NetworkValidator(DocumentDetail.this);
        session = new SessionManager(DocumentDetail.this);

    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    @Bind(R.id.document_name)
    TextView doc_name_textview;

    @Bind(R.id.document_type)
    TextView doc_type_textview;

    @Bind(R.id.code)
    TextView code_textview;

    @Bind(R.id.document_due_date)
    TextView document_due_date_textview;

    @Bind(R.id.rejected_on)
    TextView rejected_on_textview;

    @Bind(R.id.rejected_reason)
    TextView rejected_reason_textview;

    @Bind(R.id.main_layout)
    LinearLayout main_layout;

    @Bind(R.id.progress_bar_percent)
    TextProgressBar textProgressBar;

    @Bind(R.id.progress_layout)
    LinearLayout progress_layout;

    @OnClick(R.id.view_btn)
    void performViewFunction(){
        if (!permissionsManager.checkPermission(DocumentDetail.this, DocumentDetail.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionsManager.askForPermission(DocumentDetail.this, DocumentDetail.this, Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSIONS_REQUEST_CODE);
        }
        else{
            view_function();
        }

    }


    @OnClick(R.id.renew_btn)
    void performRenewFunction(){
//        Intent goToCapture = new Intent(DocumentDetail.this, CaptureDocument.class);

        if (initPermission()) {
            redirection();

        }

    }



    private void redirection(){
        Intent goToCapture = new Intent(DocumentDetail.this, TestCameraScreen.class);
        goToCapture.putExtra("document_request_id", encrypted_doc_id);
        goToCapture.putExtra("document_type", code);
        goToCapture.putExtra("document_name", doc_name);
        goToCapture.putExtra("load_number", title);
        goToCapture.putExtra("due_date", due_date);
        goToCapture.putExtra("comment", comment);
        goToCapture.putExtra("status", doc_status);
        goToCapture.putExtra("key", "");
        goToCapture.putExtra("is_expired", "0");
        goToCapture.putExtra("document_belongs_to", doc_type);
        startActivity(goToCapture);
    }

    private Boolean initPermission(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return false;
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);

            return false;
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
            return false;
        }

        return true;
    }








    public void view_function() {
        if (file_url != null && file_url != "false") {
            String[] file_type = file_type_string.split("/");
            if (file_type[0] != "" && file_type[0].equals("image")) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DocumentDetail.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.image_doc_alert, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                    /*TextView title = (TextView) dialogView.findViewById(R.id.doc_title);
                    title.setText(model.getFile_name());*/
                final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
                String url = UrlController.HOST + file_url;

                Glide.with(DocumentDetail.this)
                        .load(url)
                        .error(R.drawable.no_image)
                        .into(doc_image);

                Button close_btn = dialogView.findViewById(R.id.close_btn);
                final AlertDialog b = dialogBuilder.create();
                b.show();
                close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b.dismiss();
                    }
                });
            } else if (file_type[1] != "" && file_type[1].equals("pdf")) {

                //callback.showProgressCallback();
                name = document_request_id + "-" + code + "-" + doc_name + ".pdf";
                document_name = doc_name;
                    /* ManagePdf managePdf = new ManagePdf();
                    managePdf.registerDownloadInterface(UploadedDocumentsAdapter.this);
                    managePdf.downloadPdf(activity,model.getFile_url(),name);*/


                int downloadId = PRDownloader.download(file_url, uploadedDirPath, name)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress_layout.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {

                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(final Progress progress) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (textProgressBar != null) {
                                            int perc = (int) (100 * progress.currentBytes / progress.totalBytes);
                                            textProgressBar.setProgress(perc);
                                            textProgressBar.setText("" + perc + " %");

                                        }

                                    }
                                });
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (textProgressBar != null) {
                                            textProgressBar.setProgress(0);
                                            textProgressBar.setText("" + 0 + " %");
                                            progress_layout.setVisibility(View.GONE);
                                        }

                                    }
                                });
                                Intent next = new Intent(DocumentDetail.this, ViewPdf.class);
                                next.putExtra("name", name);
                                next.putExtra("document_name", document_name);
                                startActivity(next);
                            }

                            @Override
                            public void onError(Error error) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (textProgressBar != null) {
                                            textProgressBar.setProgress(0);
                                            textProgressBar.setText("" + 0 + " %");
                                            progress_layout.setVisibility(View.GONE);
                                        }

                                    }
                                });
                                Toast.makeText(DocumentDetail.this, "Download was not successfull", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }


    }
}
