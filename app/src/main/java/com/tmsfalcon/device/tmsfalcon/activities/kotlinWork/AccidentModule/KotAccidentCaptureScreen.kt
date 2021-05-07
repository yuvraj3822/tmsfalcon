package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters.DamageImageAdapter
import com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters.KotReportAccidentQueueAdapter
import com.tmsfalcon.device.tmsfalcon.customtools.AppController
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.CaptureScreenAccident
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.OnSearchItemSelected
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.SearchListItem
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.SearchableDialog
import kotlinx.android.synthetic.main.activity_accident_capture_screen.*
import kotlinx.android.synthetic.main.dialog_camera_holistic_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class KotAccidentCaptureScreen : NavigationBaseActivity(), OnSearchItemSelected, ViewPager.OnPageChangeListener {

    lateinit var alertDialog: AlertDialog
    lateinit var doc_type_adapter: ArrayAdapter<*>
    var doc_type_arraylist = ArrayList<String?>()
    var imageHelper: ImageHelper? = null
    var adapter: KotReportAccidentQueueAdapter? = null
    var sessionManager: SessionManager? = null
    lateinit var doc_type: AppCompatSpinner

    var img_description: EditText? = null
    var model_arrayList = ArrayList<AccidentCaptureModel>()
    var images_arraylist = ArrayList<String>()
    var mCameraFileName: String? = ""

    var accident_types_list: ArrayList<SearchListItem> = ArrayList()
    lateinit var searchableDialog:SearchableDialog
    lateinit var accident_type:TextView
    lateinit var db: AccidentModuleDb

    var REQUEST_VIDEO_CAPTURE = 420
    lateinit var outuri:Uri
     var lng:String = ""
     var lat:String = ""

    var currentPageFragNo:Int = 0
    var listCaptureScreenData = ArrayList<CaptureScreenAccident>()
    private lateinit var fusedLocationClientCapt: FusedLocationProviderClient

    override fun onDestroy() {
        super.onDestroy()

//        if (alertDialog != null) {
//            alertDialog!!.dismiss()
//            alertDialog = null
//        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_accident_capture_screen, null, false)

        drawer.addView(contentView, 0)
        ButterKnife.bind(this)

        init()
        createList()
        clickListenerActivity()

//        isThereDataInDb()
        checkGpsEnabled()
        progress_bar.visibility = View.VISIBLE
        timer.start()
    }





    var timer  = object : CountDownTimer(2000,1000){
        override fun onFinish() {
            /**
             * This method called inside the countdown timer
             * to avoid freezing of the UI
             */
            isThereDataInDb()
        }
        override fun onTick(p0: Long) {

        }
    }


    fun clickListenerActivity(){

        skip.setOnClickListener {
            performCameraDialog()
        }
        next.setOnClickListener {
            currentPageFragNo++
            if(currentPageFragNo == 3){
                Log.e("tag","navigate to next screen")
                performCameraDialog()
            }else {
                pager.currentItem = currentPageFragNo
            }
        }
    }


    fun performCameraDialog(){

        /**
         *  1st make view gone for the Viewpager
         */
        info_view.visibility = View.GONE

        /**
         * than show alert dialog for camera menus
         */
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_camera_holistic_view, null)
            dialogBuilder.setView(dialogView)
            dialogBuilder.create()
            dialogBuilder.setCancelable(true)
            var alertDialog = dialogBuilder.show()

            dialogView.camera_view.setOnClickListener {
            alertDialog.dismiss()
                ImagePicker.with(this@KotAccidentCaptureScreen)
                    .cameraOnly()
                    .start()
            }

            dialogView.gallery_view.setOnClickListener {
                alertDialog.dismiss()
                ImagePicker.with(this@KotAccidentCaptureScreen)
                        .galleryOnly()
                        .start()
            }

         /*   dialogView.video_view.setOnClickListener {
                alertDialog.dismiss()
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

            }*/







    dialogView.video_view.setOnClickListener {
        alertDialog.dismiss()


        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)

        val newPicFile = imageHelper!!.uniqueFileName() + ".mp4"

        val root_directory_name = "tmsfalcon"
        val root = Environment.getExternalStorageDirectory().toString() + "/" + root_directory_name
        val outPath = "$root/$newPicFile"
        Log.e("outPath",": $outPath")

        outuri = Uri.parse(outPath)
        val outFile = File(outPath)

        mCameraFileName = outFile.toString()
//        val outuri = Uri.fromFile(outFile)

        var outUri = FileProvider.getUriForFile(this@KotAccidentCaptureScreen, getApplicationContext().getPackageName().toString() + ".provider", outFile)

        Log.e("outPath", " is $outPath")
        Log.e("mCameraFileName", " is $mCameraFileName")
        Log.e("outuri", " is $outUri")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 90)
        startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)



//        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
//            takeVideoIntent.resolveActivity(packageManager)?.also {
//                startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
//            }
//        }
    }


    }

    fun checkGpsEnabled(){


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClientCapt.lastLocation.addOnSuccessListener {
            if (it!=null){
                if (it.latitude!=null){
                    lat = it.latitude.toString()
                    lng = it.longitude.toString()
                } else {
                    lat = ""
                    lng = ""
                }
            } else {
                lat = ""
                lng = ""
            }
        }


//
//        val nManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        if (!nManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Log.e("tag","gps not enabled")
//            showGPSDisabledAlertToUser()
//        } else {
//            Log.e("tag","gps enabled")
//
//
////            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                    != PackageManager.PERMISSION_GRANTED
////                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
////                    != PackageManager.PERMISSION_GRANTED) {
////
////
////                return
////            }
////            fusedLocationClient.lastLocation
////                    .addOnSuccessListener { location : Location? ->
////                        if (location!=null){
////                            if (location.latitude!=null){
////                                lat = location?.latitude.toString()
////                                lng =location?.longitude.toString()
////                            } else {
////                                lat = ""
////                                lng = ""
////                            }
////                        }else {
////                            lat = ""
////                            lng = ""
////                        }
////
////                    }
//
////            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
////                    != PackageManager.PERMISSION_GRANTED
////                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
////                    != PackageManager.PERMISSION_GRANTED) {
////
////
////                return
////            }
////        fusedLocationClientCapt.lastLocation.addOnSuccessListener {
////            if (it!=null){
////                            if (it.latitude!=null){
////                                lat = it?.latitude.toString()
////                                lng =it?.longitude.toString()
////                            } else {
////                                lat = ""
////                                lng = ""
////                            }
////                        }else {
////                            lat = ""
////                            lng = ""
////                        }
////        }
//
//        }
    }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(this,android.R.style.Theme_DeviceDefault_Dialog_Alert)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS"
                ) { dialog, id ->
                    val callGPSSettingIntent = Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(callGPSSettingIntent)
                }
        alertDialogBuilder.setNegativeButton("Cancel"
        ) { dialog, id -> dialog.cancel() }
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    /**
     * open the popup to add the data or
     * show the already saved data.
     */
    fun isThereDataInDb(){
        CoroutineScope(IO).launch {
            var listCaptureData = db.csaDao().getImageListss() as ArrayList
            if (listCaptureData.isEmpty()){
                CoroutineScope(Main).launch {
                    progress_bar.visibility = View.GONE
//                    showPopUp()


                    message.visibility = View.VISIBLE
                    next_btn.setText("Nothing To Capture")

                }
            }else {
                listCaptureScreenData.addAll(listCaptureData)
                CoroutineScope(Main).launch {
                    progress_bar.visibility = View.GONE
                    adapter!!.notifyDataSetChanged()
                    message.visibility = View.GONE
                    next_btn.setText("Done")

                }
            }
        }
    }

    fun clickListener(){

        searchableDialog = SearchableDialog(this, accident_types_list, "Type")
        searchableDialog.setOnItemSelected(this)
        accident_type.setOnClickListener {
            searchableDialog.show()
        }
    }


    fun createList(){
        for ((index,value) in resources.getStringArray(R.array.picture_array).withIndex()){
            accident_types_list.add(SearchListItem(index,value))
        }
        Log.e("size",": "+accident_types_list.size)
    }

    fun init() {
        imageHelper = ImageHelper()
        sessionManager = SessionManager(this@KotAccidentCaptureScreen)
        searchableDialog = SearchableDialog(this, accident_types_list, "Type")
        db = AccidentModuleDb.getDatabase(this@KotAccidentCaptureScreen)
        adapter = KotReportAccidentQueueAdapter(this@KotAccidentCaptureScreen, this@KotAccidentCaptureScreen, listCaptureScreenData)
        grid_view!!.adapter = adapter
        fusedLocationClientCapt = LocationServices.getFusedLocationProviderClient(this@KotAccidentCaptureScreen)

    }


    fun manageFragmentView(){

        if (listCaptureScreenData.isEmpty()){
            /**
             * make viewpager view visible
             */
            info_view.visibility = View.VISIBLE
            /**
             * now set viewpager and its adapter
             */
            val pagerAdapter = DamageImageAdapter(supportFragmentManager)
            pager.adapter = pagerAdapter
            tab_layout.setupWithViewPager(pager)
            pager.addOnPageChangeListener(this@KotAccidentCaptureScreen)
        } else {
            performCameraDialog()
        }
    }





    fun showPopUp() {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_kot_accident_capture_picture, null)
        dialogBuilder.setView(dialogView)
        doc_type = dialogView.findViewById(R.id.doc_type_spinner)
//        img_name = dialogView.findViewById(R.id.accident_type)
        img_description = dialogView.findViewById(R.id.img_description)
        val capture_btn = dialogView.findViewById<Button>(R.id.capture_btn)
        accident_type = dialogView.findViewById<Button>(R.id.accident_type)
        val string_array = resources.getStringArray(R.array.picture_array)

        clickListener()


// TODO SETUP
//        val img_type_adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this@AccidentCaptureScreen, android.R.layout.simple_spinner_item, string_array)
//        img_name.setAdapter(img_type_adapter)

        doc_type_arraylist.clear()
        doc_type_arraylist.add("Picture")
        doc_type_arraylist.add("Document")
        doc_type_arraylist.add("Video")
        doc_type_adapter = ArrayAdapter<String?>(this@KotAccidentCaptureScreen, android.R.layout.simple_spinner_item, doc_type_arraylist)
        doc_type_adapter.setDropDownViewResource(R.layout.custom_textview_to_spinner)
        doc_type.setAdapter(doc_type_adapter)
        doc_type.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                Log.e("i", "" + i)
                val textView = adapterView.getChildAt(0) as TextView
                if (textView != null) {
                    textView.setTextColor(resources.getColor(R.color.light_greyish))
                    textView.textSize = 15f
                }

// TODO
//                val img_type_adapter: ArrayAdapter<*>
//                if (i == 0) {
//                    val string_array = resources.getStringArray(R.array.picture_array)
//                    img_type_adapter = ArrayAdapter<Any?>(this@AccidentCaptureScreen, android.R.layout.simple_spinner_item, string_array)
//                    img_name.setAdapter<ArrayAdapter<*>>(img_type_adapter)
//                } else if (i == 1) {
//                    val string_array = resources.getStringArray(R.array.document_array)
//                    img_type_adapter = ArrayAdapter<Any?>(this@AccidentCaptureScreen, android.R.layout.simple_spinner_item, string_array)
//                    img_name.setAdapter<ArrayAdapter<*>>(img_type_adapter)
//                }
//                img_name.showDropDown();



            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
//        TODO
//        img_name.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus -> if (hasFocus) img_name.showDropDown() })
        alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        //emailAlertDialog.setTitle("Send Documents By Email");
        alertDialog.show()
        capture_btn.setOnClickListener {

            alertDialog.dismiss()
            manageFragmentView()

//            alertDialog.dismiss()
//            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
//            val inflater = this.layoutInflater
//            val dialogView = inflater.inflate(R.layout.dialog_camera_holistic_view, null)
//            dialogBuilder.setView(dialogView)
//            dialogBuilder.create()
//            dialogBuilder.setCancelable(true)
//            var alertDialog = dialogBuilder.show()
//
//            dialogView.camera_view.setOnClickListener {
//            alertDialog.dismiss()
//                ImagePicker.with(this@KotAccidentCaptureScreen)
//                    .cameraOnly()
//                    .start()
//            }
//
//            dialogView.gallery_view.setOnClickListener {
//                alertDialog.dismiss()
//                ImagePicker.with(this@KotAccidentCaptureScreen)
//                        .galleryOnly()
//                        .start()
//            }
//
//            dialogView.video_view.setOnClickListener {
//                alertDialog.dismiss()
//                val builder = StrictMode.VmPolicy.Builder()
//                StrictMode.setVmPolicy(builder.build())
//                val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//                val newPicFile = ImageHelper()!!.uniqueFileName() + ".mp4"
//                val root_directory_name = "tmsfalcon"
//                val root = Environment.getExternalStorageDirectory().toString() + "/" + root_directory_name
//                val outPath = "$root/$newPicFile"
//                val outFile = File(outPath)
//                outuri = Uri.fromFile(outFile)
//                Log.e("outuri", " is: $outuri")
//                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, outuri)
//                takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 90)
//
//                if (takeVideoIntent.resolveActivity(packageManager) != null) {
//                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
//                }
//
//            }

        }
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(alertDialog.getWindow()!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        alertDialog.getWindow()!!.attributes = lp



    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == ImagePicker.REQUEST_CODE) {
                val filePath:String = ImagePicker.getFilePath(data)!!
                saveAndShowFromDb(filePath,false)
            }
            else if (requestCode === REQUEST_VIDEO_CAPTURE){
//                val bMap: Bitmap = ThumbnailUtils.createVideoThumbnail(outuri.path, MediaStore.Video.Thumbnails.MICRO_KIND)

//                Todo uncomment

//                if (data != null) {
//                    Log.e("well","well: "+data.data.path)
//                }

                outuri.path?.let { saveAndShowFromDb(it,true) }

//                if (data != null) {
//                    saveAndShowFromDb(data.data.path,true)
//                }else {
//                    Toast.makeText(this@KotAccidentCaptureScreen,"No data from activity result",Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }

    fun saveTextStatus(){
        if (next_btn.text.toString().equals("Done",ignoreCase = true)){
            sessionManager?.saveStatusPhotographicTextPref()
        } else {
            sessionManager?.clearTextPhotographicPref()
        }

    }

    fun clearTextStatus(){
        sessionManager?.clearTextPhotographicPref()
    }

    @OnClick(R.id.next_btn)
    fun goToNext() {


        CoroutineScope(IO).launch {
            var list  = db.csaDao().getImageListss()
            if (list.isEmpty()){
                saveTextStatus()
                saveDBStatus()
                val returnIntent = Intent()
                returnIntent.putExtra("result", "result")
                setResult(Activity.RESULT_OK, returnIntent)
                finish()

            }else {
                clearTextStatus()
                saveDBStatus()
                val returnIntent = Intent()
                returnIntent.putExtra("result", "result")
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }
        }

//
//        saveDBStatus()
//        val returnIntent = Intent()
//        returnIntent.putExtra("result", "result")
//        setResult(Activity.RESULT_OK, returnIntent)
//        finish()
    }


    fun saveAndShowFromDb(filePath:String,isItVideo:Boolean){
        CoroutineScope(IO).launch {

            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("dd:MM:yyyy hh:mm:ss")
            val today: String = formatter.format(date)

            var captureData = CaptureScreenAccident(null,sessionManager!!._driver_id,doc_type.selectedItem.toString(),filePath, today,lat,lng, AppController.accident_report_id,img_description?.text.toString(),accident_type.text.toString(),isItVideo)
            db.csaDao().insert(captureData)
            listCaptureScreenData.clear()

            var listCaptureData = db.csaDao().getImageListss() as ArrayList
            listCaptureScreenData.addAll(listCaptureData)

            CoroutineScope(Main).launch {
                adapter!!.notifyDataSetChanged()
                if (listCaptureData.isEmpty()) {
                    message.visibility = View.VISIBLE
                    next_btn.setText("Nothing To Capture")
                } else {
                    message.visibility = View.GONE
                    next_btn.setText("Done")
                }

//                Toast.makeText(applicationContext, "Image Saved!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    fun saveDBStatus(){
        sessionManager?.saveStatusPhotographicPref()
    }





    @OnClick(R.id.capture)
    fun capturePic() {
        showPopUp()
//        alertDialog.dismiss()
//        manageFragmentView()
    }

    companion object {
        var CAMERA_CODE = 15
    }

    override fun onClick(position: Int, searchListItem: SearchListItem) {
        accident_type.text = searchListItem.title
        hideKeyboardFrom(this@KotAccidentCaptureScreen,accident_type)
        searchableDialog.dismiss()
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        currentPageFragNo = position
        visibilityBottomNavigation(position)
    }


    fun visibilityBottomNavigation(position: Int){
        if (position == 2){
            next.text = "Got it!!"
            skip.visibility = View.GONE

        } else {
            next.text = "Next"
            skip.visibility = View.VISIBLE
        }
    }


}