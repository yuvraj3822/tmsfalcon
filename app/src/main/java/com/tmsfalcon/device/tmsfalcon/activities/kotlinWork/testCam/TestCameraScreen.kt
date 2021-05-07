package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Point
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import android.util.Log
import android.util.SparseIntArray
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import butterknife.Bind
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.dhaval2404.imagepicker.ImagePicker
//import com.github.dhaval2404.imagepicker.ImagePicker
import com.scanlibrary.ScanActivity
import com.scanlibrary.ScanAppController
import com.scanlibrary.Utils
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager
import com.tmsfalcon.device.tmsfalcon.customtools.ScanHint
import com.tmsfalcon.device.tmsfalcon.customtools.ScanUtils
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable
import com.tmsfalcon.device.tmsfalcon.database.RequestedDocumentsTable
import com.tmsfalcon.device.tmsfalcon.widgets.ProgressDialogFragment
import com.tmsfalcon.device.tmsfalcon.widgets.ScanSurfaceView
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_direct_upload_camera_screen.*
import kotlinx.android.synthetic.main.capture_btn_custom_layout.*
import kotlinx.android.synthetic.main.surfacelayout_camera.surface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TestCameraScreen : NavigationBaseActivity(), SurfaceHolder.Callback, Camera.PictureCallback, Camera.PreviewCallback {
    var permissionsManager = PermissionManager()
    var manager: CameraManager? = null
    var is_flash_enabled = false
    var characteristics: CameraCharacteristics? = null
    private var hasFlashCameraOne = false
    var params: Camera.Parameters? = null

    var canfocus = false
    var has_camera_permission = 0
    var sessionManager: SessionManager? = null
    var db: DirectUploadTable? = null
    var db1: RequestedDocumentsTable? = null

    //Camera one varibles
    lateinit var camera: Camera
    private var containerScan: ViewGroup? = null
    private var cameraPreviewLayout: FrameLayout? = null
    private val mImageSurfaceView: ScanSurfaceView? = null
    private val isPermissionNotGranted = false
    private var captureHintText: TextView? = null
    private var captureHintLayout: LinearLayout? = null
    private var cameraId: String? = null
    private var copyBitmap: Bitmap? = null
    private var first_flash_call = true

    private val REQUEST_CODE = 99


    companion object {
        const val CAMERA = 0x5
        private val TAG = TestCameraScreen::class.java.simpleName
        private const val GALLERY = 1
        private const val mOpenCvLibrary = "opencv_java3"
        private var progressDialogFragment: ProgressDialogFragment? = null
        private val ORIENTATIONS = SparseIntArray()
        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                    matrix, true)
        }

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }

        init {
            System.loadLibrary(mOpenCvLibrary)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("test", "test")
        //closeCamera();
    }

    override fun onDestroy() {
        super.onDestroy()
        //closeCamera();
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_direct_upload_camera_screen, null, false)
        drawer.addView(contentView, 0)
        Log.e("checkscreens","TestCameraLayout")

        //setContentView(R.layout.activity_direct_upload_camera_screen);
        ButterKnife.bind(this)
        if (savedInstanceState != null) {
            Log.e("in bundle not null", "yes")
            Log.e("test bundle", savedInstanceState.getString("test"))
        }
        sessionManager = SessionManager(this@TestCameraScreen)
        sessionManager!!.checkLogin()

        fromNotification = fetchIntent()
        if (fromNotification){
            db1 = RequestedDocumentsTable(this@TestCameraScreen)
        }else {
            db = DirectUploadTable(this@TestCameraScreen)
        }
        init()
        clickListeners()
        if (!permissionsManager.checkPermission(this@TestCameraScreen, this@TestCameraScreen, Manifest.permission.CAMERA)) {
            permissionsManager.askForPermission(this@TestCameraScreen, this@TestCameraScreen, Manifest.permission.CAMERA, CAMERA)
        } else {
            has_camera_permission = 1
//            cameraSettings()
            startPreviewCamera()
        }

        Log.e("yuvraj","oncreate")

    }

    private fun init() {
        containerScan = findViewById(R.id.container_scan)
        cameraPreviewLayout = findViewById(R.id.camera_one_preview)
        captureHintLayout = findViewById(R.id.capture_hint_layout)
        captureHintText = findViewById(R.id.capture_hint_text)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    //    override fun onBackPressed() {
//
////        val i = Intent(this@TestCameraScreen, DashboardActivity::class.java)
////        startActivity(i)
//
//    }

    fun cameraSettings() {
        /**
         * initialising camera as per mobile config
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                Log.e("herecalled", "herecalled")
                cameraId = manager!!.cameraIdList[0]
                characteristics = manager!!.getCameraCharacteristics(cameraId!!)
            } catch (e: CameraAccessException) {
                Log.e("exception ", e.toString())
            }
        } else {
            hasFlashCameraOne = packageManager
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
            if (!hasFlashCameraOne) {
                if (camera == null) {
                    try {
                        Log.e("isitcalled", "isitcalled")
                        camera = Camera.open()
                        params = camera.getParameters()
                    } catch (e: RuntimeException) {
                        Log.e("Failed to Open. Error: ", e.message)
                    }
                }
            }
        }
        /**
         * database work
         */

         var docs_count :Int

        if (fromNotification){
            docs_count = db1!!.count
        }else {
            docs_count =   db!!.count
        }
        if (docs_count > 0) {
            doc_count_badge!!.text = "" + docs_count
        } else {
            doc_count_badge!!.visibility = View.GONE
        }
        /**
         * start camera preview
         */
        Handler().postDelayed({
            runOnUiThread {
                //                        mImageSurfaceView = new ScanSurfaceView(TestCameraScreen.this, TestCameraScreen.this);
//                        cameraPreviewLayout.addView(mImageSurfaceView);
            }
        }, 500)
    }

    lateinit var mSurfaceHolder: SurfaceHolder
    private lateinit var executor: ExecutorService
    private lateinit var proxySchedule: Scheduler
    private var mCamera: Camera? = null


    fun startCamera(){
        mCamera?.startPreview() ?: Log.i(TAG, "camera null")

    }

    fun cameraSettingNew() {
        mSurfaceHolder = surface.holder
        Log.e("called","init")
        mSurfaceHolder.addCallback(this)
        executor = Executors.newSingleThreadExecutor()
        proxySchedule = Schedulers.from(executor)


    }


    fun startPreviewCamera(){
        setCountFromDb()
        cameraSettingNew()
        startCamera()
    }


    fun setCountFromDb(){
//        val docs_count = db!!.count
//        if (docs_count > 0) {
//            doc_count_badge!!.text = "" + docs_count
//        } else {
//            doc_count_badge!!.visibility = View.GONE
//        }



        var docs_count :Int

        if (fromNotification){
            docs_count = db1!!.count
        }else {
            docs_count =   db!!.count
        }
        if (docs_count > 0) {
            doc_count_badge!!.text = "" + docs_count
        } else {
            doc_count_badge!!.visibility = View.GONE
        }

    }

    fun clickListeners(){

        flash_layout.setOnClickListener {
            val hasFlash = this.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

            if(hasFlash) {

                var param = mCamera!!.parameters
                Log.e("torch available: ", "${param.flashMode}" )
//                if (param.flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
//                if (param.flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH) {

                if (param.flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
                    param.flashMode = Camera.Parameters.FLASH_MODE_OFF
                    mCamera!!.parameters = param
                    mCamera!!.startPreview()
                    bottom_lights!!.setImageResource(R.drawable.headlight_off)
                    flash_text!!.text = resources.getString(R.string.lights_off)


                }else {
                    param.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                    mCamera!!.parameters = param
                    mCamera!!.startPreview()

                    bottom_lights!!.setImageResource(R.drawable.headlight)
                    flash_text!!.text = resources.getString(R.string.lights_on)

                }
//                }


            } else {
                Toast.makeText(this,"Your device does not support flash functionality",Toast.LENGTH_SHORT).show()
            }
        }

        import_layout.setOnClickListener {
//
//            val galleryIntent = Intent(Intent.ACTION_PICK,
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startActivityForResult(galleryIntent, GALLERY)

            ScanAppController.isCameraOpen = false
            ImagePicker.with(this)
                    .galleryOnly()	//User can only select image from Gallery
                    .start()


        }
        capture_layout.setOnClickListener {
//            val progressdialog = ProgressDialog(applicationContext)
//            progressdialog.setMessage("Loading....")
//            progressdialog.show()

//            progress_bar.visibility = View.VISIBLE
            ScanAppController.isCameraOpen = true
            busy = true
            Log.e(TAG, "try to focus")
//                mCamera?.autoFocus { b, _ ->
//                    Log.e(TAG, "focus result: " + b)
//                    mCamera?.takePicture(null, null, this)
//            }

            mCamera?.takePicture(null,null,this)
            mCamera?.setPreviewCallback(null)


        }






    }

    //    @Override
    fun displayHint(scanHint: ScanHint?) {
        captureHintLayout!!.visibility = View.VISIBLE
        when (scanHint) {
            ScanHint.MOVE_CLOSER -> {
                captureHintText!!.text = resources.getString(R.string.move_closer)
                captureHintLayout!!.background = resources.getDrawable(R.drawable.hint_red)
            }
            ScanHint.MOVE_AWAY -> {
                captureHintText!!.text = resources.getString(R.string.move_away)
                captureHintLayout!!.background = resources.getDrawable(R.drawable.hint_red)
            }
            ScanHint.ADJUST_ANGLE -> {
                captureHintText!!.text = resources.getString(R.string.adjust_angle)
                captureHintLayout!!.background = resources.getDrawable(R.drawable.hint_red)
            }
            ScanHint.FIND_RECT -> {
                captureHintText!!.text = resources.getString(R.string.finding_rect)
                captureHintLayout!!.background = resources.getDrawable(R.drawable.hint_white)
            }
            ScanHint.CAPTURING_IMAGE -> {
                captureHintText!!.text = resources.getString(R.string.hold_still)
                captureHintLayout!!.background = resources.getDrawable(R.drawable.hint_green)
            }
            ScanHint.NO_MESSAGE -> captureHintLayout!!.visibility = View.GONE
            else -> {
            }
        }
    }


    /**
     * Notification data
     */

    var intent_doc_belongs_to: String? = null
    var intent_is_expired:kotlin.String? = null
    var intent_key:kotlin.String? = null
    var intent_document_request_id:kotlin.String? = null
    var intent_document_type:kotlin.String? = null
    var intent_document_name:kotlin.String? = null
    var intent_load_number:kotlin.String? = null
    var intent_due_date:kotlin.String? = null
    var intent_comment:kotlin.String? = null
    var intent_status:kotlin.String? = null

    var fromNotification = false

    /**
     * initiailistng notification data
     */
    private fun fetchIntent() :Boolean{
        var dataAvailable = false
        if (intent.extras != null) {
            intent_document_request_id = intent.extras!!.getString("document_request_id", "")
            intent_document_type = intent.extras!!.getString("document_type", "")
            intent_document_name = intent.extras!!.getString("document_name", "")
            intent_load_number = intent.extras!!.getString("load_number", "")
            intent_due_date = intent.extras!!.getString("due_date", "")
            intent_comment = intent.extras!!.getString("comment", "")
            intent_status = intent.extras!!.getString("status", "")
            intent_key = intent.extras!!.getString("key", "")
            intent_is_expired = intent.extras!!.getString("is_expired", "")
            intent_doc_belongs_to = intent.extras!!.getString("document_belongs_to", "")
            dataAvailable = true
        } else {
            intent_document_request_id = ""
            intent_document_type = ""
            intent_document_name = ""
            intent_load_number = ""
            intent_due_date = ""
            intent_comment = ""
            intent_status = ""
            intent_key = ""
            intent_is_expired = ""
            intent_doc_belongs_to = ""
            dataAvailable = false
        }
    return dataAvailable
    }


    protected fun postImagePick(bitmap: Bitmap?, context: Context?) {
        val uri = Utils.getUri(context, bitmap)
        ScanAppController.nowUri = uri
        bitmap!!.recycle()
        //        scanner.onBitmapSelect(uri);
    }

    fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    //    @Override
    fun onPictureClicked(bitmap: Bitmap, bytes: ByteArray?) {
        try {
            copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            val height = window.findViewById<View>(Window.ID_ANDROID_CONTENT).height
            val width = window.findViewById<View>(Window.ID_ANDROID_CONTENT).width
            copyBitmap = ScanUtils.resizeToScreenContentSize(copyBitmap, width, height)

            postImagePick(copyBitmap, this)


            val goToCropScreen = Intent(this@TestCameraScreen, ScanActivity::class.java)
            goToCropScreen.putExtra("imported_from_gallery", false)
            startActivityForResult(goToCropScreen,REQUEST_CODE)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }

    @Synchronized
    private fun showProgressDialog(message: String) {
        if (progressDialogFragment != null && progressDialogFragment!!.isVisible) {
            // Before creating another loading dialog, close all opened loading dialogs (if any)
            progressDialogFragment!!.dismissAllowingStateLoss()
        }
        progressDialogFragment = null
        progressDialogFragment = ProgressDialogFragment(message)
        val fm = fragmentManager
        progressDialogFragment!!.show(fm, ProgressDialogFragment::class.java.toString())
    }

    @Synchronized
    private fun dismissDialog() {
        progressDialogFragment!!.dismissAllowingStateLoss()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                5 -> {
                    has_camera_permission = 1
                    startPreviewCamera()

                }
            }
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    @JvmField
    @Bind(R.id.camera_one_preview)
    var cameraOnePreview: FrameLayout? = null

    @JvmField
    @Bind(R.id.flash_text)
    var bottom_flash_text: TextView? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("onactivityresult","onactivityresult")
        captureHintLayout!!.visibility = View.GONE

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val fileUri = data?.data
            ScanAppController.nowUri = fileUri

//            val file: File = ImagePicker.getFile(data)!!
            if (fromNotification){
                Log.e("checkNot","withdata")
                val goToCropScreen = Intent(this@TestCameraScreen, ScanActivity::class.java)
                goToCropScreen.putExtra("document_request_id", intent_document_request_id)
                goToCropScreen.putExtra("document_type", intent_document_type)
                goToCropScreen.putExtra("document_name", intent_document_name)
                goToCropScreen.putExtra("load_number", intent_load_number)
                goToCropScreen.putExtra("due_date", intent_due_date)
                goToCropScreen.putExtra("comment", intent_comment)
                goToCropScreen.putExtra("status", intent_status)
                goToCropScreen.putExtra("key", intent_key)
                goToCropScreen.putExtra("is_expired", intent_is_expired)
                goToCropScreen.putExtra("document_belongs_to", intent_doc_belongs_to)
                startActivityForResult(goToCropScreen, REQUEST_CODE)
            } else {
                Log.e("checkNot","withoutdata")
                val goToCropScreen = Intent(this@TestCameraScreen, ScanActivity::class.java)
                startActivityForResult(goToCropScreen, REQUEST_CODE)
            }
        }


         if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.e("outside","outside")
            if (fromNotification){
                Log.e("checkNot","withdata")

                val next = Intent(this@TestCameraScreen, TestPreviewScreen::class.java)
                next.putExtra("document_request_id", intent_document_request_id)
                next.putExtra("document_type", intent_document_type)
                next.putExtra("document_name", intent_document_name)
                next.putExtra("load_number", intent_load_number)
                next.putExtra("due_date", intent_due_date)
                next.putExtra("comment", intent_comment)
                next.putExtra("status", intent_status)
                next.putExtra("key", intent_key)
                next.putExtra("is_expired", intent_is_expired)
                next.putExtra("document_belongs_to", intent_doc_belongs_to)
                startActivity(next)

            }else {
                Log.e("checkNot","withoutdata-----")

                var intent = Intent(this@TestCameraScreen,TestPreviewScreen::class.java)
                startActivity(intent)
            }



        }

    }

    @OnClick(R.id.capture_layout)
    fun txtClick() {
        if (has_camera_permission == 1) {
            mImageSurfaceView!!.autoCapture(ScanHint.CAPTURING_IMAGE)
        } else {
            Toast.makeText(this@TestCameraScreen, "Please Enable Camera Permissions First.", Toast.LENGTH_SHORT).show()
        }
    }

    //
    @OnClick(R.id.flash_layout)
    fun flashFunction() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            // Do something for lollipop and above versions
            Log.e("true", "true")
            //            Toast.makeText(TestCameraScreen.this, "Under Development", Toast.LENGTH_SHORT).show();
//            Camera cam = Camera.open();
//            Camera.Parameters p = cam.getParameters();
//            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//            cam.setParameters(p);
//            cam.startPreview();
//                manager.setTorchMode(cameraId,true);
            Log.e("false", "false")
            if (first_flash_call) {
                first_flash_call = false
                mImageSurfaceView!!.turnOnFlash = true
                mImageSurfaceView.flashMode()
            }
            if (is_flash_enabled) {
                is_flash_enabled = false
                mImageSurfaceView!!.turnOnFlash = false
                mImageSurfaceView.flashMode()
                bottom_lights!!.setImageResource(R.drawable.headlight_off)
                bottom_flash_text!!.text = resources.getString(R.string.lights_off)
            } else {
                is_flash_enabled = true
                mImageSurfaceView!!.turnOnFlash = true
                mImageSurfaceView.flashMode()
                bottom_lights!!.setImageResource(R.drawable.headlight)
                bottom_flash_text!!.text = resources.getString(R.string.lights_on)
            }
        } else {
            Log.e("false", "false")
            if (first_flash_call) {
                first_flash_call = false
                mImageSurfaceView!!.turnOnFlash = true
                mImageSurfaceView.flashMode()
            }
            if (is_flash_enabled) {
                is_flash_enabled = false
                mImageSurfaceView!!.turnOnFlash = false
                mImageSurfaceView.flashMode()
                bottom_lights!!.setImageResource(R.drawable.headlight_off)
                bottom_flash_text!!.text = resources.getString(R.string.lights_off)
            } else {
                is_flash_enabled = true
                mImageSurfaceView!!.turnOnFlash = true
                mImageSurfaceView.flashMode()
                bottom_lights!!.setImageResource(R.drawable.headlight)
                bottom_flash_text!!.text = resources.getString(R.string.lights_on)
            }
            // do something for phones running an SDK before lollipop
        }
    }




    fun initCamera(){

        Log.e("called","initCamera")

        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK)
        } catch (e: RuntimeException) {
            e.stackTrace
            Toast.makeText(this@TestCameraScreen, "cannot open camera, please grant camera", Toast.LENGTH_SHORT).show()
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
        val pm = this@TestCameraScreen.packageManager
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


    private var busy: Boolean = false

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
        progress_bar.visibility = View.GONE

        initCamera()
    }

    override fun onPictureTaken(p0: ByteArray?, p1: Camera?) {
        Log.e("called","onPictureTaken")
//        var bitmap = ScanUtils.decodeBitmapFromByteArray(p0,
//                ScanConstants.HIGHER_SAMPLING_THRESHOLD, ScanConstants.HIGHER_SPLING_THRESHOLD)
//        //val bitmap: Bitmap = BitmapFactory.decodeByteArray(p0, 0, p0!!.size)

        captureHintLayout!!.visibility = View.VISIBLE
        captureHintText!!.text = "Captured!! Wait a moment please"
        captureHintLayout!!.background = resources.getDrawable(R.drawable.hint_green)
        p1?.stopPreview()
        ScanAppController.nowByteArray = p0
        if (fromNotification){
            Log.e("checkNot","withdata")
            val goToCropScreen = Intent(this@TestCameraScreen, ScanActivity::class.java)
            goToCropScreen.putExtra("document_request_id", intent_document_request_id)
            goToCropScreen.putExtra("document_type", intent_document_type)
            goToCropScreen.putExtra("document_name", intent_document_name)
            goToCropScreen.putExtra("load_number", intent_load_number)
            goToCropScreen.putExtra("due_date", intent_due_date)
            goToCropScreen.putExtra("comment", intent_comment)
            goToCropScreen.putExtra("status", intent_status)
            goToCropScreen.putExtra("key", intent_key)
            goToCropScreen.putExtra("is_expired", intent_is_expired)
            goToCropScreen.putExtra("document_belongs_to", intent_doc_belongs_to)
            startActivityForResult(goToCropScreen, REQUEST_CODE)
        }else {
            Log.e("checkNot","withoutdata")
            val goToCropScreen = Intent(this@TestCameraScreen, ScanActivity::class.java)
            startActivityForResult(goToCropScreen, REQUEST_CODE)
        }

//        TODO uncomment onPictureTaken
//        ScanAppController.nowByteArray = p0
//        Log.e("called","after bytearray saving")
//
//        if(fromNotification) {
//            Log.e("checkNot","withdata")
//            val goToCropScreen = Intent(this@TestCameraScreen, ScanActivity::class.java)
//            goToCropScreen.putExtra("document_request_id", intent_document_request_id)
//            goToCropScreen.putExtra("document_type", intent_document_type)
//            goToCropScreen.putExtra("document_name", intent_document_name)
//            goToCropScreen.putExtra("load_number", intent_load_number)
//            goToCropScreen.putExtra("due_date", intent_due_date)
//            goToCropScreen.putExtra("comment", intent_comment)
//            goToCropScreen.putExtra("status", intent_status)
//            goToCropScreen.putExtra("key", intent_key)
//            goToCropScreen.putExtra("is_expired", intent_is_expired)
//            goToCropScreen.putExtra("document_belongs_to", intent_doc_belongs_to)
//            startActivityForResult(goToCropScreen, REQUEST_CODE)
//            busy = false
//        }else {
//            Toast.makeText(this,"callled",Toast.LENGTH_SHORT).show()
//            Log.e("checkNot","withoutdata")
//            val goToCropScreen = Intent(this@TestCameraScreen, ScanActivity::class.java)
//            startActivityForResult(goToCropScreen, REQUEST_CODE)
//            busy = false
//        }


    }



    override fun onPreviewFrame(p0: ByteArray?, p1: Camera?) {

    }


}