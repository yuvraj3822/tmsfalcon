package com.tmsfalcon.device.tmsfalcon.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AccidentDetailWebView extends NavigationBaseActivity {

    NetworkValidator networkValidator;
    String target_url = "";
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
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_accident_detail_web_view, null, false);
        drawer.addView(contentView, 0);
       // setContentView(R.layout.activity_accident_detail_web_view);

        ButterKnife.bind(this);

        mContext = this.getApplicationContext();
        target_url = getIntent().getStringExtra("url");
        Log.e("target_url",target_url);
        int colorCodeDark = Color.parseColor("#4285f3");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        progressBar.setVisibility(View.GONE);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        initWebView();
        loadURL();

    }

    private void loadURL() {
        webView.loadUrl(target_url);
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(AccidentDetailWebView.this));
        webView.addJavascriptInterface(new AjaxHandler(AccidentDetailWebView.this), "ajaxHandler");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
               // view.loadUrl("javascript:(function() { document.getElementsByTagName('video')[0].play(); })()");
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
