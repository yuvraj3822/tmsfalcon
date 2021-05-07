package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.scanlibrary.ScansActivity
import com.scanlibrary.ScanConstants
import com.tmsfalcon.device.tmsfalcon.R
import kotlinx.android.synthetic.main.activity_test_cam.*
import java.io.IOException

class TestCam : AppCompatActivity() {

    private val REQUEST_CODE = 99
    private val REQUEST_CAMERA_PERMISSION = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_cam)
        initOnPermissionsss()
        init()
    }


    private fun initOnPermissionsss() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun init() {
//        scanButton = findViewById<View>(R.id.scanButton) as Button

        scanButton.setOnClickListener(ScanButtonClickListener())
        cameraButton.setOnClickListener {
            Log.e("tag","well that it can be doen")
                val intent = Intent(this, ScansActivity::class.java)
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)
                startActivityForResult(intent, REQUEST_CODE)


        }
//        cameraButton.setOnClickListener(ScanButtonClickListener(ScanConstants.OPEN_CAMERA))
        mediaButton.setOnClickListener {
                val intent = Intent(this, ScansActivity::class.java)
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA)
                startActivityForResult(intent, REQUEST_CODE)

        }
    }

    private class ScanButtonClickListener : View.OnClickListener {
        private var preference = 0

        constructor(preference: Int) {
            this.preference = preference
        }

        constructor() {}

        override fun onClick(v: View) {
//            startScan(preference)
        }
    }

     fun startScan(preference: Int) {
        val intent = Intent(this, ScansActivity::class.java)
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference)
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.extras?.getParcelable<Uri>(ScanConstants.SCANNED_RESULT)
            var bitmap: Bitmap? = null
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                uri?.let { contentResolver.delete(it, null, null) }
                scannedImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun convertByteArrayToBitmap(data: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(data, 0, data.size)
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
//    }

}