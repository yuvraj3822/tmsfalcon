package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import butterknife.ButterKnife
import butterknife.OnClick
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.gms.location.LocationServices
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.activities.ReportedAccidentScreenFive
import com.tmsfalcon.device.tmsfalcon.customtools.AppController.*
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.BasicFormAccident
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.OnSearchItemSelected
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.SearchListItem
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.SearchableDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.accident_details_basic_form_data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AccidentBasicFormDataK : NavigationBaseActivity(), OnSearchItemSelected {
    var incidentDateListener: DatePickerDialog.OnDateSetListener? = null
    var incidentTimeListener: TimePickerDialog.OnTimeSetListener? = null
    var sessionManager: SessionManager? = null
    var isFormValid = false
    var customValidator: CustomValidator? = null
    var accident_types_arrayList: List<String> = ArrayList()
    var accident_types_list: ArrayList<SearchListItem> = ArrayList()
    var networkValidator: NetworkValidator? = null
    var accident_type_spinner: Spinner? = null

    var networkValidators: NetworkValidator? = null
    lateinit var searchableDialog: SearchableDialog
    lateinit var db: AccidentModuleDb
    val TAG = this@AccidentBasicFormDataK::class.simpleName
    var RESPONSE_FROM_SECOND_ACTIVITY = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.accident_details_basic_form_data, null, false)

        drawer.addView(contentView, 0)
        ButterKnife.bind(this)

        initIds()
        zoom_linear_layout!!.init(this@AccidentBasicFormDataK)

        accident_types_arrayList = Arrays.asList(*resources.getStringArray(R.array.accident_types))
        setListeners()
        val date_n = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        Log.e("current date", date_n)

        accident_date!!.setText(date_n)
        val sdf = SimpleDateFormat("HH:mm")
        val currentTime = sdf.format(Date())

        accident_time!!.setText(currentTime)
        Log.e("currentTime date", currentTime)
        if (sessionManager!!.keyCurrentLatitude == null || sessionManager!!.keyCurrentLatitude === "") {
            lastLocation
        }

     // initliseSpinnerAndListener()
        selectAccident()
        createListOfAccidentType()
        fetchData()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESPONSE_FROM_SECOND_ACTIVITY){
            if (resultCode == Activity.RESULT_OK){
                val returnIntent = Intent()
                returnIntent.putExtra("result", "result")
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }


//            if (resultCode == Activity.RESULT_CANCELED){
//            }

        }

    }

    fun fetchData() {

        CoroutineScope(IO).launch {
            var bfa = db.bfaDao().getBasicFormData()
            Log.e("data", "setup: " + bfa)
            if (bfa != null) {
                Log.e(TAG, ": updateUI ")
                updateUI(bfa)

            } else {
                Log.e(TAG, ": driverinfo ")
                requestDriverInfo()
            }
        }


    }

    //    var description:String = ""
    suspend fun updateUI(bfa: BasicFormAccident) {

        CoroutineScope(Main).launch {
            if (bfa.incidentType == ACCIDENT) accident_radio_button.isChecked = true
            else incident_radio_button.isChecked = true

            accident_date.setText(bfa.date)
            accident_time.setText(bfa.time)
            accident_description.setText(bfa.description)
            accident_type.text = bfa.type
            is_private_property_checkbox.isChecked = bfa.isPrivateProperty
//            has_employer_checkbox.isChecked = bfa.isDrivingForEmployer
            driver_employer_name.setText(bfa.driverName)
            driver_employer_phone_no.setText(bfa.driverPhoneNo)
            owner_city.setText(bfa.driverCity)
            owner_state.setText(bfa.driverState)
            owner_zipcode.setText(bfa.driverZipCode)
            owner_address.setText(bfa.driverAddress)
//            driver_employer_insurance_provider.setText(bfa.employerInsuranceProvider)
//            driver_employer_insurance_policy_number.setText(bfa.employerInsurancePolicyNo)
            licenseNo = bfa.licenseNo
            dobDriver = bfa.dob
            telephoneNo = bfa.telephoneNo
            year_make = bfa.vehicleYearMake
            licensePlateIdNo = bfa.licensePlateVehicleIdNo

        }


    }


//    fun clearEmployerFields() {
//
//        driver_employer_name.setText("")
//        driver_employer_phone_no.setText("")
//        driver_employer_insurance_provider.setText("")
//        driver_employer_insurance_policy_number.setText("")
//    }

    fun addToDb(whetherToMoveNextScreen: Boolean) {
//        if (!has_employer_checkbox.isChecked) clearEmployerFields()



        CoroutineScope(IO).launch {
            Log.e("inserted", "initialise")

            var bfa = BasicFormAccident(1, strIncidentType, accident_date.text.toString(),
                    accident_time.text.toString(), accident_description.text.toString(),
                    accident_type.text.toString(), is_private_property_checkbox.isChecked,
//                    has_employer_checkbox.isChecked,
                    driver_employer_name.text.toString(),
                    driver_employer_phone_no.text.toString(),
                    owner_address.text.toString(),
                    owner_city.text.toString(),
                    owner_state.text.toString(),
                    owner_zipcode.text.toString(),

//                    driver_employer_insurance_provider.text.toString(),
//                    driver_employer_insurance_policy_number.text.toString(),
                    sessionManager!!.keyCurrentLatitude ?: "",
                    sessionManager!!.keyCurrentLongitude ?: "",
                    licenseNo,
                    dobDriver,
                    telephoneNo,
                    year_make,
                    licensePlateIdNo)


            try {
                var result = db.bfaDao().insert(bfa)
                navigateToScreen(result, whetherToMoveNextScreen)
            } catch (e: Exception) {
                Log.e("insertionException: ", "$e")
                Toast.makeText(this@AccidentBasicFormDataK, "Something went wrong while saving data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun navigateToScreen(result: Long, whetherToMoveNextScreen: Boolean) {
        when (whetherToMoveNextScreen) {
            true -> {
                val i = Intent(this@AccidentBasicFormDataK,KotAccidentVehicleDetails::class.java)
                startActivityForResult(i, RESPONSE_FROM_SECOND_ACTIVITY)
//                startActivity(i)

            }
            false -> {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        AlertDialog.Builder(this@AccidentBasicFormDataK, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Attention")
                .setMessage("Do you want to save the progress?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        // Continue with delete operation
                        dialog!!.dismiss()
                        addToDb(false)

                    }
                }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                        finish()
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

    }


    fun requestDriverInfo() {
        checkInternet()
    }

    fun createListOfAccidentType() {
        for ((index, value) in accident_types_arrayList.withIndex()) {
            accident_types_list.add(SearchListItem(index, value))
        }
        Log.e("size", ": " + accident_types_list.size)

    }

    fun selectAccident() {
        event_type_radio_button.setOnCheckedChangeListener { radioGroup, i ->
            if (R.id.accident_radio_button == radioGroup.checkedRadioButtonId) {
                Log.e("selected", "accident")
                strIncidentType = ACCIDENT
                type_view.visibility = View.VISIBLE
            } else {
                Log.e("selected", "incident")
                strIncidentType = INCIDENT
                type_view.visibility = View.GONE
                accident_type.setText(R.string.type)
            }
        }
    }


    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()// GPS location can be null if GPS is switched off// TODO: Consider calling
    //    ActivityCompat#requestPermissions
    // here to request the missing permissions, and then overriding
    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
    //                                          int[] grantResults)
    // to handle the case where the user grants the permission. See the documentation
    // for ActivityCompat#requestPermissions for more details.

    // Get last known recent location using new Google Play Services SDK (v11+)
    val lastLocation: Unit
        get() {
            // Get last known recent location using new Google Play Services SDK (v11+)
            val locationClient = LocationServices.getFusedLocationProviderClient(this)
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
            locationClient.lastLocation
                    .addOnSuccessListener { location ->
                        // GPS location can be null if GPS is switched off
                        Log.e("MapDemoActivity", "in location $location")
                        if (location != null) {
                            sessionManager!!.storeCurrentLocation(java.lang.Double.toString(location.latitude), java.lang.Double.toString(location.longitude))
                            val gcd = Geocoder(baseContext,
                                    Locale.getDefault())
                            val addresses: List<Address>
                            try {
                                addresses = gcd.getFromLocation(location.latitude,
                                        location.longitude, 1)
                                if (addresses.size > 0) {
                                    val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    val locality = addresses[0].locality
                                    val subLocality = addresses[0].subLocality
                                    val state = addresses[0].adminArea
                                    val country = addresses[0].countryName
                                    val postalCode = addresses[0].postalCode
                                    val knownName = addresses[0].featureName
                                    sessionManager!!.storeCurrentAddress(state, locality)
                                    sessionManager!!.storeCurrentStreetAddress(address)
//                                    sessionManager.keyCurrentLatitude = location.latitude as String
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("MapDemoActivity", "Error trying to get last GPS location")
                        e.printStackTrace()
                    }
        }


    fun initIds() {
        sessionManager = SessionManager()
        customValidator = CustomValidator(this@AccidentBasicFormDataK)
        networkValidators = NetworkValidator(this@AccidentBasicFormDataK)
//        accident_type_spinner = findViewById(R.id.accident_type_spinner)

        db = AccidentModuleDb.getDatabase(this@AccidentBasicFormDataK)
    }


    lateinit var prLocation: Location


    private fun setListeners() {

        searchableDialog = SearchableDialog(this, accident_types_list, "Type")
        searchableDialog.setOnItemSelected(this)
        accident_type.setOnClickListener {
            searchableDialog.show()
        }

        incidentDateListener = DatePickerDialog.OnDateSetListener { datePickerDialog, year, monthOfYear, dayOfMonth ->
            val date = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
            accident_date!!.setText(date)
        }



        incidentTimeListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

            val startTime = String.format("%02d:%02d", hourOfDay, minute)
            //String startTime = hourOfDay+":"+minute;
            accident_time!!.setText(startTime)
        }

        is_private_property_checkbox.setOnCheckedChangeListener { compoundButton, b ->

        }
//        has_employer_checkbox.setOnCheckedChangeListener { compoundButton, b ->
//            if (b) {
//                driver_employer_block!!.visibility = View.VISIBLE
//                scroll_view!!.post {
//                    scroll_view!!.smoothScrollTo(0, scroll_view!!.bottom)
//                }
//            } else {
//                driver_employer_block!!.visibility = View.GONE
//            }
//        }

        Log.e("test", ": " + accident_description.text)

        accident_date!!.addTextChangedListener(GenericTextWatcher(accident_date!!))
        accident_time!!.addTextChangedListener(GenericTextWatcher(accident_time!!))
        driver_employer_name!!.addTextChangedListener(GenericTextWatcher(driver_employer_name!!))



//        val formatter = MaskedFormatter("(###)###-####")
//        driver_employer_phone_no.addTextChangedListener(MaskedWatcher(formatter, driver_employer_phone_no))
//        driver_employer_phone_no!!.addTextChangedListener(GenericTextWatcher(driver_employer_phone_no!!))

//        var strPhone:String = ""
//
//        val phoneFormat = PhoneFormat("us",this@AccidentBasicFormDataK)
//
//
//        driver_employer_phone_no.addTextChangedListener(PhoneNumberFormattingTextWatcher("us"))
//
//        driver_employer_phone_no!!.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(p0: Editable?) {
//                if (p0!!.length ==10){
//
//                }
//
//                Log.e("afterTextChanged: ",phoneFormat.format(p0.toString()))
////                driver_employer_phone_no.setText(phoneFormat.format(p0.toString()))
//
//            }
//
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                    Log.e("onTextChanged: ",phoneFormat.format(p0.toString()))
//            }
//
//        })
    }


    fun validateForm(): Boolean {
        if (!customValidator!!.setRequired(accident_date!!.text.toString())) {
            error_accident_date!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_accident_date!!.visibility = View.GONE
            isFormValid = true
        }
        if (!customValidator!!.setRequired(accident_time!!.text.toString())) {
            error_accident_time!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_accident_time!!.visibility = View.GONE
            isFormValid = true
        }
        if (!customValidator!!.setRequired(driver_employer_name!!.text.toString())) {
            error_employer_name!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_employer_name!!.visibility = View.GONE
            isFormValid = true
        }
        if (!customValidator!!.setRequired(driver_employer_phone_no!!.text.toString())) {
            error_employer_phone!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_employer_phone!!.visibility = View.GONE
            isFormValid = true
        }
        return isFormValid
    }


    private fun saveToDb() {
        if (validationABFD()) {
            addToDb(true)
        }
    }


    /**
     * validation on Screen Accident BasicFormData
     */
    fun validationABFD(): Boolean {
        if (accident_date.text.toString() == "") {
            Toast.makeText(this@AccidentBasicFormDataK, "Please mention the date of the $strIncidentType", Toast.LENGTH_SHORT).show()
            return false
        } else if (accident_time.text.toString() == "") {
            Toast.makeText(this@AccidentBasicFormDataK, "Please mention the time of the $strIncidentType", Toast.LENGTH_SHORT).show()
            return false
        } else if (accident_description.text.toString() == "") {
            Toast.makeText(this@AccidentBasicFormDataK, "Please provide the brief description about what happened", Toast.LENGTH_SHORT).show()
            return false
        }
        if (driver_employer_name.text.toString() == "") {
            Toast.makeText(this@AccidentBasicFormDataK, "Please mention the name of the Employer", Toast.LENGTH_SHORT).show()
            return false
        } else if (driver_employer_phone_no.text.toString() == "") {
            Toast.makeText(this@AccidentBasicFormDataK, "Please mention the Phone no of the Employer", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (strIncidentType.equals(ACCIDENT,ignoreCase = true)){
            var chekcType = false
            if (accident_type.text.toString() == "Type") {
                Toast.makeText(this@AccidentBasicFormDataK, "Please provide the type of the $strIncidentType", Toast.LENGTH_SHORT).show()
                chekcType= false
                return chekcType
            }  else {
                chekcType = true
                return chekcType
            }
        return chekcType
        }

//        else if (has_employer_checkbox.isChecked) {
//
//        } else {
//            return true
//        }




        return true

    }


    /**
     * setDriverDetails
     */

    fun parseDriverJsonObj(obj: JSONObject){
        if(obj!=null){
            var firstName = obj.getString("first_name")
            var lastName = obj.getString("last_name")
            var zipCode = obj.getString("zip_code")
            var state = obj.getString("state")
            var city = obj.getString("city")
            var cell = obj.getString("cell")
            var address = obj.getString("address")



            Log.e("firstname",": "+firstName)
            driver_employer_name.setText(firstName+ " "+ lastName)
            driver_employer_phone_no.setText(cell)
            owner_city.setText(city)
            owner_state.setText(state)
            owner_zipcode.setText(zipCode)
            owner_address.setText(address)
        }else {
            Log.e("driverObj: ","null")
        }


    }


    private fun saveDataToDb() {
        // validateForm();


        if (isFormValid) {
            /* AccidentBasicDetailsModel accidentBasicDetailsModel = new AccidentBasicDetailsModel();
            accidentBasicDetailsModel.setDriver_id(sessionManager.get_driver_id());
            accidentBasicDetailsModel.setAccident_date(accident_date.getText().toString());
            accidentBasicDetailsModel.setAccident_lat(sessionManager.getKeyCurrentLatitude());
            accidentBasicDetailsModel.setAccident_location(sessionManager.getKeyCurrentStreetAddress());
            accidentBasicDetailsModel.setAccident_long(sessionManager.getKeyCurrentLongitude());
            accidentBasicDetailsModel.setAccident_time(accident_time.getText().toString());
            accidentBasicDetailsModel.setEmployer_name(driver_employer_name.getText().toString());
            accidentBasicDetailsModel.setEmployer_phone_number(driver_employer_phone_no.getText().toString());
            accidentBasicDetailsModel.setEmployer_insurance_provider(driver_employer_insurance_provider.getText().toString());
            accidentBasicDetailsModel.setEmployer_insurance_policy_number(driver_employer_insurance_policy_number.getText().toString());

            long result = db.saveAccidentBasicDetails(accidentBasicDetailsModel);

            if(result != -1) {
                int accident_report_id = (int) result;
                AppController.accident_report_id = accident_report_id;

                for (AccidentVehicleDetailsModel accidentVehicleDetailsModel : vehicleDetailArrayList) {
                    accidentVehicleDetailsModel.setAccident_report_id(AppController.accident_report_id);
                    db.saveVehicleDetails(accidentVehicleDetailsModel);
                }
            }*/
            val i = Intent(this@AccidentBasicFormDataK, ReportedAccidentScreenFive::class.java)
            startActivity(i)
        }

        val i = Intent(this@AccidentBasicFormDataK, KotAccidentVehicleDetails::class.java)
        startActivity(i)

    }

    lateinit var year: String
    lateinit var make: String
    var year_make: String = ""
    var driverAddress: String = ""
    var licenseNo: String = ""
    var dobDriver: String = ""
    var telephoneNo: String = ""
    var licensePlateIdNo: String = ""

    fun checkInternet() {
        if (networkValidators!!.isNetworkConnected) {
            progress_bar.setVisibility(View.VISIBLE)
            AndroidNetworking.get(UrlController.DRIVER_INFO)
                    .addHeaders("Token", sessionManager!!._token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            progress_bar.setVisibility(View.GONE)
                            Log.e("response", " is $response")
                            var status = false
                            var data_json: JSONObject? = null
                            try {
                                status = response.getBoolean("status")
                                data_json = response.getJSONObject("data")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            if (status) {
                                try {
                                    data_json = response.getJSONObject("data")
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                                if (data_json != null && data_json.length() > 0) {
                                    try {
                                        val truck_json_obj = data_json.getJSONObject("driver_truck_info")
                                        val driver_json_object = data_json.getJSONObject("driver_info")
                                        val trailer_json_array = data_json.getJSONArray("driver_trailer_info")
                                        if (truck_json_obj != null && truck_json_obj.length() > 0) {
                                            //    vehicle_json_array.put(truck_json_obj)
                                            if (truck_json_obj.get("year") != null) {
                                                year = truck_json_obj.get("year") as String
                                                Log.e("year", ": " + year)
                                            } else {
                                                year = ""
                                            }
                                            if (truck_json_obj.get("make") != null) {
                                                make = truck_json_obj.get("make") as String
                                            } else {
                                                make = ""
                                            }
                                            year_make = year + make
                                            if (truck_json_obj.get("license_plate_number") != null) {
                                                licensePlateIdNo = truck_json_obj.get("license_plate_number") as String
                                            }

                                            //       lskjlds
                                        }
                                        if (driver_json_object != null && driver_json_object.length() > 0) {

                                            Log.e("parse","parse")
                                            parseDriverJsonObj(driver_json_object)

                                            driverAddress = driver_json_object.get("city") as String?
                                                    ?: "" + driver_json_object.get("state") ?: ""

                                            if (driver_json_object.get("dl") != null) {
                                                licenseNo = driver_json_object.get("dl") as String
                                            }
                                            if (driver_json_object.get("dob") != null) {
                                                dobDriver = driver_json_object.get("dob") as String
                                            }
                                            if (driver_json_object.get("cell") != null) {
                                                telephoneNo = driver_json_object.get("cell") as String
                                            }

                                        }
                                        if (trailer_json_array != null && trailer_json_array.length() > 0) {
                                            for (i in 0 until trailer_json_array.length()) {
                                                val jEmpObj = trailer_json_array.getJSONObject(i)
                                                //    vehicle_json_array.put(jEmpObj)
                                            }
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                            }

                        }

                        override fun onError(error: ANError) {
                            progress_bar.setVisibility(View.GONE)
                            Log.e("error", " is $error")
                            // handle error
                        }
                    })
        } else {
            Toast.makeText(this@AccidentBasicFormDataK, resources.getString(R.string.internet_not_connected), Toast.LENGTH_LONG).show()
        }
    }




    @OnClick(R.id.accident_time)
    fun setincidentTime() {
        val time = Calendar.getInstance()
        val minute = time[Calendar.MINUTE]
        //12 hour format
        val hour = time[Calendar.HOUR_OF_DAY]
        Log.e("hour", "" + hour)
        Log.e("minute", "" + minute)
        val tpd = TimePickerDialog.newInstance(incidentTimeListener, Calendar.HOUR_OF_DAY, Calendar.MINUTE, true)
        tpd.setStartTime(hour, minute)

        tpd.show(this@AccidentBasicFormDataK.fragmentManager, "TimePickerDialog")
    }

//    @OnCheckedChanged(R.id.has_employer_checkbox)
//    fun onRadioButtonCheckChanged(button: CompoundButton?, checked: Boolean) {
//        if (checked) {
//            driver_employer_block!!.visibility = View.VISIBLE
//            scroll_view!!.post { scroll_view!!.smoothScrollTo(0, scroll_view!!.bottom) }
//        } else {
//            driver_employer_block!!.visibility = View.GONE
//        }
//    }

    @OnClick(R.id.next_btn)
    fun goToNext() {
        saveToDb()
    }

    @OnClick(R.id.accident_date)
    fun setPickUpDatePicker() {
        val now = Calendar.getInstance()


        val dpd = DatePickerDialog.newInstance(
                incidentDateListener,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
        )

        now.add(Calendar.DATE,-120)

        dpd.minDate = now
//        dpd.setMinDate(System.currentTimeMillis() - 1000);
//        var cal = Calendar.getInstance()
//        cal.set(10368000000.toInt(),1000)
//        dpd.minDate = cal

        dpd.show(this@AccidentBasicFormDataK.fragmentManager, "DatePickerDialog")
    }

    private inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.accident_date -> {
                    val str1 = accident_date!!.text.toString()
                    if (str1 !== "" && str1.length > 0) {
                        error_accident_date!!.visibility = View.GONE
                    } else {
                        error_accident_date!!.visibility = View.VISIBLE
                    }
                }
                R.id.accident_time -> {
                    val str2 = accident_time!!.text.toString()
                    if (str2 !== "" && str2.length > 0) {
                        error_accident_time!!.visibility = View.GONE
                    } else {
                        error_accident_time!!.visibility = View.VISIBLE
                    }
                }
                R.id.driver_employer_name -> {
                    val str3 = driver_employer_name!!.text.toString()
                    if (str3 !== "" && str3.length > 0) {
                        error_employer_name!!.visibility = View.GONE
                    } else {
                        error_employer_name!!.visibility = View.VISIBLE
                    }
                }
                R.id.driver_employer_phone_no -> {
                    val str4 = driver_employer_phone_no!!.text.toString()
                    if (str4 !== "" && str4.length > 0) {
                        error_employer_phone!!.visibility = View.GONE
                    } else {
                        error_employer_phone!!.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onClick(position: Int, searchListItem: SearchListItem) {
        Log.e("click", "position: " + searchListItem.title)
        accident_type.text = searchListItem.title
        hideKeyboardFrom(this@AccidentBasicFormDataK, accident_type)
        searchableDialog.dismiss()
    }

    fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}