package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatCheckBox
import butterknife.ButterKnife
import butterknife.OnClick
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R

import com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters.KotOtherAccidentListAdapter
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.MultipleOtherVehicleDetails
import com.tmsfalcon.device.tmsfalcon.interfacess.OtherAccidetntClickDetails
import com.tmsfalcon.device.tmsfalcon.interfacess.clickEventForUpdate
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.activity_other_party_vehicle_details.list_view_vehicles
import kotlinx.android.synthetic.main.activity_other_party_vehicle_details.message
import kotlinx.android.synthetic.main.activity_other_party_vehicle_details.next_btn
import kotlinx.android.synthetic.main.dialog_other_party_accident_vehicle_detail.*
import kotlinx.android.synthetic.main.dialog_other_party_accident_vehicle_detail.view.*
import kotlinx.android.synthetic.main.other_insurance_view.*
import kotlinx.android.synthetic.main.other_insurance_view.view.*
import kotlinx.android.synthetic.main.other_towing_details.*
import kotlinx.android.synthetic.main.other_towing_details.view.*

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class KotOtherPartyVehicleDetails : NavigationBaseActivity(), OtherAccidetntClickDetails,clickEventForUpdate {

    var vehicle_insurance_provider_edittext: EditText? = null
    var vehicle_insurance_no_edittext: EditText? = null
    var vehicle_dot_number_edittext: EditText? = null
    var vehicle_license_no_edittext: EditText? = null
    var vehicle_registration_no_edittext: EditText? = null
    lateinit var vehicleAlertDialog: AlertDialog
    var incidentDateListener: DatePickerDialog.OnDateSetListener? = null

    var vehicle_owner_name_edittext: EditText? = null
    var vehicle_owner_contact_number_edittext: EditText? = null
    var vehicle_owner_insurance_provider_edittext: EditText? = null
    var vehicle_owner_insurance_policy_no_edittext: EditText? = null

    //    var db: AccidentBasicDetails? = null
    lateinit var db:AccidentModuleDb
    var sessionManager: SessionManager? = null
    var vehicleDetailArrayList: ArrayList<MultipleOtherVehicleDetails> = ArrayList()
    var isFormValid = false
    var customValidator: CustomValidator? = null
    var error_vehicle_insurance_provider: TextView? = null
    var error_vehicle_insurance_no: TextView? = null
    var error_vehicle_license_no: TextView? = null
    var popUpOpened = false
    lateinit var is_towed_checkbox: AppCompatCheckBox
    lateinit var tow_block: LinearLayout
    lateinit var scroll_view: ScrollView
    var vehicleListAdapter: KotOtherAccidentListAdapter? = null
    lateinit var owner_dob:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_other_party_vehicle_details, null, false)
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

    fun setTextOnNextBtn(list:List<MultipleOtherVehicleDetails>){
        CoroutineScope(Main).launch {
            if (list.isEmpty()){
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
    fun checkAndSetTheData(){
        CoroutineScope(IO).launch {
            var tempList = db.movdDao().getOtherVehicleDetailList()
            if (tempList!=null){
                if (tempList.size>0){
                    vehicleDetailArrayList.clear()

                    var reverseList =  tempList.reversed()
                    vehicleDetailArrayList.addAll(reverseList)
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
        CoroutineScope(Main).launch {
            vehicleListAdapter!!.notifyDataSetChanged()
        }
    }

    /**
     * initiialise adapter
     */
    fun intitiliseAdapter(){
//        visibilityMainView(View.VISIBLE)

        vehicleListAdapter = KotOtherAccidentListAdapter(this@KotOtherPartyVehicleDetails,vehicleDetailArrayList,this@KotOtherPartyVehicleDetails,this@KotOtherPartyVehicleDetails)
        list_view_vehicles!!.adapter = vehicleListAdapter

    }



    fun initIds() {
//        db = AccidentBasicDetails(this@KotOtherPartyVehicleDetails)
        db = AccidentModuleDb.getDatabase(this@KotOtherPartyVehicleDetails)
        sessionManager = SessionManager()
        customValidator = CustomValidator(this@KotOtherPartyVehicleDetails)

        incidentDateListener = DatePickerDialog.OnDateSetListener { datePickerDialog, year, monthOfYear, dayOfMonth ->
            val date = (monthOfYear + 1).toString() + "/" + dayOfMonth + "/" + year
            vehicleAlertDialog.owner_dob!!.text = date
        }

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
            if (!customValidator!!.setRequired(vehicle_insurance_no_edittext!!.text.toString())) {
                error_vehicle_insurance_no!!.visibility = View.VISIBLE
                isFormValid = false
            } else {
                error_vehicle_insurance_no!!.visibility = View.GONE
                isFormValid = true
            }
            if (!customValidator!!.setRequired(vehicle_license_no_edittext!!.text.toString())) {
                error_vehicle_license_no!!.visibility = View.VISIBLE
                isFormValid = false
            } else {
                error_vehicle_license_no!!.visibility = View.GONE
                isFormValid = true
            }
        }
        return isFormValid

    }


    fun emptyErrorMessage(str:String):Boolean{
        Toast.makeText(this@KotOtherPartyVehicleDetails,str,Toast.LENGTH_SHORT).show()
        return false
    }

    fun validationForDialogElements():Boolean{

        if (vehicleAlertDialog.vehicle_unit_no.text.toString().trim().isEmpty())   return emptyErrorMessage("Please enter vehicle unit no")
        else if (vehicleAlertDialog.vehicle_year.text.toString().trim().isEmpty())  return emptyErrorMessage("Please enter vehicle year")
        else if (vehicleAlertDialog.vehicle_make.text.toString().trim().isEmpty())  return emptyErrorMessage("Please enter vehicle make")
        else if (vehicleAlertDialog.vehicle_license_no.text.toString().trim().isEmpty())   return emptyErrorMessage("Please enter license no")
        else if (vehicleAlertDialog.vehicle_license_state.text.toString().trim().isEmpty())   return emptyErrorMessage("Please enter license state")
        else if (vehicleAlertDialog.automob_insurancename.text.toString().trim().isEmpty())   return emptyErrorMessage("Please enter insurance company name")
        else if (vehicleAlertDialog.vehicle_insurance_no.text.toString().trim().isEmpty())       return emptyErrorMessage("Please enter insurance policy no")
        else if (vehicleAlertDialog.policy_holder_phone_no.text.toString().trim().isEmpty())    return emptyErrorMessage("Please enter policy holder first name")
        else if (vehicleAlertDialog.policy_holder_email.text.toString().trim().isEmpty()) return emptyErrorMessage("Please enter policy holder last name")
        else if (vehicleAlertDialog.owner_first_name.text.toString().trim().isEmpty())  return emptyErrorMessage("Please enter owner first name")
        else if (vehicleAlertDialog.owner_phone_no.text.toString().trim().isEmpty())   return emptyErrorMessage("Please enter owner last name")
        else if (vehicleAlertDialog.owner_phone_no.text.toString().trim().isEmpty())  return emptyErrorMessage("Please enter owner phone no")
        else if (vehicleAlertDialog.owner_dob.text.toString().trim().isEmpty())  return emptyErrorMessage("Please enter owner data of birth")
        else if (vehicleAlertDialog.owner_city.text.toString().trim().isEmpty())    return emptyErrorMessage("Please enter owner city")
        else if (vehicleAlertDialog.owner_state.text.toString().trim().isEmpty())   return emptyErrorMessage("Please enter owner state")
        else if (vehicleAlertDialog.owner_zipcode.text.toString().trim().isEmpty())   return emptyErrorMessage("Please enter owner zip code")
        else if (vehicleAlertDialog.is_towed_checkbox.isChecked){
            if (vehicleAlertDialog.towing_company_name.text.toString().trim().isEmpty())      return emptyErrorMessage("Please enter towing company name")
            else if (vehicleAlertDialog.towing_company_phone.text.toString().trim().isEmpty()) return emptyErrorMessage("Please enter towing phone no")
            else return true
        }
        else {
            return true
        }

    }


    fun showVehicleDetailPopUp(isUpdate:Boolean) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_other_party_accident_vehicle_detail, null)
        dialogBuilder.setView(dialogView)
        popUpOpened = true
        val zoomLinearLayout: ZoomLinearLayout = dialogView.findViewById(R.id.zoom_linear_layout)
        zoomLinearLayout.init(this@KotOtherPartyVehicleDetails)
        is_towed_checkbox = dialogView.findViewById(R.id.is_towed_checkbox)
        scroll_view = dialogView.findViewById(R.id.scroll_view)
        tow_block = dialogView.findViewById(R.id.tow_block)
        owner_dob = dialogView.findViewById(R.id.owner_dob)

        dialogView.owner_phone_no.setText("")
        dialogView.policy_holder_phone_no.setText("")
        dialogView.towing_company_phone.setText("")

        dialogView.insurance_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (b) dialogView.other_insurance_view.visibility = View.VISIBLE else {
                dialogView.other_insurance_view.visibility = View.GONE
                dialogView.automob_insurancename.setText("")
                dialogView.vehicle_insurance_no.setText("")
                dialogView.policy_holder_phone_no.setText("")
                dialogView.policy_holder_email.setText("")
            }
        }




        is_towed_checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) { //checked
                scroll_view.post(Runnable { scroll_view.smoothScrollTo(0, scroll_view.getBottom()) })
                tow_block.setVisibility(View.VISIBLE)
            } else {
                tow_block.setVisibility(View.GONE)
            }
        })

        val add_btn = dialogView.findViewById<Button>(R.id.add_vehicle_detail)
//        val vehicleUnitNo = dialogView.findViewById<EditText>(R.id.vehicle_unit_no)


        /**
         * check for update
         */

        if (isUpdate){
            if (updateVehicleDetailObj!=null){
                    add_btn.setText(resources.getString(R.string.save))
                    dialogView.vehicle_unit_no.setText(updateVehicleDetailObj.unitNo)
                    dialogView.vehicle_year.setText(updateVehicleDetailObj.vehicleYear)
                    dialogView.vehicle_make.setText(updateVehicleDetailObj.vehicleMake)
                    dialogView.vehicle_license_no.setText(updateVehicleDetailObj.licenseNos)
                    dialogView.vehicle_license_state.setText(updateVehicleDetailObj.licenseState)
                    dialogView.insurance_checkbox.isChecked = updateVehicleDetailObj.isInsurance
                    dialogView.automob_insurancename.setText(updateVehicleDetailObj.insuranceCompany)
                    dialogView.vehicle_insurance_no.setText(updateVehicleDetailObj.insurancePolicyNo)
                    dialogView.policy_holder_phone_no.setText(updateVehicleDetailObj.policyHolderFirstName)
                    dialogView.policy_holder_email.setText(updateVehicleDetailObj.policyHolderLastName)
                    dialogView.owner_first_name.setText(updateVehicleDetailObj.ownerName)
                    dialogView.owner_phone_no.setText(updateVehicleDetailObj.ownerPhoneno)
                    dialogView.owner_address.setText(updateVehicleDetailObj.ownerAddress)
//                    dialogView.owner_dob.setText(updateVehicleDetailObj.ownerDob)
                    dialogView.owner_state.setText(updateVehicleDetailObj.ownerState)
                    dialogView.owner_zipcode.setText(updateVehicleDetailObj.ownerZipCode)
                    dialogView.is_towed_checkbox.isChecked = updateVehicleDetailObj.isVehicleTowedAway
                    dialogView.towing_company_name.setText(updateVehicleDetailObj.towedCompanyName)
                    dialogView.towing_company_phone.setText(updateVehicleDetailObj.towedCompanyPhnNo)
            }
        }


        dialogView.insurance_checkbox.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                dialogView.other_insurance_view.visibility = View.VISIBLE
            } else {
                dialogView.other_insurance_view.visibility = View.GONE
            }
        }

        add_btn.setOnClickListener {

            if (dialogView.policy_holder_email.text.toString().isNotEmpty()) {
                if (!isValidEmail(dialogView.policy_holder_email.text.toString().trim())) {
                    Toast.makeText(this@KotOtherPartyVehicleDetails, "Incorrect email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
                }
            }


            CoroutineScope(IO).launch {

                var no:Int = 0
                var retrieveLastItem = db.movdDao().getOtherVehicleDetailList()
                if (retrieveLastItem.size>0){
                    var value =  retrieveLastItem.get(retrieveLastItem.size-1)
                        if (isUpdate) no = updateVehicleDetailObj.id!! else  no = value.id!!+1
                }

                var detailOtherVehicle =  MultipleOtherVehicleDetails(
                        no,
                        vehicleAlertDialog.vehicle_unit_no.text.toString(),
                        vehicleAlertDialog.vehicle_year.text.toString(),
                        vehicleAlertDialog.vehicle_make.text.toString(),
                        vehicleAlertDialog.vehicle_license_no.text.toString(),
                        vehicleAlertDialog.vehicle_license_state.text.toString(),


                        vehicleAlertDialog.owner_first_name.text.toString(),
                        vehicleAlertDialog.owner_phone_no.text.toString(),
                        vehicleAlertDialog.owner_address.text.toString(),
                        vehicleAlertDialog.owner_city.text.toString(),
                        vehicleAlertDialog.owner_state.text.toString(),
                        vehicleAlertDialog.owner_zipcode.text.toString(),


                        vehicleAlertDialog.insurance_checkbox.isChecked,
                        vehicleAlertDialog.automob_insurancename.text.toString(),
                        vehicleAlertDialog.vehicle_insurance_no.text.toString(),
                        vehicleAlertDialog.policy_holder_phone_no.text.toString(),
                        vehicleAlertDialog.policy_holder_email.text.toString(),
                        vehicleAlertDialog.owner_first_name.text.toString(),
                        vehicleAlertDialog.owner_phone_no.text.toString(),
                        vehicleAlertDialog.owner_address.text.toString(),
                        vehicleAlertDialog.owner_city.text.toString(),
                        vehicleAlertDialog.owner_state.text.toString(),
                        vehicleAlertDialog.owner_zipcode.text.toString(),
                        vehicleAlertDialog.is_towed_checkbox.isChecked,
                        vehicleAlertDialog.towing_company_name.text.toString(),
                        vehicleAlertDialog.towing_company_phone.text.toString())

                /**
                 * check either to
                 * update or add the data
                 */


                db.movdDao().insert(detailOtherVehicle)
            //    if (isUpdate) db.movdDao().updateQuerry(detailOtherVehicle) else db.movdDao().insert(detailOtherVehicle)

                CoroutineScope(Main).launch {
                    vehicleAlertDialog!!.dismiss()
                    checkAndSetTheData()
                }
            }
        }

        owner_dob.setOnClickListener {
            clickListener()
        }
        val cancel_btn = dialogView.findViewById<Button>(R.id.cancel_btn)
        cancel_btn.setOnClickListener { vehicleAlertDialog!!.dismiss() }
        vehicleAlertDialog = dialogBuilder.create()
        //emailAlertDialog.setTitle("Send Documents By Email");
        vehicleAlertDialog.show()
        vehicleAlertDialog.getWindow()?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
    fun clickListener(){
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                incidentDateListener,
                now[Calendar.YEAR],
                now[Calendar.MONTH],
                now[Calendar.DAY_OF_MONTH]
        )
        dpd.show(this@KotOtherPartyVehicleDetails.fragmentManager, "DatePickerDialog")

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
                    val str6 = vehicle_insurance_no_edittext!!.text.toString()
                    if (str6 !== "" && str6.length > 0) {
                        error_vehicle_insurance_no!!.visibility = View.GONE
                    } else {
                        error_vehicle_insurance_no!!.visibility = View.VISIBLE
                    }
                }
                R.id.vehicle_license_no -> {
                    val str7 = vehicle_insurance_no_edittext!!.text.toString()
                    if (str7 !== "" && str7.length > 0) {
                        error_vehicle_license_no!!.visibility = View.GONE
                    } else {
                        error_vehicle_license_no!!.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    @OnClick(R.id.next_btn)
    fun nextActivity() {
//        val i = Intent(this@KotOtherPartyVehicleDetails, KotAccidentDamageDetails::class.java)
//        startActivity(i)

        CoroutineScope(IO).launch {
            var list  = db.movdDao().getOtherVehicleDetailList()
            if (list.isEmpty()) {
//                CoroutineScope(Main).launch {
//                    Toast.makeText(this@KotOtherPartyVehicleDetails,getString(R.string.please_enter_the_vehicle_details),Toast.LENGTH_SHORT).show()
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
        sessionManager?.saveStatusOtherBasicPref()
    }
    fun saveTextStatus() {
        if(next_btn.text.toString().equals("Done",ignoreCase = true)){
            sessionManager?.saveStatusOtherTextBasicPref()
        }else {
            sessionManager?.clearTextOtherBasicPref()
        }
    }

    fun clearTextStatus(){
        sessionManager?.clearTextOtherBasicPref()
    }


    @OnClick(R.id.add_vehicle_detail)
    fun callFunc() {
        showVehicleDetailPopUp(false)
    }

    override fun otherAccidentEvent(obj:MultipleOtherVehicleDetails) {

        AlertDialog.Builder(this@KotOtherPartyVehicleDetails,R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Alert!!")
                .setMessage("Are you sure you want to delete this info?") // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        // Continue with delete operation
                        dialog!!.dismiss()
                        CoroutineScope(IO).launch {
                            db.movdDao().deleteOhterSingleVehicleDetail(obj)
                            CoroutineScope(Main).launch {
                                vehicleDetailArrayList.remove(obj)
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

    lateinit var updateVehicleDetailObj:MultipleOtherVehicleDetails

    override fun updateEventClickInterface(obj:MultipleOtherVehicleDetails) {
        updateVehicleDetailObj = obj
        showVehicleDetailPopUp(true)
    }
}