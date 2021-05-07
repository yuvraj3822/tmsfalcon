package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import com.tmsfalcon.device.tmsfalcon.R
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.surfacelayout_camera.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() , SurfaceHolder.Callback, Camera.PictureCallback, Camera.PreviewCallback {

    private val REQUEST_CAMERA_PERMISSION = 0
    private var mCamera: Camera? = null

    lateinit var mSurfaceHolder: SurfaceHolder
    private lateinit var executor: ExecutorService
    private lateinit var proxySchedule: Scheduler
    private var busy: Boolean = false

    private var REQUEST_CODE = 1

    var TAG: String = MainActivity::class.java.simpleName




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.surfacelayout_camera)

        initOnCreate()

        Log.e(TAG,md5("2B:9A:55:21:C6:AF:68:D8:61:0E:D7:3E:10:06:94:3D"))
    }


    fun md5(s: String): String? {
        val MD5 = "MD5"
        try {
            // Create MD5 Hash
            val digest = MessageDigest
                    .getInstance(MD5)
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuilder()
            for (aMessageDigest in messageDigest) {
                var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
                while (h.length < 2) h = "0$h"
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    fun  initOnCreate(){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
        }



        OpenCVLoader.initDebug();

        mSurfaceHolder = surface.holder

        initBlock()
        startCamera()
//        initCamera()


        shut.setOnClickListener {
//            clickPicture()
//          var param =  mCamera!!.parameters
//            param.flashMode = Camera.Parameters.FLASH_MODE_TORCH
//        mCamera!!.parameters = param
//            mCamera!!.startPreview()

//            var intent = Intent(this@MainActivity, Test::class.java)
//
//            startActivityForResult(intent,11)


        }

        Log.e("MainActivity","oncreate")
    }





    fun clickPicture(){
        Log.e("called","shut")

        busy = true
        Log.i(TAG, "try to focus")
        mCamera?.autoFocus { b, _ ->
            Log.i(TAG, "focus result: " + b)
            mCamera?.takePicture(null, null, this)
//            MediaActionSound().play(MediaActionSound.SHUTTER_CLICK)
        }
    }

    fun initBlock(){
        Log.e("called","init")
        mSurfaceHolder.addCallback(this)
        executor = Executors.newSingleThreadExecutor()
        proxySchedule = Schedulers.from(executor)
    }

    fun startCamera(){
        mCamera?.startPreview() ?: Log.i(TAG, "camera null")

    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION
                && (grantResults[permissions.indexOf(android.Manifest.permission.CAMERA)] == PackageManager.PERMISSION_GRANTED)) {

            Log.e("permission granted","rsult")

//            showMessage(R.string.camera_grant)
//            mPresenter.initCamera()
//            mPresenter.updateCamera()


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    fun initCamera(){

        Log.e("called","initCamera")

        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        } catch (e: RuntimeException) {
            e.stackTrace
            Toast.makeText(this@MainActivity, "cannot open camera, please grant camera", Toast.LENGTH_SHORT).show()
            return
        }

        val param = mCamera?.parameters
        val size = getMaxResolution()
        param?.setPreviewSize(size?.width ?: 1920, size?.height ?: 1080)

        val display = windowManager.defaultDisplay
        val point = Point()
        display.getRealSize(point)
        val displayWidth = minOf(point.x, point.y)
        val displayHeight = maxOf(point.x, point.y)
        val displayRatio = displayWidth.div(displayHeight.toFloat())
        val previewRatio = size?.height?.toFloat()?.div(size.width.toFloat()) ?: displayRatio
        if (displayRatio > previewRatio) {
            val surfaceParams = surface.layoutParams
            surfaceParams.height = (displayHeight / displayRatio * previewRatio).toInt()
            surface.layoutParams = surfaceParams
        }

        val supportPicSize = mCamera?.parameters?.supportedPictureSizes
        supportPicSize?.sortByDescending { it.width.times(it.height) }
        var pictureSize = supportPicSize?.find { it.height.toFloat().div(it.width.toFloat()) - previewRatio < 0.01 }

        if (null == pictureSize) {
            pictureSize = supportPicSize?.get(0)
        }

        if (null == pictureSize) {
            Log.e(TAG, "can not get picture size")
        } else {
            param?.setPictureSize(pictureSize.width, pictureSize.height)
        }
        val pm = this@MainActivity.packageManager
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS)) {
            param?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            Log.d(TAG, "enabling autofocus")
        } else {
            Log.d(TAG, "autofocus not available")
        }
        param?.flashMode = Camera.Parameters.FLASH_MODE_AUTO

        mCamera?.parameters = param
        mCamera?.setDisplayOrientation(90)
    }


    private fun getMaxResolution(): Camera.Size? = mCamera?.parameters?.supportedPreviewSizes?.maxBy {
        Log.e("created","getMaxResolution")
        it.width
    }


    fun updateCamera() {
        Log.e("called","updatecamera")

        if (null == mCamera) {
            return
        }
        mCamera?.stopPreview()
        try {
            mCamera?.setPreviewDisplay(mSurfaceHolder)
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        mCamera?.setPreviewCallback(this)
        mCamera?.startPreview()
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        Log.e("called","surfaceChanged")

        updateCamera()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {

        synchronized(this) {
            mCamera?.stopPreview()
            mCamera?.setPreviewCallback(null)
            mCamera?.release()
            mCamera = null
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        Log.e("created","surfaceCreated")
        initCamera()
    }

    override fun onPictureTaken(p0: ByteArray?, p1: Camera?) {
        Log.e("called","onPictureTaken")

        Observable.just(p0)
                .subscribeOn(proxySchedule)
                .subscribe {


                    busy = false
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
//            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode === REQUEST_CODE && resultCode === Activity.RESULT_OK) {
                Log.e(TAG,"called onactivityresult")
                initOnCreate()
            }
        } catch (ex: Exception) {

        }

        if (requestCode == 11 && resultCode == Activity.RESULT_OK){
            Log.e(TAG, "test completed: " )
        }

    }

    override fun onPreviewFrame(p0: ByteArray?, p1: Camera?) {


        if (busy) {
            return
        }
        busy = true
        Observable.just(p0)
                .observeOn(proxySchedule)
                .subscribe {
                    Log.e(TAG, "start prepare paper")
                    val parameters = p1?.parameters
                    val width = parameters?.previewSize?.width
                    val height = parameters?.previewSize?.height
                    val yuv = YuvImage(p0, parameters?.previewFormat ?: 0, width ?: 320, height
                            ?: 480, null)
                    val out = ByteArrayOutputStream()
                    yuv.compressToJpeg(Rect(0, 0, width ?: 320, height ?: 480), 100, out)
                    val bytes = out.toByteArray()
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                    val img = Mat()
                    Utils.bitmapToMat(bitmap, img)
                    bitmap.recycle()
//                    Core.rotate(img, img, Core.ROTATE_90_CLOCKWISE)
                    try {
                        out.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }


                }

    }


    var isTimerRunning:Boolean = false

    var timer  = object : CountDownTimer(3000,1000){
        override fun onFinish() {
            Log.e("MainActivity","auto picture taken")
            isItEnoughForThePictureClick()
            isTimerRunning = false

        }

        override fun onTick(p0: Long) {
//            clickPicture()
            isTimerRunning  =  true
        }
    }


    var autoCrop:Boolean = false

    var goodToGoForCapturingPicture:Boolean = false

    var boolList:ArrayList<Boolean> = ArrayList()

    fun resetAllAutocropFeatures(){
        isTimerRunning = false
        boolList.clear()
        autoCrop = false
        goodToGoForCapturingPicture = false
        timer.cancel()

    }



    fun isItEnoughForThePictureClick(){
        var i:Long = 0
        var j:Long = 0
        for (bool in boolList){
            if (bool){
                i++
            }else {
                j++
            }
        }
        if (i>j){
            goodToGoForCapturingPicture = true
            Log.e("check","Good for click")
        } else {
            Log.e("check","Not good to go")

        }
        boolList.clear()

    }



}