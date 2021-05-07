package com.tmsfalcon.device.tmsfalcon.activities.test

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.webkit.WebView
import android.webkit.WebViewClient
import com.tmsfalcon.device.tmsfalcon.R

class TestActivity : AppCompatActivity() {

    lateinit var webView: WebView


    var activity: Activity? = null
    lateinit var progDailog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

//        btn.setOnClickListener {
//            callRemote()
//        }


        activity = this

        progDailog = ProgressDialog.show(activity, "Loading", "Please wait...", true)
        progDailog.setCancelable(false)



        webView = findViewById(R.id.webview_compontent) as WebView



        webView.getSettings().javaScriptEnabled = true
        webView.getSettings().loadWithOverviewMode = true
        webView.getSettings().useWideViewPort = true

        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                progDailog.show()
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                progDailog.dismiss()
            }
        })

        webView.loadUrl("https://rapp.tmsfalcon.com/application")


    }




//    fun callRemote(){
//
//
//
//
//      var a = TestApiClient.client?.create(TestDataInterface::class.java)
//
//     var b=  a?.uploadFile("v3")
//
//        b?.enqueue(object :Callback<JsonElement>{
//            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
//
//            }
//
//            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
//                Log.e("response","response : "+response.errorBody()?.source())
//
//
//            }
//
//        })
//
//
////    a.baseUrl()
//    }

}