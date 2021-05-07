package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import butterknife.ButterKnife
import butterknife.OnClick
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.android.material.textfield.TextInputLayout
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters.KotAccidentVehicleListAdapter
import com.tmsfalcon.device.tmsfalcon.customtools.*
import com.tmsfalcon.device.tmsfalcon.customtools.AppController.INCIDENT
import com.tmsfalcon.device.tmsfalcon.customtools.AppController.strIncidentType
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.MultipleVehicleDetails
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel
import com.tmsfalcon.device.tmsfalcon.interfacess.clickEventOtherUpdate
import com.tmsfalcon.device.tmsfalcon.interfacess.deleteDriverDetails
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_accident_vehicle_details.*
import kotlinx.android.synthetic.main.dialog_accident_vehicle_detail.view.*
import kotlinx.android.synthetic.main.insurance_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class KotAccidentVehicleDetails : NavigationBaseActivity(), deleteDriverDetails, clickEventOtherUpdate {
    var vehicle_insurance_provider_edittext: EditText? = null
//    var vehicle_insurance_no_edittext: EditText? = null
//    var vehicle_dot_number_edittext: EditText? = null
//    var vehicle_license_no_edittext: EditText? = null
//    var vehicle_license_state_edt: EditText? = null
//    var policy_holder_name_edt: EditText? = null
//    var policy_holder_lastname_edt: EditText? = null

    var checkIsSameAsDriver: AppCompatCheckBox? = null
    var vehicleAlertDialog: AlertDialog? = null

//    var vehicle_year_edt: EditText? = null
//    var vehicle_make_edt: EditText? = null

    var vehicle_owner_name_edt: EditText? = null
    var vehicle_owner_lastname_edt: EditText? = null
    var vehicle_owner_phoneno_edt: EditText? = null
    var vehicle_owner_dob_edt: TextView? = null
    var vehile_owner_city_edt: EditText? = null
    var vehicle_owner_state_edt: EditText? = null
    var vehicle_owner_zipcode_edt: EditText? = null
//    var edt_company_name: EditText? = null
//    var edt_company_phoneno: MaskedEditText? = null
//    var db: AccidentBasicDetails? = null
    lateinit var db: AccidentModuleDb
    var sessionManager: SessionManager? = null
    var vehicleDetailArrayList: ArrayList<AccidentVehicleDetailsModel>? = null
    var isFormValid = false
    var customValidator: CustomValidator? = null
    var error_vehicle_insurance_provider: TextView? = null
    var error_vehicle_insurance_no: TextView? = null
    var error_vehicle_license_no: TextView? = null
    var popUpOpened = false
    lateinit var is_towed_checkbox: AppCompatCheckBox
    lateinit var tow_block: LinearLayout
    lateinit var scroll_view: ScrollView
    var networkValidator: NetworkValidator? = null
    var vehicleListAdapter: KotAccidentVehicleListAdapter? = null
    var vehicle_json_array = JSONArray()
    var driver_json = JSONObject()
    var truckJsonInfo = JSONObject()
    var listVehicleDetails: ArrayList<MultipleVehicleDetails> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_accident_vehicle_details, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this)
        initIds()
        checkInternet()
    }

    fun clickListener() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                incidentDateListener,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
        )
        dpd.show(this@KotAccidentVehicleDetails.fragmentManager, "DatePickerDialog")
    }



    fun setTextOnNextBtn( list:List<MultipleVehicleDetails>){
        CoroutineScope(Main).launch {
            if(list.isEmpty()){
                next_btn.setText("No Vehicles")
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
    fun checkAndSetTheData() {
        CoroutineScope(IO).launch {
            var tempList = db.mvdDao().getVehicleDetailList()

            if (tempList != null) {
                if (tempList.size > 0) {
                    listVehicleDetails.clear()
                    var reverseList = tempList.reversed()
                    listVehicleDetails.addAll(reverseList)
                    resetAdapter()
                    setTextOnNextBtn(tempList)
                } else {
                    CoroutineScope(Main).launch {
//                        callFunc()
                        setTextOnNextBtn(tempList)
                    }
                }
            }
        }
    }

    /**
     * email validation
     */

    fun isValidEmail(target: CharSequence?): Boolean {
        var result =  !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        return result
    }


    /**
     * email validation for driver truck
     */

    fun isValidEmailForTruckDriver(view: View,target: CharSequence?):Boolean{
        if(view.driver.isChecked){
//            tam jham
            var result =  !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
            return result
        } else if (view.truck_check.isChecked){
//            tam jham
            var result =  !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
            return result
        } else {
            return true
        }
    }

    /**
     * set the adapter if there is change in data
     */
    fun resetAdapter() {
        CoroutineScope(Main).launch {
            visibilityMainView(View.VISIBLE)
            vehicleListAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * initiialise adapter
     */
    fun intitiliseAdapter() {
//        visibilityMainView(View.VISIBLE)
        vehicleListAdapter = KotAccidentVehicleListAdapter(this@KotAccidentVehicleDetails, listVehicleDetails,
                this@KotAccidentVehicleDetails,this@KotAccidentVehicleDetails)
        list_view_vehicles!!.adapter = vehicleListAdapter

    }

    fun initIds() {
//        db = AccidentBasicDetails(this@KotAccidentVehicleDetails)
        db = AccidentModuleDb.getDatabase(this@KotAccidentVehicleDetails)
        sessionManager = SessionManager()
        customValidator = CustomValidator(this@KotAccidentVehicleDetails)
        vehicleDetailArrayList = ArrayList()
        networkValidator = NetworkValidator(this@KotAccidentVehicleDetails)
    }

    fun checkInternet() {

        if (networkValidator!!.isNetworkConnected) {
            progress_bar!!.visibility = View.VISIBLE
            AndroidNetworking.get(UrlController.DRIVER_INFO)
                    .addHeaders("Token", sessionManager!!._token)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            progress_bar!!.visibility = View.GONE
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


//                                        if(truck_json_obj != null && truck_json_obj.length() > 0) {
//                                            vehicle_json_array.put(truck_json_obj);
//                                        }
                                        if (driver_json_object != null && driver_json_object.length() > 0) {
                                            driver_json = driver_json_object
                                        }
                                        if(truck_json_obj != null){
                                           truckJsonInfo = truck_json_obj
                                        }
                                        if (trailer_json_array != null && trailer_json_array.length() > 0) {
                                            for (i in 0 until trailer_json_array.length()) {
                                                val jEmpObj = trailer_json_array.getJSONObject(i)
                                                vehicle_json_array.put(jEmpObj)
                                            }
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                            }

                            intitiliseAdapter()
                            checkAndSetTheData()

//                            main_layout!!.visibility = View.VISIBLE
//                            bottom_layout!!.visibility = View.VISIBLE
//                            vehicleListAdapter = AccidentVehicleListAdapter(this@KotAccidentVehicleDetails)
//                            list_view_vehicles!!.adapter = vehicleListAdapter
                        }

                        override fun onError(error: ANError) {
                            progress_bar!!.visibility = View.GONE
                            Log.e("error", " is $error")
                            // handle error
                        }
                    })
        } else {
            Toast.makeText(this@KotAccidentVehicleDetails, resources.getString(R.string.internet_not_connected), Toast.LENGTH_LONG).show()
        }

    }

    fun doesItReqToShowEmailMessageTrailer(view:View):Boolean{
        if (view.company.isChecked) {
            if (view.trailer_check.isChecked) {
                if (!isValidEmail(view.insureance_trailer_email_no!!.text.toString().trim())) {
                    return true
                }
            }
        }
        return false
    }


    fun validateForm(): Boolean {
        if (popUpOpened) {
            if (!customValidator!!.setRequired(vehicle_insurance_provider_edittext!!.text.toString())) {
                error_vehicle_insurance_provider!!.visibility = View.VISIBLE
                isFormValid = false
            } else {
                error_vehicle_insurance_provider!!.visibility = View.GONE
                isFormValid = true
            }
//            if (!customValidator!!.setRequired(vehicle_insurance_no_edittext!!.text.toString())) {
//                error_vehicle_insurance_no!!.visibility = View.VISIBLE
//                isFormValid = false
//            }
//
//            else {
//                error_vehicle_insurance_no!!.visibility = View.GONE
//                isFormValid = true
//            }
//            if (!customValidator!!.setRequired(dialogView.vehicle_license_no!!.text.toString())) {
//                error_vehicle_license_no!!.visibility = View.VISIBLE
//                isFormValid = false
//            } else {
//                error_vehicle_license_no!!.visibility = View.GONE
//                isFormValid = true
//            }
        }
        return isFormValid
    }


    fun validationEdtText(view: View): Boolean {

        //if (popUpOpened){

        if(strIncidentType.equals(INCIDENT,ignoreCase = true)){
            return true
        } else if (view.vehicle_year!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter your vehicle year", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.vehicle_make!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter your vehicle make", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (view.vehicle_license_no!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter license no", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.vehicle_license_state!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter license state", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.driver.isChecked && view.insurance_name!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance company as driver.", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.driver.isChecked && view.insurance_policy_no!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance no as driver.", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.driver.isChecked && view.policy_holder_phoneno!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered phone no as driver.", Toast.LENGTH_SHORT).show()
            return false
        }

//        else if (view.driver.isChecked && view.policy_holder_email!!.text.toString().trim { it <= ' ' }.length == 0) {
//            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no as driver.", Toast.LENGTH_SHORT).show()
//            return false
//        }



//                check

        if (view.company.isChecked && view.truck_check.isChecked && view.insurance_name!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance company for truck.", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.company.isChecked && view.truck_check.isChecked && view.insurance_policy_no!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance no for truck.", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.company.isChecked && view.truck_check.isChecked && view.policy_holder_phoneno!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered phone no for truck.", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.company.isChecked && view.truck_check.isChecked && view.policy_holder_email!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no for truck.", Toast.LENGTH_SHORT).show()
            return false
        }



        else if (!(/*view.driver.isChecked && */isValidEmailForTruckDriver(view,view.policy_holder_email!!.text.toString().trim()))) {
            if(view.driver.isChecked)
                Toast.makeText(this@KotAccidentVehicleDetails, "Please enter correct insurance email as driver.", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this@KotAccidentVehicleDetails, "Please enter correct insurance email for truck.", Toast.LENGTH_SHORT).show()
            return false
        }



// check
        if (view.company.isChecked && view.trailer_check.isChecked && view.trailer_insurance_company!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance company for trailer.", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.company.isChecked && view.trailer_check.isChecked && view.trailer_insurance_no!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance no for trailer.", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.company.isChecked && view.trailer_check.isChecked && view.insurance_trailer_phone_no!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered phone no for trailer.", Toast.LENGTH_SHORT).show()
            return false

        }
//        else if (!(view.company.isChecked && view.trailer_check.isChecked && /*view.insureance_trailer_email_no!!.text.toString().trim { it <= ' ' }.length == 0*/
//                isValidEmail(view.insureance_trailer_email_no!!.text.toString().trim()))) {
//            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no for trailer.", Toast.LENGTH_SHORT).show()
//            return false
//
//        }
//


//        else if (isValidEmail(view.insureance_trailer_email_no!!.text.toString().trim())) {
//            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no for trailer.", Toast.LENGTH_SHORT).show()
//            return false
//
//        }


        else if (doesItReqToShowEmailMessageTrailer(view)){
            Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no for trailer.", Toast.LENGTH_SHORT).show()
            return false
        }

        else if (vehicle_owner_name_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter vehicle owner name", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.owner_phone_no!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter vehicle owner phone no", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.owner_address!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter vehicle owner address", Toast.LENGTH_SHORT).show()
            return false
        }
//        else if (vehicle_owner_dob_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
//            Toast.makeText(this, "Please enter owner date of birth", Toast.LENGTH_SHORT).show()
//            return false
//        }
        else if (vehile_owner_city_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter owner city", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehicle_owner_state_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter owner state", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehicle_owner_zipcode_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter owner zipcode", Toast.LENGTH_SHORT).show()
            return false
        } else if (is_towed_checkbox.isChecked) {
            if (view.towing_company_name!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show()
                return false
            } else if (view.towing_company_phone!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this, "Please enter company phone no", Toast.LENGTH_SHORT).show()
                return false
            } else {
                return true
            }
        } else {
            return true
        }


    }


    fun makeYearFourCharacter(raw:String):String{
        val s = raw.trim()
        val upTo4Characters = s.substring(0, Math.min(s.length, 4))
        return upTo4Characters
    }

    fun showTruckInfo(obj:JSONObject,view:View){

        var truckUnitNo = obj.getString("unit_number")
        var truckYear = obj.getString("year")
        var truckMake = obj.getString("make")
        var truckInsuranceName = obj.getString("insurance_name")
        var truckInsurancePolicyNo = obj.getString("insurance_policy_number")
        var truckLicensePlateNo = obj.getString("license_plate_number")
        var truckRegisteredState = obj.getString("registered_state")

        view.apply {
            truck_unit_no_dialog.setText(truckUnitNo)



            truck_year.setText(makeYearFourCharacter(truckYear))
            truck_make.setText(truckMake)
            truck_license_no.setText(truckLicensePlateNo)
            truck_license_state.setText(truckRegisteredState)
        }
    }


    fun manageTruckSectionView(view: View,bool:Boolean){
        if(bool){
            var truckInsuracneName = truckJsonInfo.getString("insurance_name")
            var truckInsurancePolicyNo= truckJsonInfo.getString("insurance_policy_number")
//            Todo need to be change
//            view.insurance_name.setText(truckInsuracneName)
//            view.insurance_name.setText("")
//            view.insurance_policy_no.setText(truckInsurancePolicyNo)
//            view.policy_holder_phoneno.setText("")
//            view.policy_holder_email.setText("")

        }else{
            //            Todo need to be change
//            view.insurance_name.setText("")
//            view.insurance_policy_no.setText("")
//            view.policy_holder_phoneno.setText("")
//            view.policy_holder_email.setText("")

        }

    }


    fun copyvalidationEdtText(view: View): Boolean {

        //if (popUpOpened){
        if (view.vehicle_year!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter your vehicle year", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.vehicle_make!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter your vehicle make", Toast.LENGTH_SHORT).show()
            return false
        }
        else if (view.vehicle_license_no!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter license no", Toast.LENGTH_SHORT).show()
            return false
        } else if (view.vehicle_license_state!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter license state", Toast.LENGTH_SHORT).show()
            return false
        }

//
//        else if (vehicle_insurance_provider_edittext!!.text.toString().trim { it <= ' ' }.length == 0) {
//            Toast.makeText(this, "Please enter insurance company", Toast.LENGTH_SHORT).show()
//            return false
//        } else if (vehicle_insurance_no_edittext!!.text.toString().trim { it <= ' ' }.length == 0) {
//            Toast.makeText(this, "Please enter insurance no", Toast.LENGTH_SHORT).show()
//            return false
//        } else if (policy_holder_name_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
//            Toast.makeText(this, "Please enter policy holder name", Toast.LENGTH_SHORT).show()
//            return false
//        } else if (policy_holder_lastname_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
//            Toast.makeText(this, "Please enter policy holder last name", Toast.LENGTH_SHORT).show()
//            return false
//        }
//


        else if (view.driver.isChecked) {


            if (view.insurance_name!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance company.", Toast.LENGTH_SHORT).show()
                return false
            } else if (view.insurance_policy_no!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance no.", Toast.LENGTH_SHORT).show()
                return false
            } else if (view.policy_holder_phoneno!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered phone no", Toast.LENGTH_SHORT).show()
                return false
            } else if (view.policy_holder_email!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no", Toast.LENGTH_SHORT).show()
                return false
            } else {
                return true
            }

        } else if (view.company.isChecked) {
            if (view.truck_check.isChecked) {
                if (view.insurance_name!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance company.", Toast.LENGTH_SHORT).show()
                    return false
                } else if (view.insurance_policy_no!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance no.", Toast.LENGTH_SHORT).show()
                    return false
                } else if (view.policy_holder_phoneno!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered phone no.", Toast.LENGTH_SHORT).show()
                    return false
                } else if (view.policy_holder_email!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no.", Toast.LENGTH_SHORT).show()
                    return false
                } else {
                    return true
                }
//                    vehicle_insurance_company
//                    vehicle_insurance_no
//                    policy_holder_phoneno
//                    policy_holder_email
            } else if (view.trailer_check.isChecked) {

                if (view.trailer_insurance_company!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance company.", Toast.LENGTH_SHORT).show()
                    return false
                } else if (view.trailer_insurance_no!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter insurance no.", Toast.LENGTH_SHORT).show()
                    return false
                } else if (view.insurance_trailer_phone_no!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered phone no.", Toast.LENGTH_SHORT).show()
                    return false
                } else if (view.insureance_trailer_email_no!!.text.toString().trim { it <= ' ' }.length == 0) {
                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no.", Toast.LENGTH_SHORT).show()
                    return false
                } else {
                    return true
                }
            } else {
                return true
            }

        } else if (vehicle_owner_name_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter vehicle owner name", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehicle_owner_lastname_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter vehicle owner name", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehicle_owner_phoneno_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter vehicle owner phone no", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehicle_owner_dob_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter owner date of birth", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehile_owner_city_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter vehicle owner phone no", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehicle_owner_state_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter owner state", Toast.LENGTH_SHORT).show()
            return false
        } else if (vehicle_owner_zipcode_edt!!.text.toString().trim { it <= ' ' }.length == 0) {
            Toast.makeText(this, "Please enter owner zipcode", Toast.LENGTH_SHORT).show()
            return false
        } else if (is_towed_checkbox.isChecked) {
            if (view.towing_company_name!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this, "Please enter company name", Toast.LENGTH_SHORT).show()
                return false
            } else if (view.towing_company_phone!!.text.toString().trim { it <= ' ' }.length == 0) {
                Toast.makeText(this, "Please enter company phone no", Toast.LENGTH_SHORT).show()
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    var incidentDateListener: DatePickerDialog.OnDateSetListener? = null
    lateinit var outBirthdateview: TextInputLayout


    var strStatusDriverOrTruck:String = "driver"

    fun setStatusDriverOrTruck(str:String){
        strStatusDriverOrTruck = str
    }

    fun getStatusDriverOrTruck():String{
        return strStatusDriverOrTruck
    }

    fun safeFetchFromJson(json:JSONObject,key:String):String{
       var returnString:String
        if (json.has(key))
        returnString = json.getString(key)
        else
        returnString = ""

        return returnString
    }

    fun showVehicleDetailPopUp(isUpdate:Boolean) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_accident_vehicle_detail, null)
        dialogBuilder.setView(dialogView)
        popUpOpened = true
        is_towed_checkbox = dialogView.findViewById(R.id.is_towed_checkbox)
        scroll_view = dialogView.findViewById(R.id.scroll_view)
        tow_block = dialogView.findViewById(R.id.tow_block)
        val zoomLinearLayout: ZoomLinearLayout = dialogView.findViewById(R.id.zoom_linear_layout)
        zoomLinearLayout.init(this@KotAccidentVehicleDetails)
        dialogView.rad_group_insurance.check(R.id.driver)

        var truckPlateNo = safeFetchFromJson(truckJsonInfo,"license_plate_number")


        dialogView.select_truck_no.setText(truckPlateNo)

        dialogView.policy_holder_phoneno.setText("")
        dialogView.insurance_trailer_phone_no.setText("")

        /**
         * initialise driver view
         */
        dialogView.apply {
            vehicle_tyoes.visibility = View.GONE
            truck_no_list_view.visibility = View.GONE
            view_trailer_details.visibility = View.GONE
            view_truck_details.visibility = View.VISIBLE
            truck_insurance_heading.text = "Driver Insurance Details:"
            setStatusDriverOrTruck(resources.getString(R.string.driver))
        }


        dialogView.rad_group_insurance.setOnCheckedChangeListener { radioGroup, i ->
            if (radioGroup.driver.isChecked) {
                /**
                 * show driver view insurance
                 */
                Log.e("insuracne", "show driver")
                dialogView.apply {
                    vehicle_tyoes.visibility = View.GONE
                    truck_no_list_view.visibility = View.GONE
                    view_trailer_details.visibility = View.GONE
                    view_truck_details.visibility = View.VISIBLE
                    truck_insurance_heading.text = "Driver Insurance Details:"
                    manageTruckSectionView(dialogView,false)
                    setStatusDriverOrTruck(resources.getString(R.string.driver))
                }

            } else {
                /**
                 * show company view insurance
                 */
                Log.e("insuracne", "show company")
                dialogView.apply {
                    truck_check.isChecked = true
                    truck_no_list_view.visibility = View.VISIBLE
                    vehicle_tyoes.visibility = View.VISIBLE
                    view_truck_details.visibility = View.VISIBLE
                    truck_insurance_heading.text = "Truck Insurance Details:"
                    view_trailer_details.visibility = View.GONE
                    trailer_check.isChecked = false

                    manageTruckSectionView(dialogView,true)
                    setStatusDriverOrTruck(resources.getString(R.string.truck))
                }

            }

        }



        dialogView.truck_check.setOnCheckedChangeListener { compoundButton, b ->

            /**
             * manage view on checkbox
             * At some instance on click of checkbox it wont uncheck
             * So in that cases the views are managed in the below process
             */
            dialogView.apply {
                if (!b) {
                    truck_check.isChecked = !trailer_check.isChecked
                    view_truck_details.visibility = View.VISIBLE

                    if (!trailer_check.isChecked) {
                        view_truck_details.visibility = View.VISIBLE
                        truck_insurance_heading.text = "Truck Insurance Details:"
                        manageTruckSectionView(dialogView,true)
                    } else {
                        view_truck_details.visibility = View.GONE
                    }

                } else {
                    if (b) {
                        view_truck_details.visibility = View.VISIBLE
                        truck_insurance_heading.text = "Truck Insurance Details:"
                        manageTruckSectionView(dialogView,true)
                    } else {
                        view_truck_details.visibility = View.GONE
                    }
                }
            }
        }

        dialogView.trailer_check.setOnCheckedChangeListener { compoundButton, b ->

            /**
             * manage view on checkbox
             * At some instance on click of checkbox it wont uncheck
             * So in that cases the views are managed in the below process
             */
            dialogView.apply {
                if (!b) {
                    trailer_check.isChecked = !truck_check.isChecked
                    if (!truck_check.isChecked) {
                        view_trailer_details.visibility = View.VISIBLE
                        setStatusDriverOrTruck(resources.getString(R.string.trailer))
                    } else {
                        view_trailer_details.visibility = View.GONE
                    }
                } else {
                    if (b) {
                        view_trailer_details.visibility = View.VISIBLE
                        setStatusDriverOrTruck(resources.getString(R.string.trailer))
                    } else {
                        view_trailer_details.visibility = View.GONE
                    }
                }

            }
        }


        val trailerPlateNo = ArrayList<String?>()
        Log.e("size_of_array :", "" + vehicle_json_array.length())
        for (i in 0 until vehicle_json_array.length()) {
            try {
                val jsonObject = vehicle_json_array.getJSONObject(i)
                var unit_no = jsonObject.getString("license_plate_number")
                unit_no = "Trailer Plate No: $unit_no"
                Log.e("unitNo :", "" + unit_no)
                if (!unit_no.trim { it <= ' ' }.equals("Plate No:".trim { it <= ' ' }, ignoreCase = true)) trailerPlateNo.add(unit_no)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }



        val adapterForTrailer: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, trailerPlateNo)
        adapterForTrailer.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)

        dialogView.trailer_plate_no.adapter = adapterForTrailer
        dialogView.trailer_plate_no.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                (p1 as TextView).setTextColor(ContextCompat.getColor(this@KotAccidentVehicleDetails,R.color.white_greyish))
                val jsonObject: JSONObject = vehicle_json_array.get(p2) as JSONObject
                val insurance_name: String = jsonObject.getString("insurance_name")
                val insurance_policy_number: String = jsonObject.getString("insurance_policy_number")
//                TODO uncomment
//                dialogView.trailer_insurance_company.setText(insurance_name)
//                dialogView.trailer_insurance_no.setText(insurance_policy_number)
            }

        }

        val same_as_driver_checkbox: AppCompatCheckBox = dialogView.findViewById(R.id.same_as_driver_checkbox)
        val owner_first_name = dialogView.findViewById<EditText>(R.id.owner_first_name)
        val owner_last_name = dialogView.findViewById<EditText>(R.id.owner_phone_no)
        val owner_phone_no = dialogView.findViewById<EditText>(R.id.owner_phone_no)
        val owner_dob = dialogView.findViewById<TextView>(R.id.owner_dob)
        val owner_city = dialogView.findViewById<EditText>(R.id.owner_city)
        val owner_state = dialogView.findViewById<EditText>(R.id.owner_state)
        val owner_zipcode = dialogView.findViewById<EditText>(R.id.owner_zipcode)
        checkIsSameAsDriver = same_as_driver_checkbox
        vehicle_owner_name_edt = owner_first_name
        vehicle_owner_lastname_edt = owner_last_name
        vehicle_owner_phoneno_edt = owner_phone_no
        vehicle_owner_dob_edt = owner_dob
        vehile_owner_city_edt = owner_city
        vehicle_owner_state_edt = owner_state
        vehicle_owner_zipcode_edt = owner_zipcode


        owner_dob.setOnClickListener {
            clickListener()
        }



        incidentDateListener = DatePickerDialog.OnDateSetListener { datePickerDialog, year, monthOfYear, dayOfMonth ->
            val date = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
            owner_dob!!.text = date
        }

        same_as_driver_checkbox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
                if (compoundButton.isChecked) {
                    try {
                        val first_name = driver_json.getString("first_name")
                        val last_name = driver_json.getString("last_name")
                        val cell = driver_json.getString("cell")
                        val dob = driver_json.getString("dob")
                        val address = driver_json.getString("address")
                        val city = driver_json.getString("city")
                        val zip_code = driver_json.getString("zip_code")
                        val state = driver_json.getString("state")
                        owner_first_name.setText(first_name)
                        owner_last_name.setText(last_name)
                        owner_phone_no.setText(cell)
                        owner_dob.text = dob
//                        TODO uncomment
                        dialogView.owner_address.setText(address)
                        owner_city.setText(city)
                        owner_state.setText(state)
                        owner_zipcode.setText(zip_code)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    owner_first_name.setText("")
                    owner_last_name.setText("")
                    owner_phone_no.setText("")
                    owner_dob.text = ""
//                      TODO uncomment
                    owner_city.setText("")
                    owner_state.setText("")
                    owner_zipcode.setText("")
                }
            }
        })


        val unit_no_array = ArrayList<String?>()
        Log.e("size_of_array :", "" + vehicle_json_array.length())
        for (i in 0 until vehicle_json_array.length()) {
            try {
                val jsonObject = vehicle_json_array.getJSONObject(i)
                var unit_no = jsonObject.getString("unit_number")
                unit_no = "Trailer No: $unit_no"
                Log.e("unitNo :", "" + unit_no)
                if (!unit_no.trim { it <= ' ' }.equals("Trailer No:".trim { it <= ' ' }, ignoreCase = true)) unit_no_array.add(unit_no)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        val adapter: ArrayAdapter<*> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unit_no_array)
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        dialogView.vehicle_unit_no.adapter = adapter
        dialogView.vehicle_unit_no.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                try {
                    (view as TextView).setTextColor(ContextCompat.getColor(this@KotAccidentVehicleDetails,R.color.white_greyish))
//                    select_trailer_no.text = adapterView.getItemAtPosition(i).toString()
                    val jsonObject: JSONObject = vehicle_json_array.get(i) as JSONObject
                    val year: String = jsonObject.getString("year")
                    val make: String = jsonObject.getString("make")
                    val license_plate: String = jsonObject.getString("license_plate_number")
                    val license_state: String = jsonObject.getString("registered_state")
                    val insurance_name: String = jsonObject.getString("insurance_name")
                    val insurance_policy_number: String = jsonObject.getString("insurance_policy_number")
                    dialogView.vehicle_year.setText(makeYearFourCharacter(year))
                    dialogView.vehicle_make.setText(make)
                    dialogView.vehicle_license_no.setText(license_plate)
                    dialogView.vehicle_license_state.setText(license_state)
//                  Todo need to be change
//                  dialogView.insurance_name.setText(insurance_name)
//                    dialogView.insurance_policy_no.setText(insurance_policy_number)


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        is_towed_checkbox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
                if (b) { //checked
                    scroll_view.post(object : Runnable {
                        override fun run() {
                            scroll_view.smoothScrollTo(0, scroll_view.bottom)
                        }
                    })
                    tow_block.visibility = View.VISIBLE

                    dialogView.zoom_linear_layout.setPadding(0,0,0,500)
                } else {
                    dialogView.towing_company_name!!.setText("")
                    dialogView.towing_company_phone!!.setText("")
                    tow_block.visibility = View.GONE
                    dialogView.zoom_linear_layout.setPadding(0,0,0,0)

                }
            }
        })


        val add_btn = dialogView.findViewById<Button>(R.id.add_vehicle_detail)


        /**
         * check for update
         */

        if (isUpdate){

            if (updateVehicleDetailObj!=null){
                add_btn.setText(resources.getString(R.string.save))
//                dialogView.insurance_name.setText("well ")

//                update the position of the spinner
                var updatePos = 0
                for ((index, unitNo) in unit_no_array.withIndex()){
                    if (unitNo.equals(updateVehicleDetailObj.trailerNo)){
                        updatePos = index

                    }
                }
                dialogView.vehicle_unit_no.setSelection(updatePos)
                dialogView.vehicle_year.setText(makeYearFourCharacter(updateVehicleDetailObj.vehicleYear))
                dialogView.vehicle_make.setText(updateVehicleDetailObj.vehicleMake)
                dialogView.vehicle_license_no.setText(updateVehicleDetailObj.licenseNos)
                dialogView.vehicle_license_state.setText(updateVehicleDetailObj.licenseState)



                if (updateVehicleDetailObj.insuranceClaimByDriverOrTruck.equals(resources.getString(R.string.driver),ignoreCase = true)){
                    dialogView.rad_group_insurance.check(R.id.driver)
                }else {
                    dialogView.rad_group_insurance.check(R.id.company)
                }


                if(updateVehicleDetailObj.policyHolderPhoneNo.isNotEmpty()){
                    dialogView.truck_check.isChecked = true
                }else if (updateVehicleDetailObj.policyHolderPhoneNo.isEmpty()){
                    dialogView.truck_check.isChecked = false
                }
                else if (updateVehicleDetailObj.trailerPolicyHolderPhoneNo.isNotEmpty()){
                    dialogView.trailer_check.isChecked = true
                }else if (updateVehicleDetailObj.trailerPolicyHolderPhoneNo.isEmpty()){
                    dialogView.trailer_check.isChecked = false
                }


//              truck or driver
                Log.e("result1",": "+updateVehicleDetailObj.insuranceCompany)
                Log.e("result2",": "+updateVehicleDetailObj.insurancePolicyNo)
                //            Todo need to be change
                dialogView.insurance_name.setText(updateVehicleDetailObj.insuranceCompany)
                dialogView.insurance_policy_no.setText(updateVehicleDetailObj.insurancePolicyNo)
                dialogView.policy_holder_phoneno.setText(updateVehicleDetailObj.policyHolderPhoneNo)
                dialogView.policy_holder_email.setText(updateVehicleDetailObj.policyHolderEmail)

//              trailer
                dialogView.trailer_insurance_company.setText(updateVehicleDetailObj.trailerInsuranceCompany)
                dialogView.trailer_insurance_no.setText(updateVehicleDetailObj.trailerInsurancePolicyNo)
                dialogView.insurance_trailer_phone_no.setText(updateVehicleDetailObj.trailerPolicyHolderPhoneNo)
                dialogView.insureance_trailer_email_no.setText(updateVehicleDetailObj.trailerPolicyHolderEmail)


                dialogView.owner_first_name.setText(updateVehicleDetailObj.ownerName)
                dialogView.owner_phone_no.setText(updateVehicleDetailObj.ownerPhnNo)
                dialogView.owner_address.setText(updateVehicleDetailObj.ownerAddress)

//                    dialogView.owner_dob.setText(updateVehicleDetailObj.ownerDob)
                owner_city.setText(updateVehicleDetailObj.ownerCity)
                dialogView.owner_state.setText(updateVehicleDetailObj.ownerState)
                dialogView.owner_zipcode.setText(updateVehicleDetailObj.ownerZipCode)
                dialogView.is_towed_checkbox.isChecked = updateVehicleDetailObj.isVehicleTowedAway
                dialogView.towing_company_name.setText(updateVehicleDetailObj.towedCompanyName)
                dialogView.towing_company_phone.setText(updateVehicleDetailObj.towedCompanyPhnNo)
            }
        }



        add_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

//             isValidEmail(dialogView.policy_holder_email!!.text.toString().trim())
//                if (!(dialogView.driver.isChecked && isValidEmail(dialogView.policy_holder_email!!.text.toString().trim()))) {
//                    Toast.makeText(this@KotAccidentVehicleDetails, "Please enter registered email no as driver.", Toast.LENGTH_SHORT).show()
//
//                }


                if (validationEdtText(dialogView)) {

                    CoroutineScope(IO).launch {

                        var no:Int = 0
                        var retrieveLastItem = db.mvdDao().getVehicleDetailList()
                        if (retrieveLastItem.size>0){
                            var value =  retrieveLastItem.get(retrieveLastItem.size-1)
                            if (isUpdate) no = updateVehicleDetailObj.id!! else  no = value.id!!+1
                        }


                        var multiVehicleDetail = MultipleVehicleDetails(
                                no,
                                dialogView.truck_unit_no_dialog.text.toString(),
                                dialogView.truck_year.text.toString(),
                                dialogView.truck_make.text.toString(),
                                dialogView.truck_license_no.text.toString(),
                                dialogView.truck_license_state.text.toString(),

                                dialogView.vehicle_unit_no.selectedItem.toString(),
                                dialogView.vehicle_year.text.toString(),
                                dialogView.vehicle_make.text.toString(),
                                dialogView.vehicle_license_no.text.toString(),
                                dialogView.vehicle_license_state.text.toString(),


                                (dialogView.rad_group_insurance.findViewById<RadioButton>(dialogView.rad_group_insurance.checkedRadioButtonId)).text.toString(),

//                                findViewById<RadioButton>(dialogView.rad_group_insurance.checkedRadioButtonId).transitionName,
//                                getStatusDriverOrTruck(),
//                                dialogView.insurance_name.text.toString(),
                                removeTextInString(dialogView.insurance_name.text.toString()),
                                dialogView.insurance_policy_no.text.toString(),
                                dialogView.policy_holder_phoneno.text.toString(),
                                dialogView.policy_holder_email.text.toString(),

                                dialogView.trailer_insurance_company.text.toString(),
                                dialogView.trailer_insurance_no.text.toString(),
                                dialogView.insurance_trailer_phone_no.text.toString(),
                                dialogView.insureance_trailer_email_no.text.toString(),

                                owner_first_name.text.toString(), owner_phone_no.text.toString(),
                                dialogView.owner_address.text.toString(), owner_dob.text.toString(), owner_city.text.toString(), owner_state.text.toString(), owner_zipcode.text.toString(),
                                is_towed_checkbox.isChecked, dialogView.towing_company_name.text.toString(), dialogView.towing_company_phone.text.toString()

                        )

                        db.mvdDao().insert(multiVehicleDetail)
                        dismisDialog()

                        checkAndSetTheData()
                    }
                }
            }
        })
        val cancel_btn = dialogView.findViewById<Button>(R.id.cancel_btn)
        cancel_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                vehicleAlertDialog!!.dismiss()
            }
        })

        showTruckInfo(truckJsonInfo,dialogView)

        vehicleAlertDialog = dialogBuilder.create()
        //emailAlertDialog.setTitle("Send Documents By Email");
        if (vehicleAlertDialog != null) {
            vehicleAlertDialog!!.show()
            vehicleAlertDialog!!.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }



    private fun removeTextInString(str :String):String{
        var reqStr = str.replace("Trailer No:","")
        return reqStr
    }

    private inner class GenericTextWatcher private constructor(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.vehicle_insurance_provider -> {
                    val str5 = vehicle_insurance_provider_edittext!!.text.toString()
                    if (str5 !== "" && str5.length > 0) {
                        error_vehicle_insurance_provider!!.visibility = View.GONE
                    } else {
                        error_vehicle_insurance_provider!!.visibility = View.VISIBLE
                    }
                }
                R.id.vehicle_insurance_no -> {
//                    val str6 = vehicle_insurance_no_edittext!!.text.toString()
//                    if (str6 !== "" && str6.length > 0) {
//                        error_vehicle_insurance_no!!.visibility = View.GONE
//                    } else {
//                        error_vehicle_insurance_no!!.visibility = View.VISIBLE
//                    }
                }
                R.id.vehicle_license_no -> {
//                    val str7 = vehicle_insurance_no_edittext!!.text.toString()
//                    if (str7 !== "" && str7.length > 0) {
//                        error_vehicle_license_no!!.visibility = View.GONE
//                    } else {
//                        error_vehicle_license_no!!.visibility = View.VISIBLE
//                    }
                }
            }
        }

    }


    @OnClick(R.id.next_btn)
    fun nextActivity() {
        CoroutineScope(IO).launch {
            var list  =  db.mvdDao().getVehicleDetailList()
            if (list.isEmpty()){
//                CoroutineScope(Main).launch {
//                    Toast.makeText(this@KotAccidentVehicleDetails,"Please enter the vehicle details",Toast.LENGTH_SHORT).show()
//                }

                saveTextStatus()
                saveDBStatus()
                navigateToNextScreen()

            }else {
                clearTextStatus()
                saveDBStatus()
                navigateToNextScreen()
            }
        }
    }

    fun saveDBStatus() {
        sessionManager?.saveStatusBasicPref()
    }

    fun saveTextStatus() {
        if(next_btn.text.toString().equals("Done",ignoreCase = true)){
            sessionManager?.saveStatusTextBasicPref()
        } else {
            sessionManager?.clearTextBasicPref()
        }

    }

    fun clearTextStatus(){
        sessionManager?.clearTextBasicPref()
    }

    fun dismisDialog() {
        CoroutineScope(Main).launch {
            vehicleAlertDialog!!.dismiss()
        }
    }

    fun navigateToNextScreen() {
//            val i = Intent(this@KotAccidentVehicleDetails, OtherPartyAccidentDetails::class.java)
//            startActivity(i)
        val returnIntent = Intent()
        returnIntent.putExtra("result", "result")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()


    }

    fun visibilityMainView(view: Int) {
        main_layout!!.visibility = view
        bottom_layout!!.visibility = view
    }

    @OnClick(R.id.add_vehicle_detail)
    fun callFunc() {
        visibilityMainView(View.VISIBLE)
        showVehicleDetailPopUp(false)
    }


    fun setDataAfterDeletion(data: MultipleVehicleDetails) {
        CoroutineScope(IO).launch {
            db.mvdDao().deleteSingleVehicleDetail(data)
            CoroutineScope(Main).launch {
                listVehicleDetails.remove(data)
                vehicleListAdapter?.notifyDataSetChanged()

            }

        }

    }

    override fun delteteData(data: MultipleVehicleDetails) {

        AlertDialog.Builder(this@KotAccidentVehicleDetails, R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Alert!!")
                .setMessage("Are you sure you want to delete this info?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        // Continue with delete operation
                        dialog!!.dismiss()
                        setDataAfterDeletion(data)


                    }
                }) // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0!!.dismiss()
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()

    }

    lateinit var updateVehicleDetailObj: MultipleVehicleDetails

    override fun updateOtherData(obj: MultipleVehicleDetails) {
        updateVehicleDetailObj = obj
        showVehicleDetailPopUp(true)
    }
}