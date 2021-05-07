package com.tmsfalcon.device.tmsfalcon;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Message;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RcEmbed extends NavigationBaseActivity {

    NetworkValidator networkValidator;
    private long mLastBackPressTime = 0;
    public WebView mWebviewPop;
    String target_url = "https://ringcentral.github.io/ringcentral-embeddable/app.html";
    String target_url_prefix = "https://ringcentral.github.io/ringcentral-embeddable/proxy.html?hash=ODM2YmNmZjAtODVlNy00Nzg2LWEwYTAtM2YxNzZlOTI1MDY5&prefix=rc-widget";
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_rc_embed, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        mContext = this.getApplicationContext();

        int colorCodeDark = Color.parseColor("#4285f3");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));

        /*Uri uri = Uri.parse(target_url);

        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        intentBuilder.setStartAnimations(this, android.R.anim.slide_out_right, android.R.anim.slide_in_left);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = intentBuilder.build();

        customTabsIntent.launchUrl(RcEmbed.this, uri);*/

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.loadUrl(target_url);
        webView.setWebViewClient(new MyBrowser());

        webView.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(RcEmbed.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportZoom(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                newWebView.getSettings().setSupportMultipleWindows(true);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                });

                return true;
            }
        });

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            view.loadUrl(url);
            return true;
        }
    }

    @Bind(R.id.webview)
    WebView webView;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.webview_frame)
    FrameLayout mContainer;
}
