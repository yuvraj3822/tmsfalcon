package com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.MultipleVehicleDetails
import com.tmsfalcon.device.tmsfalcon.interfacess.clickEventOtherUpdate
import com.tmsfalcon.device.tmsfalcon.interfacess.deleteDriverDetails

/**
 * Created by Android on 8/23/2017.
 */
class KotAccidentVehicleListAdapter(private val activity: Activity, var data: List<MultipleVehicleDetails>,
                                    var deleteDriverDetails: deleteDriverDetails,var clickEventOtherUpdate: clickEventOtherUpdate) : BaseAdapter() {

    private val mInflater: LayoutInflater
    var sessionManager: SessionManager
    var holder: ViewHolder? = null
    override fun getCount(): Int {
        return data.size
        //return mData.size();
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val type = getItemViewType(position)

        //final AccidentVehicleDetailsModel model = mData.get(position);
        if (convertView == null) {
            holder = ViewHolder()
            convertView = mInflater.inflate(R.layout.list_item_accident_vehicle, null)

            holder!!.detailAddress = convertView.findViewById(R.id.address_detail)
            holder!!.cancel_icon = convertView.findViewById(R.id.cancel_icon)
            holder!!.trailer_no = convertView.findViewById(R.id.trailer_no)
            holder!!.dotNo = convertView.findViewById(R.id.dotno)
            holder!!.licnesePlate = convertView.findViewById(R.id.license_no)
            holder!!.licenseState = convertView.findViewById(R.id.license_state)
            holder!!.companyName = convertView.findViewById(R.id.company_names)
            holder!!.policyNo = convertView.findViewById(R.id.policy_no)
            holder!!.policyHolderPhoneNo = convertView.findViewById(R.id.policy_holder_mobno)
            holder!!.ownerName = convertView.findViewById(R.id.owner_name)
            holder!!.ownerPhoneNo = convertView.findViewById(R.id.phone_no)
            holder!!.ownerAddress = convertView.findViewById(R.id.address)
            holder!!.towingCompanyName = convertView.findViewById(R.id.towing_company_name)
            holder!!.towingPhoneNo = convertView.findViewById(R.id.towing_phone_no)
            holder!!.left_company_name = convertView.findViewById(R.id.left_company_name)
            holder!!.left_policy_no = convertView.findViewById(R.id.left_policy_no)
            holder!!.left_mob_no = convertView.findViewById(R.id.left_mob_no)
            holder!!.trailer_view = convertView.findViewById(R.id.trailer_view)
            holder!!.company_name_trailer = convertView.findViewById(R.id.company_name_trailer)
            holder!!.policy_no_trailer = convertView.findViewById(R.id.policy_no_trailer)
            holder!!.mob_no_trailer = convertView.findViewById(R.id.mob_no_trailer)
            holder!!.truckordriver_view = convertView.findViewById(R.id.truckordriver_view)
            holder!!.detail_layout = convertView.findViewById(R.id.detail_layout)



            convertView.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
        }


//        if (data.get(position).dotNo != null) {
//            if (data.get(position).dotNo.equals("") || data.get(position).dotNo.equals("null", ignoreCase = true)) {
//                holder!!.dotNo!!.setText("")
//            } else {
//                holder!!.dotNo!!.setText(data[position].dotNo)
//            }
//        }


        if (data.get(position).licenseNos != null) {
            if (data.get(position).licenseNos.equals("") || data.get(position).licenseNos.equals("null", ignoreCase = true)) {
                holder!!.licnesePlate!!.setText("")
            } else {
                holder!!.licnesePlate!!.setText(data[position].licenseNos)
            }
        }

        if (data.get(position).licenseState != null) {
            if (data.get(position).licenseState.equals("") || data.get(position).licenseState.equals("null", ignoreCase = true)) {
                holder!!.licenseState!!.setText("")
            } else {
                holder!!.licenseState!!.setText(data[position].licenseState)
            }
        }

//        if (data.get(position).dotNo != null) {
//            if (data.get(position).dotNo.equals("") || data.get(position).dotNo.equals("null", ignoreCase = true)) {
//                holder!!.dotNo!!.setText("")
//            } else {
//                holder!!.dotNo!!.setText(data[position].dotNo)
//            }
//        }





        if (data.get(position).insuranceClaimByDriverOrTruck != null) {

            if (data.get(position).insuranceClaimByDriverOrTruck.equals(activity.resources.getString(R.string.truck), ignoreCase = true)) {
                holder!!.left_company_name?.setText(activity.resources.getString(R.string.company_name) + " (Truck)")
                holder!!.left_policy_no?.setText(activity.resources.getString(R.string.policy_no) + " (Truck)")
                holder!!.left_mob_no?.setText(activity.resources.getString(R.string.policy_holder_mobno) + " (Truck)")
            } else if (data.get(position).insuranceClaimByDriverOrTruck.equals(activity.resources.getString(R.string.driver), ignoreCase = true)) {
                holder!!.left_company_name?.setText(activity.resources.getString(R.string.company_name) )
                holder!!.left_policy_no?.setText(activity.resources.getString(R.string.policy_no) )
                holder!!.left_mob_no?.setText(activity.resources.getString(R.string.policy_holder_mobno) )
            }

        }


        /**
         * set Text for truck or driver
         */


        if (data.get(position).insuranceCompany != null) {
            if (data.get(position).insuranceCompany.equals("") || data.get(position).insuranceCompany.equals("null", ignoreCase = true)) {
                holder!!.companyName!!.setText("")
            } else {
                holder!!.companyName!!.setText(data[position].insuranceCompany)
            }
        }

        if (data.get(position).insurancePolicyNo != null) {
            if (data.get(position).insurancePolicyNo.equals("") || data.get(position).insurancePolicyNo.equals("null", ignoreCase = true)) {
                holder!!.policyNo!!.setText("")
            } else {
                holder!!.policyNo!!.setText(data[position].insurancePolicyNo)
            }
        }


        if (data.get(position).policyHolderPhoneNo != null) {
            if (data.get(position).policyHolderPhoneNo.equals("") || data.get(position).policyHolderPhoneNo.equals("null", ignoreCase = true)) {
                holder!!.policyHolderPhoneNo!!.setText("")
            } else {
                holder!!.policyHolderPhoneNo!!.setText(data[position].policyHolderPhoneNo)
            }
        }


        /**
         * set Text for trailer
         */


        if (data.get(position).trailerInsuranceCompany != null) {
            if (data.get(position).trailerInsuranceCompany.equals("") || data.get(position).trailerInsuranceCompany.equals("null", ignoreCase = true)) {
                holder!!.company_name_trailer!!.setText("")
            } else {
                holder!!.company_name_trailer!!.setText(data[position].trailerInsuranceCompany)
            }
        }

        if (data.get(position).trailerInsurancePolicyNo != null) {
            if (data.get(position).trailerInsurancePolicyNo.equals("") || data.get(position).trailerInsurancePolicyNo.equals("null", ignoreCase = true)) {
                holder!!.policy_no_trailer!!.setText("")
            } else {
                holder!!.policy_no_trailer!!.setText(data[position].trailerInsurancePolicyNo)
            }
        }


        if (data.get(position).trailerPolicyHolderPhoneNo != null) {
            if (data.get(position).trailerPolicyHolderPhoneNo.equals("") || data.get(position).trailerPolicyHolderPhoneNo.equals("null", ignoreCase = true)) {
                holder!!.mob_no_trailer!!.setText("")
            } else {
                holder!!.mob_no_trailer!!.setText(data[position].trailerPolicyHolderPhoneNo)
            }
        }


        /**
         * setTrailer view visible as per data is present
         */

        if (data.get(position).trailerInsuranceCompany != null) {
            if (data.get(position).trailerInsuranceCompany.equals("") || data.get(position).trailerInsuranceCompany.equals("null", ignoreCase = true)) {
                holder!!.trailer_view!!.visibility = View.GONE
            } else {
                holder!!.trailer_view!!.visibility = View.VISIBLE
            }
        }


        /**
         * setTruck or driver view as per data available
         */

        if (data.get(position).insuranceCompany != null) {
            if (data.get(position).insuranceCompany.equals("") || data.get(position).insuranceCompany.equals("null", ignoreCase = true)) {
                holder!!.truckordriver_view!!.visibility = View.GONE
            } else {
                holder!!.truckordriver_view!!.visibility = View.VISIBLE
            }
        }



        if (data.get(position).ownerName != null) {
            if (data.get(position).ownerName.equals("") || data.get(position).ownerName.equals("null", ignoreCase = true)) {
                holder!!.ownerName!!.setText("")
            } else {
                holder!!.ownerName!!.setText(data[position].ownerName)
            }
        }


        if (data.get(position).ownerPhnNo != null) {
            if (data.get(position).ownerPhnNo.equals("") || data.get(position).ownerPhnNo.equals("null", ignoreCase = true)) {
                holder!!.ownerPhoneNo!!.setText("")
            } else {
                holder!!.ownerPhoneNo!!.setText(data[position].ownerPhnNo)
            }
        }


        if (data.get(position).ownerAddress != null) {
            if (data.get(position).ownerAddress.equals("") || data.get(position).ownerAddress.equals("null", ignoreCase = true)) {
                holder!!.detailAddress!!.setText("")
            } else {
                holder!!.detailAddress!!.setText(data[position].ownerCity+", "+data[position].ownerState+", "+data[position].ownerZipCode)
            }
        }


        if (data.get(position).ownerCity!=null){
            if (data.get(position).ownerCity.equals("") || data.get(position).ownerCity.equals("null", ignoreCase = true)) {
                holder!!.ownerAddress!!.setText("")
            } else {
                holder!!.ownerAddress!!.setText(data.get(position).ownerAddress)
            }
        }


        if (data.get(position).towedCompanyName != null) {
            if (data.get(position).towedCompanyName.equals("") || data.get(position).towedCompanyName.equals("null", ignoreCase = true)) {
                holder!!.towingCompanyName!!.setText("")
            } else {
                holder!!.towingCompanyName!!.setText(data[position].towedCompanyName)
            }
        }



        if (data.get(position).towedCompanyPhnNo != null) {
            if (data.get(position).towedCompanyPhnNo.equals("") || data.get(position).towedCompanyPhnNo.equals("null", ignoreCase = true)) {
                holder!!.towingPhoneNo!!.setText("")
            } else {
                holder!!.towingPhoneNo!!.setText(data[position].towedCompanyPhnNo)
            }
        }



        if (data.get(position).trailerNo != null) {
            if (data.get(position).trailerNo.equals("") || data.get(position).trailerNo.equals("null", ignoreCase = true)) {
                holder!!.trailer_no!!.setText("")
            } else {
                holder!!.trailer_no!!.setText(data[position].trailerNo)
            }
        }

        holder!!.cancel_icon?.setOnClickListener {
            deleteDriverDetails.delteteData(data.get(position))
        }

        holder!!.detail_layout?.setOnClickListener {
            clickEventOtherUpdate.updateOtherData(data.get(position))
        }




        return convertView!!
    }


    class ViewHolder {
        var detailAddress: TextView? = null
        var dotNo: TextView? = null
        var licnesePlate: TextView? = null
        var licenseState: TextView? = null
        var companyName: TextView? = null
        var policyNo: TextView? = null
        var policyHolderPhoneNo: TextView? = null
        var ownerName: TextView? = null
        var ownerPhoneNo: TextView? = null
        var ownerAddress: TextView? = null
        var towingCompanyName: TextView? = null
        var towingPhoneNo: TextView? = null
        var trailer_no: TextView? = null
        var cancel_icon: ImageView? = null
        var left_company_name: TextView? = null
        var left_policy_no: TextView? = null
        var left_mob_no: TextView? = null
        var trailer_view: LinearLayout? = null
        var company_name_trailer: TextView? = null
        var policy_no_trailer: TextView? = null
        var mob_no_trailer: TextView? = null
        var truckordriver_view: LinearLayout? = null
        var detail_layout:LinearLayout? = null



    }

    /* public AccidentVehicleListAdapter(Activity activity, ArrayList<AccidentVehicleDetailsModel> data) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
    }*/
    init {
        mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        sessionManager = SessionManager(activity)
    }
}