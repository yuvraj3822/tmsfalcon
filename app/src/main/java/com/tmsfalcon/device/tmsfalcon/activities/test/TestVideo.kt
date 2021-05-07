package com.tmsfalcon.device.tmsfalcon.activities.test

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.potyvideo.library.utils.PathUtil
import com.potyvideo.library.utils.PublicFunctions
import com.tmsfalcon.device.tmsfalcon.R
import kotlinx.android.synthetic.main.activity_test_video.*
import java.net.URISyntaxException


class TestVideo : AppCompatActivity(){

    val req_code = 121

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_video)

        var path = intent.getStringExtra("path")
        Log.e("path",": $path")

        vid.setVideoPath(path)
        vid.start()
//        var filePath = PathUtil.getPath(this, Uri.parse(path))
//        Log.e("result : ","$filePath")

//        vid.setVideoPath("/storage/emulated/0/tmsfalcon/TMSFALCON-20210105112201.mp4")
//        vid.start()

//        clickme.setOnClickListener {
//            selectLocaleVideo()
//        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === req_code && resultCode === Activity.RESULT_OK) {
            val finalVideoUri: Uri = data?.data ?: return

            Log.e("fromOnActivity ",": ${finalVideoUri.toString()}")

            var filePath: String? = null
            try {
                filePath = PathUtil.getPath(this, finalVideoUri)

                Log.e("final: ","$filePath")
//                vid.setSource(filePath)
            } catch (e: URISyntaxException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed: " + e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectLocaleVideo() {
        if (PublicFunctions.checkAccessStoragePermission(this)) {
            val intent = Intent()
            intent.setType("video/*")
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(intent, "Select Video"), req_code)
        }
    }
    private fun getMedia(mediaName: String): Uri? {
        return Uri.parse("android.resource://" + packageName +
                "/raw/" + mediaName)
    }


}