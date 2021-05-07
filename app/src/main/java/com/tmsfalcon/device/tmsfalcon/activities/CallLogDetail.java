package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CallLogDetail extends NavigationBaseActivity {

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_call_log_detail, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        int colorCodeDark = Color.parseColor("#071528");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));

        url = getIntent().getStringExtra("public_link");

        Log.e("public_link"," is "+url);
        if(url != null){
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(url);
            webView.setWebViewClient(new MyBrowser());

        }
        else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(CallLogDetail.this,"Url not available.",Toast.LENGTH_LONG).show();
        }

        //webView.loadUrl("https://hello.tmsfalcon.com/calllog/view/23?view=embed");
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;
        }
    }


    @Bind(R.id.webview)
    WebView webView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
}
