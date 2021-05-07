package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.tmsfalcon.device.tmsfalcon.R
import kotlinx.android.synthetic.main.activity_kot_test.*

class KotTest : AppCompatActivity() {

var counter:Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kot_test)

        web_settlement.settings.javaScriptEnabled = true

        web_settlement.settings.loadsImagesAutomatically = true
        web_settlement.settings.javaScriptEnabled = true
        web_settlement.settings.builtInZoomControls = true
        web_settlement.settings.displayZoomControls = false
        web_settlement.settings.setSupportZoom(true)
        web_settlement.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        web_settlement.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        web_settlement.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE)
        if (Build.VERSION.SDK_INT >= 19) {
            web_settlement.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            web_settlement.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }


        btn.setOnClickListener {

//  TODD jugaad          http://docs.google.com/gview?embedded=true&url=


//            https://tmsfalcon.com/Docs/preview?key=4e5751334d7a51344f44526a5a5442684f5467774e54566b4d6a45794e57566d597a426b4d444a6a5a4451344e54526b59544533593255354e6d4d355a4455785a6a4d794e4756684e7a4d345954646c4e475a6c5a446c6a4d57526c4d47513359574e6c4d54557a4e7a63334f545a6a597a4e6c597a5a695a444e6c4e5468685a475a6c4f5749775954646b4e7a56694e6d4d784d7a67334e6a426c4d6a6c6d4e7a497a4e57566a4d446333596d4e55626d4e474e453968596d6444566d4e6f56574e474e6b5257556e4268576c4578
//            web_settlement.loadUrl("https://tmsfalcon.com/Docs/preview?key=4e5751334d7a51344f44526a5a5442684f5467774e54566b4d6a45794e57566d597a426b4d444a6a5a4451344e54526b59544533593255354e6d4d355a4455785a6a4d794e4756684e7a4d345954646c4e475a6c5a446c6a4d57526c4d47513359574e6c4d54557a4e7a63334f545a6a597a4e6c597a5a695a444e6c4e5468685a475a6c4f5749775954646b4e7a56694e6d4d784d7a67334e6a426c4d6a6c6d4e7a497a4e57566a4d446333596d4e55626d4e474e453968596d6444566d4e6f56574e474e6b5257556e4268576c4578")
//                web_settlement.loadUrl("https://cloudmatrix.ca/")
//            web_settlement.loadUrl("https://drive.google.com/file/d/1jkd2dT9D6rk-FqsWyU396l0Ro_Ki569O/view?usp=sharing")


            web_settlement.loadUrl("https://docs.google.com/viewer?embedded=true&url=https://tmsfalcon.com/Docs/preview?key=4e5751334d7a51344f44526a5a5442684f5467774e54566b4d6a45794e57566d597a426b4d444a6a5a4451344e54526b59544533593255354e6d4d355a4455785a6a4d794e4756684e7a4d345954646c4e475a6c5a446c6a4d57526c4d47513359574e6c4d54557a4e7a63334f545a6a597a4e6c597a5a695a444e6c4e5468685a475a6c4f5749775954646b4e7a56694e6d4d784d7a67334e6a426c4d6a6c6d4e7a497a4e57566a4d446333596d4e55626d4e474e453968596d6444566d4e6f56574e474e6b5257556e4268576c4578")



//            https://cloudmatrix.ca/

        }


        web_settlement.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                Log.e("webview---","shouldOverrideUrlLoading")
                view.loadUrl(url)
                return true
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                // Check to see if there is a progress dialog
                    Log.e("webview---","loading")
                    // Hide the webview while loading
                    web_settlement.setEnabled(false)

                counter++

            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // Page is done loading;
                // hide the progress dialog and show the webview
                Log.e("webview---","loaded")
                if (counter>3){
                    Log.e("webview---","done")
                    web_settlement.setEnabled(true)

                }else {
                    Log.e("webview---","called")
                    counter = 0
                    web_settlement.loadUrl("https://docs.google.com/viewer?embedded=true&url=https://tmsfalcon.com/Docs/preview?key=4e5751334d7a51344f44526a5a5442684f5467774e54566b4d6a45794e57566d597a426b4d444a6a5a4451344e54526b59544533593255354e6d4d355a4455785a6a4d794e4756684e7a4d345954646c4e475a6c5a446c6a4d57526c4d47513359574e6c4d54557a4e7a63334f545a6a597a4e6c597a5a695a444e6c4e5468685a475a6c4f5749775954646b4e7a56694e6d4d784d7a67334e6a426c4d6a6c6d4e7a497a4e57566a4d446333596d4e55626d4e474e453968596d6444566d4e6f56574e474e6b5257556e4268576c4578")

                }
//                web_settlement.loadUrl("javascript:(function() { " +
//                        "document.querySelector('[role=\"toolbar\"]').remove();return false;})()")

            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                Log.e("webview---","error")
            }




        }



    }
}