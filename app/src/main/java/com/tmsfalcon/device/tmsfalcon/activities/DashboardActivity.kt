package com.tmsfalcon.device.tmsfalcon.activities

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.androidnetworking.BuildConfig
import com.scanlibrary.ScanConstants
import com.scanlibrary.ScansActivity
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestCameraScreen
import com.tmsfalcon.device.tmsfalcon.customtools.*
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable
import com.tmsfalcon.device.tmsfalcon.services.GpsTrackerService
import kotlinx.android.synthetic.main.demo_dashboard.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class DashboardActivity : NavigationBaseActivity(), View.OnClickListener {
    var session: SessionManager? = null
    var networkValidator: NetworkValidator? = null
    var TAG = this.javaClass.simpleName
    var context: Context = this@DashboardActivity
    var captureLayout: LinearLayout? = null
    var uploadLayout: LinearLayout? = null
    var loadLayout: LinearLayout? = null
    var getCashLayout: LinearLayout? = null
    var callLayout: RelativeLayout? = null
    var is_flash_enabled = false
    var bottom_flash_text: TextView? = null
    var bottom_lights: ImageView? = null
    private var cameraId: String? = null
    var manager: CameraManager? = null
    var characteristics: CameraCharacteristics? = null
   lateinit var lights_layout: RelativeLayout
    private var camera: Camera? = null
    var params: Camera.Parameters? = null
    private var hasFlashCameraOne = false
    var db: DirectUploadTable? = null
    var company_name: String? = null
    var company_phone: String? = null
    var dispatcher_name = ""
    var dispatcher_phone: String? = ""
    var permissionsManager = PermissionManager()
    var progressBar: ProgressBar? = null
    var expired_document_count = 0
    val url = "https://androidquery.appspot.com/api/market?app=com.tmsfalcon.device.tmsfalcon"
    var appStoreVersion: String? = null
    private val REQUEST_CAMERA_PERMISSION = 0
    private val REQUEST_CODE = 99


    override fun onCreate(savedInstanceState: Bundle?) {
        // Inflate the layout for this fragment
        /*View view = inflater.inflate(R.layout.fragment_dashboard, container, false);*/
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        Log.e("checkscreens","DashboardActivity")
        //inflate your activity layout here!
        val contentView = inflater.inflate(R.layout.demo_dashboard, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this@DashboardActivity)
        initIds()
        session!!.checkLogin()


        // Log.e("db address", DebugDB.getAddressLog());
        back_btn.visibility = View.GONE
        val params = frameLayoutBell.layoutParams as RelativeLayout.LayoutParams
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        params.setMargins(0, 0, 20, 0)
        frameLayoutBell.layoutParams = params
        driver_name_text!!.text = "Hi " + session!!._driver_fullname + " !!"
        company_name_text!!.text = "Welcome to " + session!!._company_name + " !!"

        //expired_document_count = SessionManager.getInstance().getExpiredDocCount();
        Log.e("session token ", "" + session!!._token)
        //setExpiredDocLayout();
        captureLayout!!.setOnClickListener {
//            val i = Intent(this@DashboardActivity, DirectUploadCameraScreen::class.java)
//            startActivity(i)
            if (initPermission()){
//                selectImage(this@TestDashboardActivity)
                val i = Intent(this@DashboardActivity, TestCameraScreen::class.java)
                startActivity(i)
            }

        }
        uploadLayout!!.setOnClickListener {
            val i = Intent(this@DashboardActivity, RequestedDocumentsActivity::class.java)
            startActivity(i)
        }
        callLayout!!.setOnClickListener {
            val goToPhone = Intent(this@DashboardActivity, PhoneActivity::class.java)
            startActivity(goToPhone)
            /*if (!permissionsManager.checkPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.CALL_PHONE)) {
                    Toast.makeText(DashboardActivity.this, "Please Grant Permission to call", Toast.LENGTH_LONG).show();
                    permissionsManager.askForPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.CALL_PHONE, CALL);
                } else {
                    fetchPhones();
                }*/
        }
        loadLayout!!.setOnClickListener {
            val i = Intent(this@DashboardActivity, LoadsActivity::class.java)
            startActivity(i)
        }
        getCashLayout!!.setOnClickListener {
//            val load_board = Intent(this@DashboardActivity, LoadBaordActivity::class.java)
//            startActivity(load_board)

            val getCash = Intent(this@DashboardActivity, LoadBaordActivity::class.java)
            startActivity(getCash)


            /*if(AppController.first_time_login == 1){
                    Intent load_board = new Intent(DashboardActivity.this, LoadBoardPreferences.class);
                    startActivity(load_board);
                }
                else{
                    Intent load_board = new Intent(DashboardActivity.this, LoadBaordActivity.class);
                    startActivity(load_board);
                }*/
        }
        db = DirectUploadTable(this@DashboardActivity)
        db!!.deleteAllRecords()
        cameraSettings()
        deviceInfo
        if (networkValidator!!.isNetworkConnected) {
            GetVersionCode().execute()
        } else {
            Toast.makeText(this@DashboardActivity, resources.getString(R.string.network_error), Toast.LENGTH_LONG).show()
        }
        checkLocationPermissions()
        Log.e("id", " is " + SessionManager.getInstance()._driver_id.toString())
        checkWhetherCameraIsPresent(isCameraAvailable(this@DashboardActivity))

    }

    fun isCameraAvailable(context: Context): Boolean {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun checkWhetherCameraIsPresent(isAvailable:Boolean){
        if (!isAvailable) {
            AlertDialog.Builder(this@DashboardActivity, R.style.Theme_AppCompat_Dialog_Alert)
                    .setTitle("Sorry!!")
                    .setCancelable(false)
                    .setMessage("This device does not support camera?") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        dialog.dismiss()
                        finishAffinity()
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }
    }


    /*String osName = fields[Build.VERSION.SDK_INT + 1].getName();*/
    private val deviceInfo:
            // Log.e("details",details);
            Unit
        private get() {
            val fields = Build.VERSION_CODES::class.java.fields
            /*String osName = fields[Build.VERSION.SDK_INT + 1].getName();*/
            val osName = fields[Build.VERSION.SDK_INT].name
            val details = """
                VERSION.RELEASE : ${Build.VERSION.RELEASE}
                VERSION.INCREMENTAL : ${Build.VERSION.INCREMENTAL}
                VERSION.SDK.NUMBER : ${Build.VERSION.SDK_INT}
                Code name : $osName
                BOARD : ${Build.BOARD}
                BOOTLOADER : ${Build.BOOTLOADER}
                BRAND : ${Build.BRAND}
                CPU_ABI : ${Build.CPU_ABI}
                CPU_ABI2 : ${Build.CPU_ABI2}
                DISPLAY : ${Build.DISPLAY}
                FINGERPRINT : ${Build.FINGERPRINT}
                HARDWARE : ${Build.HARDWARE}
                HOST : ${Build.HOST}
                ID : ${Build.ID}
                DEVICE : ${Build.DEVICE}
                MANUFACTURER : ${Build.MANUFACTURER}
                MODEL : ${Build.MODEL}
                PRODUCT : ${Build.PRODUCT}
                SERIAL : ${Build.SERIAL}
                TAGS : ${Build.TAGS}
                TIME : ${Build.TIME}
                TYPE : ${Build.TYPE}
                UNKNOWN : ${Build.UNKNOWN}
                USER : ${Build.USER}
                """.trimIndent()
            // Log.e("details",details);
        }

    
    fun initPermission():Boolean{
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
            return false
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            return false
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION)
            return false
        }
            return true
    }


    private inner class GetVersionCode : AsyncTask<Void?, String?, String?>() {

        override fun onPostExecute(onlineVersion: String?) {
            super.onPostExecute(onlineVersion)
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                val document = Jsoup.connect("https://play.google.com/store/apps/details?id=com.tmsfalcon.device.tmsfalcon&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                if (document != null) {
                    val element = document.getElementsContainingOwnText("Current Version")
                    for (ele in element) {
                        if (ele.siblingElements() != null) {
                            val sibElemets = ele.siblingElements()
                            for (sibElemet in sibElemets) {
                                appStoreVersion = sibElemet.text()
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.e("appStoreVersion", " is $appStoreVersion")
            registerDeviceIdToServer()
            return appStoreVersion
        }
    }

    private fun registerDeviceIdToServer() {
        // Tag used to cancel the request
        val tag_json_obj = "register_device_id"
        val device_full_type = Build.BRAND + "-" + Build.MODEL
        val url = UrlController.REGISTER_DEVICE_TOKEN
        val fields = Build.VERSION_CODES::class.java.fields
        val osName = fields[Build.VERSION.SDK_INT].name
        val params: MutableMap<String?, String?> = HashMap()
        params["device_token"] = SessionManager.getInstance().deviceToken
        params["device_type"] = "android"
        params["device_version"] = Build.VERSION.RELEASE
        params["device_version_name"] = osName
        params["manufacturer"] = Build.MANUFACTURER
        params["device_full_type"] = device_full_type
        params["installed_mobile_app_version"] = BuildConfig.VERSION_NAME
        params["online_mobile_app_version"] = appStoreVersion
        Log.e("in", "params $params")
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                url, JSONObject(params as Map<*, *>),
                Response.Listener { response ->
                    var status: Boolean? = null
                    val json_messages: JSONArray
                    var messages = ""
                    var expired_count = 0
                    val data: JSONObject
                    val interval_settings: JSONObject
                    try {
                        status = response.getBoolean("status")
                        data = response.getJSONObject("data")
                        json_messages = response.getJSONArray("messages")
                        val notification_count = response.getInt("count")
                        SessionManager.getInstance().storeNotificationCount(notification_count.toString())
                        Utils.setNotificationCount(cartBadgeTextView, SessionManager.getInstance().notificationCount)
                        val loan_count = response.getInt("loan_count")
                        //int loan_count = 0;
                        setVisibilityForLoan(loan_count)
                        expired_count = response.getInt("expired_count")
                        session!!.storeExpiredDocumentsCount(expired_count)
                        expired_document_count = SessionManager.getInstance().expiredDocCount
                        setExpiredDocLayout()
                        for (i in 0 until json_messages.length()) {
                            messages += """
                                ${json_messages[i]}

                                """.trimIndent()
                        }
                        if (data.getString("gps_interval_settings") != JSONObject.NULL) {
                            interval_settings = data.getJSONObject("gps_interval_settings")
                            val off_duty = interval_settings.optInt("off_duty_interval")
                            val on_duty = interval_settings.optInt("on_duty_interval")
                            val off_duty_metres = interval_settings.optInt("off_duty_metres")
                            val on_duty_metres = interval_settings.optInt("on_duty_metres")
                            //int interval = interval_settings.optInt("interval");
                            val current = interval_settings.optString("current_status")
                            val location_status = interval_settings.optBoolean("location_status")
                            //int radius = interval_settings.optInt("radius");
                            SessionManager.getInstance().storeGpsParameters(off_duty, on_duty, off_duty_metres, on_duty_metres, current, location_status)
                        }
                        var widget_json: JSONObject? = null
                        if (response.optString("load_widgets") != "null") {
                            if (response.optString("load_widgets") != JSONObject.NULL) {
                                widget_json = response.getJSONObject("load_widgets")
                                val pickup_location = widget_json.getString("start_location")
                                val delivery_location = widget_json.getString("end_location")
                                val settlement_amount = widget_json.getString("settlements_total")
                                SessionManager.getInstance().storeWidgetVariables(pickup_location, delivery_location, settlement_amount)
                                Log.e("widgets data ", "pickup_location=>" + SessionManager.getInstance().keyWidgetLoadPickupLocation +
                                        " delivery_location=>" + SessionManager.getInstance().keyWidgetLoadDeliveryLocation +
                                        " settlement_amount=>" + SessionManager.getInstance().keyWidgetSettlementAmount)
                            }
                        }


                        // Toast.makeText(Dashboard.this,messages,Toast.LENGTH_LONG).show();
                    } catch (e: JSONException) {
                        Log.e("exception ", e.toString())
                    }

                    //Log.e("registerDeviceIdToServer", response.toString());
                }, Response.ErrorListener { error -> //Log.e(" registerDeviceIdToServer", error.toString());
            ErrorHandler.setVolleyMessage(this@DashboardActivity, error)
        }) {
            /**
             * Passing some request headers
             */
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                //headers.put("Content-Type", "application/json");
                headers["Token"] = session!!._token
                return headers
            }
        }
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj)
    }

    fun checkLocationPermissions() {
        if (!permissionsManager.checkPermission(this@DashboardActivity, this@DashboardActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsManager.askForPermission(this@DashboardActivity, this@DashboardActivity, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION)
        }
        if (Utils.isLocationEnabled(this@DashboardActivity)) {
            Log.e("Location Enabled ", "True")
            startGpsTrackerService()
        } else {
            Log.e("Location Enabled ", "False")
            val builder = AlertDialog.Builder(this@DashboardActivity)
            builder.setCancelable(false)
            builder.setTitle(R.string.gps_not_found_title) // GPS not found
            builder.setMessage(R.string.gps_not_found_message) // Want to enable?
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1)
                // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
            builder.setNegativeButton(R.string.no) { dialog, which ->
                val dialog_i = Intent(this@DashboardActivity, DialogActivity::class.java)
                dialog_i.putExtra("is_location_dialog", true)
                startActivity(dialog_i)
            }
            builder.create().show()
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            when (requestCode) {
                1 -> startGpsTrackerService()
            }
        }
    }

    fun startGpsTrackerService() {
        //startService(new Intent(DashboardActivity.this, LocationService.class));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(Intent(this@DashboardActivity, GpsTrackerService::class.java))
        } else {
            startForegroundService(Intent(this@DashboardActivity, GpsTrackerService::class.java))
        }
    }

    private fun setExpiredDocLayout() {
        if (expired_document_count > 0) {
            var header_text = ""
            doc_count_header!!.visibility = View.VISIBLE
            if (expired_document_count > 1) {
                header_text = "$expired_document_count documents have been expired."
            } else if (expired_document_count == 1) {
                header_text = "$expired_document_count document has been expired."
            }
            doc_count_text!!.text = header_text
        } else {
            doc_count_header!!.visibility = View.GONE
        }
    }

    /*
    @Override
    protected void onResume() {

        super.onResume();
        //Start Gps Tracker Service
        if(Utils.isGooglePlayServicesAvailable(DashboardActivity.this)){
            Log.e("Google Play Service","Enabled");
            if (!permissionsManager.checkPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && !permissionsManager.checkPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) && !permissionsManager.checkPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.WAKE_LOCK)) {
                permissionsManager.askForPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION);
                permissionsManager.askForPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION);
                permissionsManager.askForPermission(DashboardActivity.this, DashboardActivity.this, Manifest.permission.WAKE_LOCK, WAKE_LOCK);
            }
            else{
                if(Utils.isLocationEnabled(DashboardActivity.this)){
                    Log.e("Location Enabled ","True");
                    startGpsTrackerService();
                }
                else{
                    Log.e("Location Enabled ","False");
                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                    builder.setTitle(R.string.gps_not_found_title);  // GPS not found
                    builder.setMessage(R.string.gps_not_found_message); // Want to enable?
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
                    builder.setNegativeButton(R.string.no, null);
                    builder.create().show();
                    return;

                }

            }

        }
        else{
            Log.e("Google Play Service","Disabled");
            Toast.makeText(DashboardActivity.this,getResources().getString(R.string.google_play_services_error),Toast.LENGTH_LONG).show();
        }

    }*/
    private fun call_action() {
        val dialog = Dialog(this@DashboardActivity)
        dialog.setContentView(R.layout.call_layout)
        dialog.show()
        val lp = dialog.window!!.attributes
        lp.dimAmount = 0.85f
        dialog.window!!.attributes = lp
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val company_textview = dialog.findViewById<TextView>(R.id.company)
        val dispatcher_textview = dialog.findViewById<TextView>(R.id.dispatcher)
        company_textview.text = "Call $company_name"
        dispatcher_textview.text = "Call $dispatcher_name"
        val call_company = dialog.findViewById<LinearLayout>(R.id.company_layout)
        val call_dispatcher = dialog.findViewById<LinearLayout>(R.id.dispatcher_layout)
        if (company_phone === "" || company_phone == null || company_phone === "null") {
            call_company.visibility = View.GONE
        }
        if (dispatcher_phone === "" || dispatcher_phone == null || dispatcher_phone === "null") {
            call_dispatcher.visibility = View.GONE
        }
        call_company.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$company_phone")
            if (ActivityCompat.checkSelfPermission(this@DashboardActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@OnClickListener
            }
            startActivity(intent)
        })
        call_dispatcher.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$dispatcher_phone")
            if (ActivityCompat.checkSelfPermission(this@DashboardActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@OnClickListener
            }
            startActivity(intent)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CALL -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    call_action()
                } else {
                    Toast.makeText(this@DashboardActivity, "You need Call Permission.", Toast.LENGTH_LONG).show()
                }
                return
            }
            FINE_LOCATION -> {
                if (grantResults.size > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGpsTrackerService() // Start Location Service for Driver Tracking Purpose
                } else {
                    val dialog = Intent(this@DashboardActivity, DialogActivity::class.java)
                    dialog.putExtra("is_location_dialog", true)
                    startActivity(dialog)
                    Toast.makeText(this@DashboardActivity, "You need Location Permission to Access User Location.", Toast.LENGTH_LONG).show()
                }
            return
            }
            REQUEST_CAMERA_PERMISSION -> {
                grantPermisions(grantResults)
                return
            }

        }
    }


    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")
        val builder = android.app.AlertDialog.Builder(ContextThemeWrapper(context,R.style.AlertDialogCustom))
        builder.setTitle("Select your document from:")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Camera") {
                val intent = Intent(this, ScansActivity::class.java)
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_CAMERA)
                startActivityForResult(intent, REQUEST_CODE)
            } else if (options[item] == "Gallery") {
                val intent = Intent(this, ScansActivity::class.java)
                intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, ScanConstants.OPEN_MEDIA)
                startActivityForResult(intent, REQUEST_CODE)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    fun grantPermisions(grantResults: IntArray){

        if(grantResults.size == 2) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                Log.e("Granted", "Granted")
//                selectImage(this@TestDashboardActivity)

                val i = Intent(this@DashboardActivity, TestCameraScreen::class.java)
                startActivity(i)

            } else {
                Toast.makeText(this@DashboardActivity, "Please provide the permission to access Camera", Toast.LENGTH_SHORT).show()
            }
        }else{
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.e("Granted", "Granted")
              //  selectImage(this@TestDashboardActivity)
                val i = Intent(this@DashboardActivity, TestCameraScreen::class.java)
                startActivity(i)

            }else {
                Toast.makeText(this@DashboardActivity, "Please provide the permission to access Camera", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun fetchPhones() {
        val tag_json_obj = "phones"
        val url = UrlController.PHONES
        showProgressBar()
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.GET,
                url, null,
                Response.Listener { response ->
                    var status: Boolean? = null
                    val data_json: JSONObject?
                    Log.e("Response ", response.toString())
                    try {
                        status = response.getBoolean("status")
                        if (status) {
                            data_json = response.getJSONObject("data")
                            if (data_json != null) {
                                val company_json = data_json.getJSONObject("company")
                                /*JSONObject dispatcher_json = data_json.getJSONObject("dispatcher");*/
                                val dispatcher_json = data_json.optJSONObject("dispatcher")
                                if (company_json != null) {
                                    company_name = company_json.getString("company_name")
                                    company_phone = company_json.getString("phone_number")
                                }
                                if (dispatcher_json != null && dispatcher_json.length() > 0) {
                                    dispatcher_name = dispatcher_json.getString("dispatcher_name")
                                    dispatcher_phone = dispatcher_json.getString("phone_number")
                                }
                            }
                            hideProgressBar()
                            call_action()
                        }
                    } catch (e: JSONException) {
                        Log.e("exception ", e.toString())
                    }
                }, Response.ErrorListener { error ->
            ErrorHandler.setVolleyMessage(this@DashboardActivity, error)
            hideProgressBar()
        }) {
            /**
             * Passing some request headers
             */
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // headers.put("Content-Type", "application/json");
                headers["Token"] = session!!._token
                return headers
            }
        }
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj)
    }

    fun cameraSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
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
                        camera = Camera.open()
                        params = camera?.getParameters()
                    } catch (e: RuntimeException) {
                        Log.e("Failed to Open. Error: ", e.message)
                    }
                }
            }
        }
    }

    private fun initIds() {
        session = SessionManager(context)
        networkValidator = NetworkValidator(context)
        captureLayout = findViewById(R.id.capture_document_layout)
        uploadLayout = findViewById(R.id.upload_layout)
        loadLayout = findViewById(R.id.load_layout)
        getCashLayout = findViewById(R.id.get_cash_layout)
        callLayout = findViewById(R.id.call_layout)
        bottom_flash_text = findViewById(R.id.flash_text)
        bottom_lights = findViewById(R.id.lights)
        lights_layout = findViewById(R.id.lights_layout)
        lights_layout.setOnClickListener(this)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun showProgressBar() {
        progressBar!!.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun hideProgressBar() {
        progressBar!!.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun hasFlash(): Boolean {
        var status = false
        if (characteristics != null) {
            Log.e("in", "" + characteristics!!.get(CameraCharacteristics.FLASH_INFO_AVAILABLE))
            status = characteristics!!.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)!!
        }
        return status
    }

    protected fun updatePreview() {
        if (is_flash_enabled) {
            if (hasFlash()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        cameraId?.let { manager!!.setTorchMode(it, true) }
                    } catch (e: CameraAccessException) {
                        Log.e("exception ", e.toString())
                    }
                }
            } else {
                Toast.makeText(this@DashboardActivity, resources.getString(R.string.no_flash_available), Toast.LENGTH_LONG).show()
            }
        } else {
            if (hasFlash()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        cameraId?.let { manager!!.setTorchMode(it, false) }
                    } catch (e: CameraAccessException) {
                        Log.e("exception ", e.toString())
                    }
                }
            }
        }
    }

    fun enableFlashlight() {
        is_flash_enabled = true
        bottom_lights!!.setImageResource(R.drawable.headlight)
        bottom_flash_text!!.text = resources.getString(R.string.lights_on)
        if (Build.VERSION.SDK_INT > 21) {
            updatePreview()
        } else {
            if (hasFlashCameraOne) {
                params = camera!!.parameters
                params?.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                camera!!.parameters = params
                //camera.startPreview();
            } else {
                Toast.makeText(this@DashboardActivity, resources.getString(R.string.no_flash_available), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun disableFlashlight() {
        is_flash_enabled = false
        bottom_lights!!.setImageResource(R.drawable.headlight_off)
        bottom_flash_text!!.text = resources.getString(R.string.lights_off)
        if (Build.VERSION.SDK_INT > 21) {
            updatePreview()
        } else {
            params = camera!!.parameters
            params?.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
            camera!!.parameters = params
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.lights_layout -> flashFunctionality()
        }
    }

    private fun flashFunctionality() {
        if (is_flash_enabled) {
            disableFlashlight()
        } else {
            enableFlashlight()
        }
    }

//    @Bind(R.id.doc_count_header)
//    var expiredDocHeader: RelativeLayout? = null

//    @Bind(R.id.doc_count_text)
//    var expiredDocText: TextView? = null

//    @Bind(R.id.driver_name_text)
//    var driverNameText: TextView? = null
//
//    @Bind(R.id.company_name_text)
//    var companyNameText: TextView? = null

    @OnClick(R.id.doc_count_header)
    fun goToExpiredDoc() {
        val i = Intent(context, ExpiredDocumentActivity::class.java)
        context.startActivity(i)
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123
        private const val CALL = 0x2
        const val FINE_LOCATION = 0x3
        const val COARSE_LOCATION = 0x4
        const val WAKE_LOCK = 0x5
    }
}