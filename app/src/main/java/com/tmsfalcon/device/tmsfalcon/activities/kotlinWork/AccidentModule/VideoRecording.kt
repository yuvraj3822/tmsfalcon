package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper
import kotlinx.android.synthetic.main.activity_video_recording.*
import java.io.File


class VideoRecording : AppCompatActivity() {

     var sampleGLView:GLSurfaceView? = null
    lateinit var frameLayout:FrameLayout
    lateinit var outPath:String

    var REQUEST_VIDEO_CAPTURE = 420
    lateinit var outuri:Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_recording)
        val newPicFile = ImageHelper()!!.uniqueFileName() + ".mp4"
        val root_directory_name = "tmsfalcon"
        val root = Environment.getExternalStorageDirectory().toString() + "/" + root_directory_name

         outPath = "$root/$root_directory_name/$newPicFile"

        Log.e("path","$outPath")

        start.setOnClickListener {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            val newPicFile = ImageHelper()!!.uniqueFileName() + ".mp4"
            val root_directory_name = "tmsfalcon"
            val root = Environment.getExternalStorageDirectory().toString() + "/" + root_directory_name
            val outPath = "$root/$newPicFile"
            val outFile = File(outPath)
             outuri = Uri.fromFile(outFile)
            Log.e("outuri", " is: $outuri")
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outuri)
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 90)

            if (takeVideoIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_VIDEO_CAPTURE && resultCode === Activity.RESULT_OK) {
//            val videoUri = intent.data
            wrap_view.setVideoURI(outuri)
        }
    }
}