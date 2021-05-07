package com.tmsfalcon.device.tmsfalcon.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mabbas007.tagsedittext.TagsEditText;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PdfWebViewActivity extends NavigationBaseActivity {

    boolean isFABOpen = false;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;
    Boolean isOpen = false;
    CustomValidator customValidator = new CustomValidator(PdfWebViewActivity.this);
    AlertDialog emailAlertDialog,faxAlertDialog;
    String pdf_url;
    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    String directory_name;
    NetworkValidator networkValidator;
    String file_name = "";
    Call<PostResponse> call;
    private int counter = 0;

    private String baseStrForPdf = "https://docs.google.com/viewer?embedded=true&url=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_pdf_web_view, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        networkValidator = new NetworkValidator(PdfWebViewActivity.this);

        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);
        int colorCodeDark = Color.parseColor("#4285f3");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));

        pdf_url = getIntent().getStringExtra("pdf_url");
        Log.e("pdf_url","is "+pdf_url);
        directory_name = root+"/"+"downloaded_docs";
        file_name = getIntent().getStringExtra("file_name");
        Log.e("file_name","is "+file_name);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        Log.e("testCheck",": "+pdf_url);
//    TODO to uncomment
//                webView.loadUrl("http://docs.google.com/gview?embedded=true&url="+pdf_url);

        webView.loadUrl(baseStrForPdf+pdf_url);

//        webView.loadUrl(pdf_url);
        webView.setWebViewClient(new MyBrowser());
    }

    public void showProgessBar() {
        progress_layout.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if(textProgressBar != null){
            textProgressBar.setProgress(0);
            textProgressBar.setText(""+0+" %");
            progress_layout.setVisibility(View.GONE);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.GONE);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("webview---","loaded");
//            webView.loadUrl("javascript:(function() { " +
//                    "document.querySelector('[role=\"toolbar\"]').remove();return false;})()");
//            fab.setVisibility(View.VISIBLE);


            if (counter>3){
                Log.e("webview---","done");
                webView.setEnabled(true);
                fab.setVisibility(View.VISIBLE);


            }else {
                Log.e("webview---","called");
                counter = 0;
                webView.loadUrl(baseStrForPdf+pdf_url);

            }



        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            Log.e("webview---","loading");
            // Hide the webview while loading
            webView.setEnabled(false);

            counter++;
        }



        /*@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }*/
    }



    @Bind(R.id.webview)
    WebView webView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.fab_fax)
    FloatingActionButton fab_fax;

    @Bind(R.id.fab_download)
    FloatingActionButton fab_download;

    @Bind(R.id.fab_email)
    FloatingActionButton fab_email;

    @Bind(R.id.progress_layout)
    public LinearLayout progress_layout;

    @OnClick(R.id.back_btn)
    void PreviousScreen(){
        super.onBackPressed();
    }

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.text_progress_bar)
    public TextProgressBar textProgressBar;

    @OnClick(R.id.fab)
    void onClickFab(){
        if (isOpen) {

            fab_download.startAnimation(fab_close);
            fab_email.startAnimation(fab_close);
            fab_fax.startAnimation(fab_close);
            fab.startAnimation(fab_anticlock);
            fab_download.setClickable(false);
            fab_email.setClickable(false);
            fab_fax.setClickable(false);
            isOpen = false;

        } else {

            fab_download.startAnimation(fab_open);
            fab_email.startAnimation(fab_open);
            fab_fax.startAnimation(fab_open);
            fab.startAnimation(fab_clock);
            fab_download.setClickable(true);
            fab_email.setClickable(true);
            fab_fax.setClickable(true);
            isOpen = true;
        }
    }




//      Dialog to send the mail to your self or other
    private void optionYourselfOrOther(final String emailId){
        AlertDialog.Builder builder = new AlertDialog.Builder(PdfWebViewActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
        builder.setTitle("Hey!!");
        char quotes = '"';
        builder.setMessage("Do you want us to send this attachment at \n"+quotes+emailId+quotes+"?");
        builder.setPositiveButton(R.string.yes_send_to_this_mail, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                sending to your self
                sendEmail(emailId);
            }
        });
        builder.setNegativeButton(R.string.no_send_to_other, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tonewOtherMailId("All Right!!");
            }
        });
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.show();


    }



//    Mail your self
    private void sendEmail(String emailId){
        send_email_contact(emailId,getResources().getString(R.string.attached_doc),getResources().getString(R.string.req_doc_message),"email",pdf_url);

    }

    private void tonewOtherMailId(String title){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Dialog_Alert);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(parms);
        layout.setPadding(2, 2, 2, 2);
        final EditText et = new EditText(this);
        et.setHint(getResources().getString(R.string.enter_email));
        et.setTextColor(getResources().getColor(R.color.white));
        et.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        et.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams parmOne = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parmOne.setMargins(0,5,0,0);
        et.setLayoutParams(parmOne);

        layout.addView(et);
        dialogBuilder.setView(layout);
        dialogBuilder.setTitle(title);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setMessage("Please enter your Email id below");

        dialogBuilder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!isValidEmail(et.getText().toString().trim())){
                    Toast.makeText(PdfWebViewActivity.this, "Please enter the correct Email id", Toast.LENGTH_SHORT).show();
                }else {
                    SessionManager.getInstance().storeEmailID(et.getText().toString());
                    sendEmail(et.getText().toString());
                    dialogInterface.dismiss();
                }


            }
        });

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        dialogBuilder.show();
    }

//    fun isValidEmail(target: CharSequence?): Boolean {
//        var result =  !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
//        return result
//    }

    private Boolean isValidEmail(String str){
        Boolean result = !TextUtils.isEmpty(str) && Patterns.EMAIL_ADDRESS.matcher(str).matches();
        return result;
    }

    private void toOtherMailId(){
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
        emailsTextView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);


        final EditText subject = dialogView.findViewById(R.id.subject);
        final EditText message = dialogView.findViewById(R.id.message);

        emailsTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    emailsTextView.setHint("");
                else
                    emailsTextView.setHint("Enter Email");
            }
        });

        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    subject.setHint("");
                else
                    subject.setHint("Enter Subject");
            }
        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    message.setHint("");
                else
                    message.setHint("Enter Message");
            }
        });
        Button send_btn = dialogView.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailsTextView.getText().toString().length() == 0){
                    emailsTextView.setError("Please Enter Email.");
                }
                else if (subject.getText().toString().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please enter Subject",
                            Toast.LENGTH_SHORT).show();
                }
           /* else if (message.getText().toString().length() == 0) {
                Toast.makeText(getApplicationContext(),
                        "Please enter Message",
                        Toast.LENGTH_SHORT).show();
            }*/
                else{
                    String[] email_array = emailsTextView.getText().toString().split(",");
                    for(int i = 0; i < email_array.length ; i++){
                        email_array[i] = email_array[i].trim();
                        if(customValidator.isValidEmail(email_array[i])){
                            if(email_array.length == i+1){
                                String message_string = message.getText().toString();
                                send_email_contact(emailsTextView.getText().toString(),subject.getText().toString(),message_string,"email",pdf_url);
                                emailAlertDialog.dismiss();
                            }
                        }
                        else{
                            emailsTextView.setError("Please Enter Valid Email.");
                        }
                    }
                }
            }
        });

        Button cancel_btn =  dialogView.findViewById(R.id.cancel_btn);
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

    @OnClick(R.id.fab_email)
    void showEmailPopUp(){
//        to send mail to yourself or somebody else
        if (SessionManager.getInstance().getEmailId().isEmpty()){
            tonewOtherMailId("Hey!!");
        }else {
            optionYourselfOrOther(SessionManager.getInstance().getEmailId());
        }
    }

    @OnClick(R.id.fab_fax)
    void showFaxPopUp(){
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
        mTagsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);


        Button send_btn = dialogView.findViewById(R.id.send_btn);

        mTagsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    mTagsEditText.setHint("");
                else
                    mTagsEditText.setHint("Enter Fax Number");
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mTagsEditText.getText().toString().length() == 0){
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
                else{
                    String fax_no = mTagsEditText.getText().toString();
                    if(customValidator.isValidFax(fax_no)){
                        send_email_contact(mTagsEditText.getText().toString(),"","","fax",pdf_url);
                        faxAlertDialog.dismiss();
                    }
                    else{
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(emailAlertDialog != null){
            emailAlertDialog.dismiss();
        }

    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    public void send_email_contact(String email_str,String subject,String message,String type,String url) {
        if(networkValidator.isNetworkConnected()) {
            if (email_str!=null||subject!=null||message!=null||type!=null||url!=null){
                progressBar.setVisibility(View.VISIBLE);
                Map<String, RequestBody> postFields = new HashMap<>();
                postFields.put("subject", requestBody(subject));
                postFields.put("message", requestBody(message));
                postFields.put("tags", requestBody(email_str));
                postFields.put("type", requestBody(type));
                postFields.put("url", requestBody(url));
                try {
                    if (file_name != null && file_name != "" && file_name.length() > 0) {
                        postFields.put("file_name", requestBody(file_name));
                    } else {
                        postFields.put("file_name", requestBody(""));
                    }
                } catch (Exception e) {

                }


                Log.e("type", "is " + type);
                Log.e("tags", "is " + email_str);

                //fragment.showProgessBar();
                call = RestClient.get().emaiUrl(SessionManager.getInstance().get_token(), postFields);
                call.enqueue(new Callback<PostResponse>() {
                    @Override
                    public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                        Log.e("documents response ", "is " + response.toString());
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null || response.isSuccessful()) {
                            List messagesList = response.body().getMessages();
                            String messages = "";
                            for (int i = 0; i < messagesList.size(); i++) {
                                messages += messagesList.get(i);
                            }
                            if (response.body().getStatus()) {


                            }
                            if (emailAlertDialog != null) {
                                emailAlertDialog.dismiss();
                            }
                            if (faxAlertDialog != null) {
                                faxAlertDialog.dismiss();
                            }

                            Toast.makeText(PdfWebViewActivity.this, "" + messages, Toast.LENGTH_SHORT).show();

                        } else {

                            try {
                                String error_string = response.errorBody().string();
                                ErrorHandler.setRestClientMessage(PdfWebViewActivity.this, error_string);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // fragment.hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<PostResponse> call, Throwable t) {
                        //fragment.hideProgressBar();
                        progressBar.setVisibility(View.GONE);
                        if (emailAlertDialog != null) {
                            emailAlertDialog.dismiss();
                        }

                        Log.e("server call error", t.getMessage());
                        Toast.makeText(PdfWebViewActivity.this, "Response Call Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else {
                Toast.makeText(PdfWebViewActivity.this, "Please enter all the details", Toast.LENGTH_SHORT).show();

            }
        }
        else{
            Toast.makeText(PdfWebViewActivity.this, ""+getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    @Bind(R.id.parent_layout)
    RelativeLayout parent_layout;

    @OnClick(R.id.fab_download)
    void downloadDoc(){
        int downloadId = PRDownloader.download(pdf_url, directory_name, file_name+".pdf")
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        showProgessBar();
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
                    public void onProgress(Progress progress) {
                        int percentage = (int)(100 * progress.currentBytes / progress.totalBytes);
                        if(textProgressBar != null){
                            textProgressBar.setProgress(percentage);
                            textProgressBar.setText(""+percentage+" %");
                        }
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        hideProgressBar();
                        Snackbar snackbar = Snackbar
                                .make(parent_layout, "Document "+file_name+" is downloaded at location "+directory_name, Snackbar.LENGTH_LONG);

                        snackbar.show();

                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("isConnectionError",""+error.isConnectionError());
                        Log.e("isServerError",""+error.isServerError());
                        hideProgressBar();
                        Toast.makeText(PdfWebViewActivity.this,"Download was not successfull",Toast.LENGTH_LONG).show();
                    }

                });
    }

}
