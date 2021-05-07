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
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity
import com.tmsfalcon.device.tmsfalcon.activities.DialogActivity
import com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters.KotAccidentDamageListAdapter
import com.tmsfalcon.device.tmsfalcon.customtools.*
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.DamageDetails
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.WitnessFormAccident
import com.tmsfalcon.device.tmsfalcon.interfacess.clickDamage
import com.tmsfalcon.device.tmsfalcon.interfacess.deleteDamageDetails
import kotlinx.android.synthetic.main.activity_accident_damage_details.*
import kotlinx.android.synthetic.main.activity_accident_damage_details.message
import kotlinx.android.synthetic.main.activity_accident_damage_details.next_btn
import kotlinx.android.synthetic.main.activity_accident_vehicle_details.*
import kotlinx.android.synthetic.main.dialog_accident_damage_detail.*
import kotlinx.android.synthetic.main.dialog_accident_damage_detail.view.*
import kotlinx.android.synthetic.main.list_item_emi.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class KotAccidentDamageDetails : NavigationBaseActivity(), CompoundButton.OnCheckedChangeListener, deleteDamageDetails, clickDamage {
    var sessionManager: SessionManager? = null
    var customValidator: CustomValidator? = null
    lateinit var damageAlertDialog: AlertDialog
    var text_for_damage_type: TextView? = null
    var checkbox_block: LinearLayout? = null
    lateinit var db:AccidentModuleDb
    lateinit var injury_radio: AppCompatRadioButton
    lateinit var death_radio: AppCompatRadioButton
    lateinit var property_radio: AppCompatRadioButton
    var adapter: KotAccidentDamageListAdapter? = null
    var listDamageDetails:ArrayList<DamageDetails> = ArrayList()
    var permissionsManager = PermissionManager()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var TAG = this@KotAccidentDamageDetails.javaClass.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_accident_damage_details, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this)
        initIds()
        intitiliseAdapter()
        checkAndSetTheData()
    }


    /**
     * email validation
     */

    fun isValidEmail(target: CharSequence?): Boolean {
        var result =  !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        return result
    }

    fun setTextOnNextBtn(list: List<DamageDetails>){
        CoroutineScope(Main).launch {
            if(list.isEmpty()){
//                "No Damage"
                next_btn.setText(getString(R.string.no_damage_injury_reported))
                message.visibility = View.VISIBLE

            }else {
                next_btn.setText("Done")
                message.visibility = View.GONE

            }
        }
    }

    /**
     * check and set the data
     */
    fun checkAndSetTheData(){
        CoroutineScope(IO).launch {
            var tempList = db.damageDao().getDamageDetails()

            if (tempList!=null){
                if (tempList.size>0){
                    listDamageDetails.clear()
                    var reverseList = tempList.reversed()
                    listDamageDetails.addAll(reverseList)
                    resetAdapter()
                    setTextOnNextBtn(tempList)
                }
                else {
                    setTextOnNextBtn(tempList)
                }
            }
        }

    }


    /**
     * set the adapter if there is change in data
     */
    fun resetAdapter(){
        CoroutineScope(Dispatchers.Main).launch {
            adapter!!.notifyDataSetChanged()
        }
    }

    /**
     * initiialise adapter
     */
    fun intitiliseAdapter(){
        adapter = KotAccidentDamageListAdapter(this@KotAccidentDamageDetails,listDamageDetails,
                this@KotAccidentDamageDetails,this@KotAccidentDamageDetails)
        list_view_damages!!.adapter = adapter

    }


    fun initIds() {
        sessionManager = SessionManager()
        customValidator = CustomValidator(this@KotAccidentDamageDetails)
        db = AccidentModuleDb.getDatabase(this@KotAccidentDamageDetails)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    fun showMessage(str:String):Boolean{
        Toast.makeText(this@KotAccidentDamageDetails,str,Toast.LENGTH_SHORT).show()
        return false
    }

    fun validationDamageDetails(view:View):Boolean{
        if (damageAlertDialog.first_name.text.toString().trim().isEmpty()) return showMessage("Please enter your first name")
//        else if (damageAlertDialog.last_name.text.toString().trim().isEmpty()) return showMessage("Please enter your last name")
        else if (damageAlertDialog.address.text.toString().trim().isEmpty()) return showMessage("Please enter your address")
        else if (damageAlertDialog.phone_no.text.toString().trim().isEmpty()) return showMessage("Please enter your phone no")
//        else if (damageAlertDialog.email.text.toString().trim().isEmpty()) return showMessage("Please enter your email")


        else if (!view.property_radio.isChecked){
             if (!isValidEmail(damageAlertDialog.email.text.toString().trim())) return showMessage("Please enter your email")
            if (damageAlertDialog.injury_radio.isChecked||damageAlertDialog.death_radio.isChecked){
                if (retrieveCheckedList().isEmpty()) return showMessage("Please check the category of people who got injured")
                else return true
            }
           else return true

        } else {
            if (damageAlertDialog.injury_radio.isChecked||damageAlertDialog.death_radio.isChecked){
                if (retrieveCheckedList().isEmpty()) return showMessage("Please check the category of people who got injured")
                else return true
            }

            return true
        }






    }

    fun retrieveCheckedList():ArrayList<String>{
        return findSelectedCheckBoxList(createCheckBoxList())
    }

    private fun findSelectedCheckBoxList(listCheckBox: ArrayList<AppCompatCheckBox>):ArrayList<String>{
        var selectedCheckBox:ArrayList<String> = ArrayList()
        for (checkBox:AppCompatCheckBox in listCheckBox){
            if (checkBox.isChecked){
                val value = checkBox.findViewById<AppCompatCheckBox>(checkBox.id).text.toString()
                val builder = StringBuilder(value)
                val valuess = builder.delete(0, 3)
                val newValue = valuess.toString().replace("[?]".toRegex(), "")
                selectedCheckBox.add(newValue)
            }
        }
        return selectedCheckBox
    }



    private fun createCheckBoxList():ArrayList<AppCompatCheckBox> {
        Log.e("createCheckBoxList","init")
        var list:ArrayList<AppCompatCheckBox> = ArrayList()

        damageAlertDialog.apply {
            list.add(isDriverCheckBox)
            list.add(isPassengerCheckBox)
            list.add(isBicyclistCheckBox)
            list.add(isPedestrianCheckBox)
        }

        return list
    }






    fun updateDialogWithData(view:View,obj: DamageDetails?){

        if (obj!=null) {

            if (obj.injuryType.equals(getString(R.string.injury))) {
                view.injury_radio.isChecked = true
            } else if (obj.injuryType.equals(getString(R.string.death))) {
                view.death_radio.isChecked = true
            } else if (obj.injuryType.equals(getString(R.string.property))) {
                view.property_radio.isChecked = true
            }


            if (obj.email.isEmpty()){
                view.email_view.visibility = View.GONE
            }
            else {
                view.email_view.visibility = View.VISIBLE
            }

            view.damage_description.setText(obj.damageDescription)
            view.first_name.setText(obj.firstName)
            view.address.setText(obj.address)
            view.person_city.setText(obj.city)
            view.person_state.setText(obj.state)
            view.person_zipcode.setText(obj.zipcode)
            view.phone_no.setText(obj.phoneNo)
            view.email.setText(obj.email)

            for (str in obj.listTypePeople){
                if(str.equals("Driver",ignoreCase = true)){
                    view.isDriverCheckBox.isChecked = true
                }else if (str.equals("Passenger",ignoreCase = true)){
                    view.isPassengerCheckBox.isChecked = true
                }else if (str.equals("Bicyclist",ignoreCase = true)){
                    view.isBicyclistCheckBox.isChecked = true
                }else if (str.equals("Pedestrian",ignoreCase = true)){
                    view.isPedestrianCheckBox.isChecked = true
                }
            }

            view.add_btn.setText("Update")


        }


//
//        view.injury_radio.isChecked = obj.
//        death_radio.isChecked
//        property_radio.isChecked
//
//
//        damage_description.setText(obj)
//        first_name.setText()
//        address.setText()
//        person_city.setText()
//        person_state.setText()
//        person_zipcode.setText()
//        phone_no.setText()
//        email.setText()
//
//        isDriverCheckBox.isChecked
//        isBicyclistCheckBox.isChecked
//        isPedestrianCheckBox.isChecked
//        isPassengerCheckBox.isChecked


    }

    fun showDetailPopUp(isUpdate:Boolean,obj: DamageDetails?) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
//        dialog_accident_damage_detail
        val dialogView = inflater.inflate(R.layout.test_scroll_issue, null)
        dialogBuilder.setView(dialogView)
//        val zoomLinearLayout: ZoomLinearLayout = dialogView.findViewById(R.id.zoom_linear_layout)
//        zoomLinearLayout.init(this@KotAccidentDamageDetails)
        val cancel_btn = dialogView.findViewById<Button>(R.id.cancel_btn)
        val add_btn = dialogView.findViewById<Button>(R.id.add_btn)

        injury_radio = dialogView.findViewById(R.id.injury_radio)
        death_radio = dialogView.findViewById(R.id.death_radio)
        property_radio = dialogView.findViewById(R.id.property_radio)
        text_for_damage_type = dialogView.findViewById(R.id.text_for_damage_type)
        checkbox_block = dialogView.findViewById(R.id.checkbox_block)



        injury_radio.setOnCheckedChangeListener(this@KotAccidentDamageDetails)
        death_radio.setOnCheckedChangeListener(this@KotAccidentDamageDetails)
        property_radio.setOnCheckedChangeListener(this@KotAccidentDamageDetails)


        dialogView.phone_no.setText("")


        add_btn.setOnClickListener {

            var accidentType:String = ""

            if (dialogView.radioGroupDamageType.checkedRadioButtonId == R.id.injury_radio){
                Log.e("tag","injuryradio")
//                dialogView.injury_radio.isChecked = true
                accidentType = getString(R.string.injury)
            }else if (dialogView.radioGroupDamageType.checkedRadioButtonId == R.id.death_radio){
                Log.e("tag","deathRadio")
//                dialogView.death_radio.isChecked = true
                accidentType = getString(R.string.death)
            }else if (dialogView.radioGroupDamageType.checkedRadioButtonId == R.id.property_radio){
                Log.e("tag","propertyRadio")
//                dialogView.property_radio.isChecked = true
                accidentType = getString(R.string.property)
            }





            if (validationDamageDetails(dialogView)){

                CoroutineScope(IO).launch {


                    var no:Int = 0
                    var retrieveLastItem = db.damageDao().getDamageDetails()
                    if (retrieveLastItem.size>0){
                        var value =  retrieveLastItem.get(retrieveLastItem.size-1)
                        if (isUpdate) no = obj?.id!! else  no = value.id!!+1
                    }

                    var damageDetails:DamageDetails
                    damageAlertDialog.apply {
                        damageDetails = DamageDetails(
                                no
                                ,accidentType
                                ,first_name.text.toString()
                                ,damage_description.text.toString()
                                ,address.text.toString()
                                ,person_city.text.toString()
                                ,person_state.text.toString()
                                ,person_zipcode.text.toString()
                                ,phone_no.text.toString()
                                ,email.text.toString()
                                ,retrieveCheckedList()

                        )
                    }
                    db.damageDao().insert(damageDetails)
                    checkAndSetTheData()
                    damageAlertDialog.dismiss()
                }
            }
        }

        cancel_btn.setOnClickListener {
            damageAlertDialog!!.dismiss()
        }


        dialogView.add_btn.setText("Add")
        if(isUpdate){
            updateDialogWithData(dialogView,obj)
        }




        damageAlertDialog = dialogBuilder.create()
        //emailAlertDialog.setTitle("Send Documents By Email");
        damageAlertDialog.show()
//        damageAlertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        damageAlertDialog.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)


    }

//    @Bind(R.id.list_view_damages)
//    var list_view_damages: ListView? = null

    @OnClick(R.id.add_damage_detail)
    fun callFunc() {
        showDetailPopUp(false,null)
    }

    @OnClick(R.id.next_btn)
    fun nextActivty() {
//        val i = Intent(this@KotAccidentDamageDetails, ReportedAccidentScreenFive::class.java)
//        startActivity(i)
        CoroutineScope(IO).launch {
            var list  = db.damageDao().getDamageDetails()
            if (list.isEmpty()){
//                CoroutineScope(Main).launch {
//                    Toast.makeText(this@KotAccidentDamageDetails,"Please enter the vehicle details.",Toast.LENGTH_SHORT).show()
//                }

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
    }


    fun saveDBStatus(){
        sessionManager?.saveStatusInjuredBasicPref()
    }

    fun saveTextStatus(){
        if (next_btn.text.toString().equals("Done",ignoreCase = true)){
            sessionManager?.saveStatusInjuredTextBasicPref()
        } else {
            sessionManager?.clearTextInjuredPref()
        }
    }

    fun clearTextStatus(){

        sessionManager?.clearTextInjuredPref()
    }
    fun emailVisibility(notVisiBle:Boolean){

            if(notVisiBle){
                damageAlertDialog.email_view.visibility = View.VISIBLE

            }else {
                damageAlertDialog.email_view.visibility = View.GONE
                damageAlertDialog.email.setText("")
            }

    }


    override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
        val id = compoundButton.id
        if (b) {
            Log.e("id", "" + id)
            when (id) {
                R.id.injury_radio -> {
                    Log.e("in", "injury_radio")
                    text_for_damage_type!!.text = "Injured Person Details"
                    checkbox_block!!.visibility = View.VISIBLE
                    setLocation(false)
                    emailVisibility(true)
                }
                R.id.death_radio -> {
                    Log.e("in", "death_radio")
                    text_for_damage_type!!.text = "Deceased Person Details"
                    checkbox_block!!.visibility = View.VISIBLE
                    setLocation(false)
                    emailVisibility(true)
                }
                R.id.property_radio -> {
                    Log.e("in", "property_radio")
                    text_for_damage_type!!.text = "Property Owner Details"
                    checkbox_block!!.visibility = View.GONE
                    getCurrentLocationPermission()
                    setLocation(true)
                    emailVisibility(false)
                }
            }
        }
    }


    /**
     * sel location details on the editText fields
     */
    fun setLocation(whetherSetFields:Boolean){
        if(whetherSetFields) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                return
            }
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            Log.e("location", ": " + location.latitude.toString())
                            val gcd = Geocoder(this@KotAccidentDamageDetails, Locale.getDefault())
                            val addresses: List<Address> = gcd.getFromLocation(location.latitude, location.longitude, 1)
                            if (addresses.isNotEmpty()) {
                                Log.e("address: ", "" + addresses[0].getAddressLine(0))


                                val city = addresses[0].locality
                                val state = addresses[0].adminArea
                                val country = addresses[0].countryName
                                val postalCode = addresses[0].postalCode
                                val knownName = addresses[0].featureName // Only if available else return NULL

                                val streetNo = addresses[0].subThoroughfare
                                val locality = addresses[0].thoroughfare
//
//
//                                Log.d("address ::", "getAddress:  address$address")
//                                Log.d("address ::", "getAddress:  city$city")
//                                Log.d("address ::", "getAddress:  state$state")
//                                Log.d("address ::", "getAddress:  postalCode$postalCode")
//                                Log.d("address ::", "getAddress:  knownName$knownName")
//

                                damageAlertDialog.apply {
//                                    addresses[0].getAddressLine(0)
                                    address.setText(streetNo+", "+locality)
                                    person_city.setText(city)
                                    person_state.setText(state)
                                    person_zipcode.setText(postalCode)

                                }


                            } else {
                                Log.e(TAG, "Location address is empty")
                                // do your stuff
                            }
                        }
                        // Got last known location. In some rare situations this can be null.
                    }
        }else {
//            damageAlertDialog.apply {
//                damageAlertDialog.address.setText("")
//                damageAlertDialog.person_city.setText("")
//                damageAlertDialog.person_state.setText("")
//                damageAlertDialog.person_zipcode.setText("")
//            }
        }
    }


    fun getCurrentLocationPermission(){
        if (!permissionsManager.checkPermission(this@KotAccidentDamageDetails, this@KotAccidentDamageDetails, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsManager.askForPermission(this@KotAccidentDamageDetails, this@KotAccidentDamageDetails, Manifest.permission.ACCESS_FINE_LOCATION, DashboardActivity.FINE_LOCATION)
        }
        if (Utils.isLocationEnabled(this@KotAccidentDamageDetails)) {
            Log.e("Location Enabled ", "True")
//            startGpsTrackerService()
        } else {
            Log.e("Location Enabled ", "False")
            val builder = AlertDialog.Builder(this@KotAccidentDamageDetails)
            builder.setCancelable(false)
            builder.setTitle(R.string.gps_not_found_title) // GPS not found
            builder.setMessage(R.string.gps_not_found_message) // Want to enable?
            builder.setPositiveButton(R.string.yes) { dialogInterface, i ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1)
                // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
            builder.setNegativeButton(R.string.no) { dialog, which ->
                val dialog_i = Intent(this@KotAccidentDamageDetails, DialogActivity::class.java)
                dialog_i.putExtra("is_location_dialog", true)
                startActivity(dialog_i)
            }
            builder.create().show()
            return
        }
    }

    override fun delteteData(data: DamageDetails) {
        AlertDialog.Builder(this@KotAccidentDamageDetails,R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Alert!!")
                .setMessage("Are you sure you want to delete this info?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        // Continue with delete operation
                        dialog!!.dismiss()
                        CoroutineScope(IO).launch {
                            db.damageDao().deleteDamageDetail(data)
                            CoroutineScope(Dispatchers.Main).launch {
                                listDamageDetails.remove(data)
                                resetAdapter()
                            }
                        }
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

    override fun updateDamage(obj: DamageDetails) {
        showDetailPopUp(true,obj)
    }

}