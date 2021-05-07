package com.tmsfalcon.device.tmsfalcon.activities

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import butterknife.ButterKnife
import butterknife.OnCheckedChanged
import butterknife.OnClick
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotOtherPartyVehicleDetails
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.OtherFormAccident
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.OnSearchItemSelected
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.SearchListItem
import com.tmsfalcon.device.tmsfalcon.widgets.searchDialogCustom.SearchableDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_other_party_accident_details.*
import kotlinx.android.synthetic.main.activity_other_party_accident_details.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OtherPartyAccidentDetails : NavigationBaseActivity() ,OnSearchItemSelected
         {
    var incidentDateListener: DatePickerDialog.OnDateSetListener? = null
    var incidentTimeListener: TimePickerDialog.OnTimeSetListener? = null
    var sessionManager: SessionManager? = null
    var isFormValid = false
    var customValidator: CustomValidator? = null
    var accident_types_arrayList: List<String> = ArrayList()

    var strIncidentType:String = "Accident/Incident"
    var accident_types_list: ArrayList<SearchListItem> = ArrayList()

    lateinit var searchableDialog: SearchableDialog

    lateinit var year:String
    lateinit var make:String
    var year_make:String = ""
    var driverAddress:String = ""
    var licenseNo:String = ""
    var dobDriver:String = ""
    var telephoneNo:String = ""
    var licensePlateIdNo:String = ""

    lateinit var db: AccidentModuleDb

    val TAG = this@OtherPartyAccidentDetails::class.simpleName

    var NEXT_SCREEN = 2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_other_party_accident_details, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this)
        initIds()
        zoom_linear_layout!!.init(this@OtherPartyAccidentDetails)
        accident_types_arrayList = Arrays.asList(*resources.getStringArray(R.array.accident_types))
        setListeners()
        val date_n = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
        Log.e("current date", date_n)
        accident_date!!.setText(date_n)
        val sdf = SimpleDateFormat("HH:mm")
        val currentTime = sdf.format(Date())
        accident_time!!.setText(currentTime)
        Log.e("currentTime date", currentTime)

        createListOfAccidentType()
        driver_phoneno_other.setText("")
        fetchData()

        /*if(sessionManager.getKeyCurrentLatitude() == null ||sessionManager.getKeyCurrentLatitude() == ""){
            getLastLocation();
        }*/
    }

    override fun onBackPressed() {
        // super.onBackPressed()

        AlertDialog.Builder(this@OtherPartyAccidentDetails,R.style.Theme_AppCompat_Dialog_Alert)
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
                .setNegativeButton(android.R.string.no, object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                        finish()
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

    }





    fun fetchData(){

        CoroutineScope(IO).launch {
            var ofa = db.ofaDao().getBasicFormData()
            Log.e("data","setup: "+ofa)
            if(ofa != null) {
                Log.e(TAG, ": updateUI " )
                updateUI(ofa)
            }
        }
    }


    fun updateUI(bfa:OtherFormAccident){
        CoroutineScope(Main).launch {

            accident_date.setText(bfa.date)
            accident_time.setText(bfa.time)
            accident_description.setText(bfa.description)
            accident_type.text = bfa.type
            is_private_property_checkbox.isChecked = bfa.isPrivateProperty
            has_employer_checkbox.isChecked = bfa.isDrivingForEmployer
            driver_name_other.setText(bfa.driverName)
            driver_phoneno_other.setText(bfa.driverPhoneNo)
            driver_address_other.setText(bfa.driverAddress)
            driver_city_other.setText(bfa.driverCity)
            driver_state_other.setText(bfa.driverState)
            driver_zipcode_other.setText(bfa.driverZipCode)

            driverAddress = bfa.driverAddress
            licenseNo = bfa.licenseNo
            dobDriver = bfa.dob
            telephoneNo = bfa.telephoneNo
            year_make = bfa.vehicleYearMake
            licensePlateIdNo = bfa.licensePlateVehicleIdNo

        }


    }



    private fun saveToDb(){
        if (validationABFD()){
            addToDb(true)
        }
    }

    fun clearEmployerFields(){

        driver_name_other.setText("")
        driver_phoneno_other.setText("")
        driver_address_other.setText("")
        driver_city_other.setText("")
        driver_state_other.setText("")
        driver_zipcode_other.setText("")

//        driver_employer_insurance_provider.setText("")
//        driver_employer_insurance_policy_number.setText("")

    }


    fun navigateToScreen(result:Long,whetherToMoveNextScreen:Boolean){
        when(whetherToMoveNextScreen){
            true -> {
                val i = Intent(this@OtherPartyAccidentDetails, KotOtherPartyVehicleDetails::class.java)
                startActivityForResult(i,NEXT_SCREEN)
            }
            false -> {
                finish()
            }
        }
    }


     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         val returnIntent = Intent()
         returnIntent.putExtra("result", "result")
         setResult(Activity.RESULT_OK, returnIntent)
         finish()

     }

             fun addToDb(whetherToMoveNextScreen:Boolean){
//        if (!has_employer_checkbox.isChecked) clearEmployerFields()

            Log.e("inserted","initialise")

            var ofa =   OtherFormAccident(1,strIncidentType,
                    accident_date.text.toString(),
                    accident_time.text.toString(),
                    accident_description.text.toString(),
                    accident_type.text.toString(),
                    is_private_property_checkbox.isChecked,
                    has_employer_checkbox.isChecked,
                    driver_name_other.text.toString(),
                    driver_phoneno_other.text.toString(),
                    driver_address_other.text.toString(),
                    driver_city_other.text.toString(),
                    driver_state_other.text.toString(),
                    driver_zipcode_other.text.toString(),
                    driver_employer_insurance_provider.text.toString(),
                    driver_employer_insurance_policy_number.text.toString(),
                    sessionManager!!.keyCurrentLatitude ?: "",
                    sessionManager!!.keyCurrentLongitude ?: "",
                    licenseNo,
                    dobDriver,
                    telephoneNo,
                    year_make,
                    licensePlateIdNo)
                    CoroutineScope(IO).launch {
                        try {
                            var result= db.ofaDao().insert(ofa)
                            navigateToScreen(result,whetherToMoveNextScreen)
                        }catch (e:Exception){
                            Log.e("insertionException: ","$e")
                            Toast.makeText(this@OtherPartyAccidentDetails, "Something went wrong while saving data", Toast.LENGTH_SHORT).show()
                        }
                    }
    }


    fun validationABFD():Boolean{
        if (accident_date.text.toString() == ""){
            Toast.makeText(this@OtherPartyAccidentDetails, "Please mention the date of the $strIncidentType", Toast.LENGTH_SHORT).show()
            return false
        }else if (accident_time.text.toString() == ""){
            Toast.makeText(this@OtherPartyAccidentDetails, "Please mention the time of the $strIncidentType", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (accident_type.text.toString().equals("Type",ignoreCase = true)|| accident_type.text.toString().trim().isEmpty()){
            Toast.makeText(this@OtherPartyAccidentDetails, "Please provide the type of the $strIncidentType", Toast.LENGTH_SHORT).show()
            return false
        }
//        else if (has_employer_checkbox.isChecked){
            if (driver_name_other.text.toString() == ""){
                Toast.makeText(this@OtherPartyAccidentDetails, "Please mention the name of the Employer", Toast.LENGTH_SHORT).show()
                return false
            }else if (driver_phoneno_other.text.toString() == ""){
                Toast.makeText(this@OtherPartyAccidentDetails, "Please mention the Phone no of the Employer", Toast.LENGTH_SHORT).show()
                return false
            }


            return true
//        }else {
//            return true
//        }
    }


    fun initIds() {
        sessionManager = SessionManager()
        customValidator = CustomValidator(this@OtherPartyAccidentDetails)
        db = AccidentModuleDb.getDatabase(this@OtherPartyAccidentDetails)

    }

    fun createListOfAccidentType(){
        for ((index,value) in accident_types_arrayList.withIndex()){
            accident_types_list.add(SearchListItem(index,value))
        }
        Log.e("size",": "+accident_types_list.size)

    }


    private fun setListeners() {
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, accident_types_arrayList)
//        accident_type!!.threshold = 2
//        accident_type!!.setAdapter(adapter)

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

        has_employer_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                driver_employer_block!!.visibility = View.VISIBLE
                scroll_view!!.post {
                    scroll_view!!.smoothScrollTo(0, scroll_view!!.bottom)
                }
            } else {
                driver_employer_block!!.visibility = View.GONE
            }
        }

        Log.e("test",": "+accident_description.text)

        accident_date!!.addTextChangedListener(GenericTextWatcher(accident_date!!))
        accident_time!!.addTextChangedListener(GenericTextWatcher(accident_time!!))
        driver_name_other!!.addTextChangedListener(GenericTextWatcher(driver_name_other!!))
        driver_phoneno_other!!.addTextChangedListener(GenericTextWatcher(driver_phoneno_other!!))

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
        if (!customValidator!!.setRequired(driver_name_other!!.text.toString())) {
            error_employer_name!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_employer_name!!.visibility = View.GONE
            isFormValid = true
        }
        if (!customValidator!!.setRequired(driver_phoneno_other!!.text.toString())) {
            error_employer_phone!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_employer_phone!!.visibility = View.GONE
            isFormValid = true
        }
        return isFormValid
    }

//    private fun saveDataToDb() {
//        // validateForm();
//        if (isFormValid) {
//            /* AccidentBasicDetailsModel accidentBasicDetailsModel = new AccidentBasicDetailsModel();
//            accidentBasicDetailsModel.setDriver_id(sessionManager.get_driver_id());
//            accidentBasicDetailsModel.setAccident_date(accident_date.getText().toString());
//            accidentBasicDetailsModel.setAccident_lat(sessionManager.getKeyCurrentLatitude());
//            accidentBasicDetailsModel.setAccident_location(sessionManager.getKeyCurrentStreetAddress());
//            accidentBasicDetailsModel.setAccident_long(sessionManager.getKeyCurrentLongitude());
//            accidentBasicDetailsModel.setAccident_time(accident_time.getText().toString());
//            accidentBasicDetailsModel.setEmployer_name(driver_employer_name.getText().toString());
//            accidentBasicDetailsModel.setEmployer_phone_number(driver_employer_phone_no.getText().toString());
//            accidentBasicDetailsModel.setEmployer_insurance_provider(driver_employer_insurance_provider.getText().toString());
//            accidentBasicDetailsModel.setEmployer_insurance_policy_number(driver_employer_insurance_policy_number.getText().toString());
//
//            long result = db.saveAccidentBasicDetails(accidentBasicDetailsModel);
//
//            if(result != -1) {
//                int accident_report_id = (int) result;
//                AppController.accident_report_id = accident_report_id;
//
//                for (AccidentVehicleDetailsModel accidentVehicleDetailsModel : vehicleDetailArrayList) {
//                    accidentVehicleDetailsModel.setAccident_report_id(AppController.accident_report_id);
//                    db.saveVehicleDetails(accidentVehicleDetailsModel);
//                }
//            }*/
//            //Intent i = new Intent(OtherPartyAccidentDetails.this, ReportedAccidentScreenFive.class);
//            //startActivity(i);
//        }
//        val i = Intent(this@OtherPartyAccidentDetails, OtherPartyVehicleDetails::class.java)
//        startActivity(i)
//    }

//    @JvmField
//    @Bind(R.id.accident_date)
//    var accident_date: EditText? = null

//    @JvmField
//    @Bind(R.id.accident_time)
//    var accident_time: EditText? = null

//    @JvmField
//    @Bind(R.id.accident_description)
//    var accident_description: EditText? = null

//    @JvmField
//    @Bind(R.id.accident_type)
//    var accident_type: AutoCompleteTextView? = null

//    @JvmField
//    @Bind(R.id.is_private_property_checkbox)
//    var is_private_property_checkbox: AppCompatCheckBox? = null

//    @JvmField
//    @Bind(R.id.has_employer_checkbox)
//    var has_employer_checkbox: AppCompatCheckBox? = null
//
//    @JvmField
//    @Bind(R.id.driver_employer_block)
//    var driver_employer_block: LinearLayout? = null
//
//    @JvmField
//    @Bind(R.id.driver_employer_name)
//    var driver_employer_name: EditText? = null

//    @JvmField
//    @Bind(R.id.driver_employer_phone_no)
//    var driver_employer_phone_no: EditText? = null

//    @JvmField
//    @Bind(R.id.driver_employer_insurance_provider)
//    var driver_employer_insurance_provider: EditText? = null

//    @JvmField
//    @Bind(R.id.driver_employer_insurance_policy_number)
//    var driver_employer_insurance_policy_number: EditText? = null

//    @JvmField
//    @Bind(R.id.error_accident_date)
//    var error_accident_date: TextView? = null

//    @JvmField
//    @Bind(R.id.error_accident_time)
//    var error_accident_time: TextView? = null

//    @JvmField
//    @Bind(R.id.error_employer_name)
//    var error_employer_name: TextView? = null

//    @JvmField
//    @Bind(R.id.error_employer_phone)
//    var error_employer_phone: TextView? = null

//    @JvmField
//    @Bind(R.id.scroll_view)
//    var scroll_view: ScrollView? = null

//    @JvmField
//    @Bind(R.id.zoom_linear_layout)
//    var zoom_linear_layout: ZoomLinearLayout? = null

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
        tpd.show(this@OtherPartyAccidentDetails.fragmentManager, "TimePickerDialog")
    }

    @OnCheckedChanged(R.id.has_employer_checkbox)
    fun onRadioButtonCheckChanged(button: CompoundButton?, checked: Boolean) {
//        if (checked) {
//            driver_employer_block!!.visibility = View.VISIBLE
//            scroll_view!!.post { scroll_view!!.smoothScrollTo(0, scroll_view!!.bottom) }
//        } else {
//            driver_employer_block!!.visibility = View.GONE
//        }
    }

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
        dpd.show(this@OtherPartyAccidentDetails.fragmentManager, "DatePickerDialog")
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
                    val str3 = driver_name_other!!.text.toString()
                    if (str3 !== "" && str3.length > 0) {
                        error_employer_name!!.visibility = View.GONE
                    } else {
                        error_employer_name!!.visibility = View.VISIBLE
                    }
                }
                R.id.driver_employer_phone_no -> {
                    val str4 = driver_phoneno_other!!.text.toString()
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
        Log.e("click","position: "+searchListItem.title)
        accident_type.text = searchListItem.title
        hideKeyboardFrom(this@OtherPartyAccidentDetails,accident_type)
        searchableDialog.dismiss()
    }
    fun hideKeyboardFrom(context: Context, view: View) {
        val imm: InputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}