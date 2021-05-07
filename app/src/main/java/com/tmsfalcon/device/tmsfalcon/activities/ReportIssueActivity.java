package com.tmsfalcon.device.tmsfalcon.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReportIssueActivity extends AppCompatActivity {

    NetworkValidator networkValidator;
    String target_url = "https://tmsfalcon.atlassian.net/servicedesk/customer/portals";
    public Context mContext;
    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String TAG = ReportIssueActivity.class.getSimpleName();
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        ButterKnife.bind(this);

        mContext = this.getApplicationContext();

        int colorCodeDark = Color.parseColor("#4285f3");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        progressBar.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        initWebView();
        loadURL();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, intent);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                String dataString = intent.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
        return;
    }

    private void loadURL() {
        webView.loadUrl(target_url);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(ReportIssueActivity.this));
        webView.addJavascriptInterface(new AjaxHandler(ReportIssueActivity.this), "ajaxHandler");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public class AjaxHandler {

        private static final String TAG = "AjaxHandler";
        private final Context context;

        public AjaxHandler(Context context) {
            this.context = context;
        }

        public void ajaxBegin() {
            Log.e(TAG, "AJAX Begin");
            Toast.makeText(context, "AJAX Begin", Toast.LENGTH_SHORT).show();
        }

        public void ajaxDone() {
            Log.e(TAG, "AJAX Done");
            Toast.makeText(context, "AJAX Done", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
            mFilePathCallback = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            return true;
        }


    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Bind(R.id.webview)
    WebView webView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.webview_frame)
    FrameLayout mContainer;
}
