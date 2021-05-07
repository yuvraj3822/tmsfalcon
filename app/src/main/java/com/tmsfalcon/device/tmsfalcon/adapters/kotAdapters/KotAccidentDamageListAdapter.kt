package com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.DamageDetails
import com.tmsfalcon.device.tmsfalcon.entities.AccidentDamageDetailsModel
import com.tmsfalcon.device.tmsfalcon.interfacess.clickDamage
import com.tmsfalcon.device.tmsfalcon.interfacess.deleteDamageDetails
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Android on 8/23/2017.
 */

class KotAccidentDamageListAdapter(private val activity: Activity,var list:ArrayList<DamageDetails>,var deleteDamageDetails: deleteDamageDetails,var clickDamage: clickDamage) : BaseAdapter() {
    private val mInflater: LayoutInflater
    var sessionManager: SessionManager
    var holder: ViewHolder? = null
    override fun getCount(): Int {
        return list.size
        //return mData.size();
    }

    override fun getItem(position: Int): Any {
//        return mData[position]
          return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val type = getItemViewType(position)

        if (convertView == null) {
            holder = ViewHolder()
            convertView = mInflater.inflate(R.layout.list_item_accident_damage, null)



            holder!!.cancel_icon = convertView.findViewById(R.id.cancel_icon)
            holder!!.name = convertView.findViewById(R.id.name)
            holder!!.emailAddress = convertView.findViewById(R.id.email_id)
            holder!!.phoneNo = convertView.findViewById(R.id.phone_no)
            holder!!.address = convertView.findViewById(R.id.address)
            holder!!.detail_layout = convertView.findViewById(R.id.detail_layout)

            holder!!.damage_heading = convertView.findViewById(R.id.damage_heading)




            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        holder.apply {
            setTextOnTextView(this!!.damage_heading!!,this!!.name!!, this!!.emailAddress!!, this!!.phoneNo!!, this!!.address!!,position)
            cancel_icon!!.setOnClickListener {
                deleteDamageDetails.delteteData(list[position])
            }
            detail_layout?.setOnClickListener {
                clickDamage.updateDamage(list[position])
            }
        }



        return convertView!!
    }


    fun setStrText(tView:TextView,str:String){
        tView.setText(str)
    }

    fun setTextOnTextView(damageHeading:TextView,name:TextView,emailAddress:TextView,phnNo:TextView,address:TextView,pos:Int){
        if (list[pos].firstName!=null){
           if (list[pos].firstName.isNotEmpty() && !list[pos].firstName.equals("null",ignoreCase = true)) setStrText(name,list[pos].firstName) else setStrText(name,"")
        } else {
            Log.e("error","data particular item null")
        }

        if (list[pos].address!=null){
            if (list[pos].address.isNotEmpty() && !list[pos].address.equals("null",ignoreCase = true)) setStrText(address,list[pos].address) else setStrText(address,"")
        } else {
            Log.e("error","data particular item null")
        }

        if (list[pos].email!=null){
            if (list[pos].email.isNotEmpty() && !list[pos].email.equals("null",ignoreCase = true)) setStrText(emailAddress,list[pos].email) else setStrText(emailAddress,"")
        } else {
            Log.e("error","data particular item null")
        }

        if (list[pos].phoneNo!=null){
            if (list[pos].phoneNo.isNotEmpty() && !list[pos].phoneNo.equals("null",ignoreCase = true)) setStrText(phnNo,list[pos].phoneNo) else setStrText(phnNo,"")
        } else {
            Log.e("error","data particular item null")
        }

        if(list[pos].injuryType!=null){
            if (list[pos].injuryType.isNotEmpty() && !list[pos].injuryType.equals("null",ignoreCase = true)) setStrText(damageHeading,list[pos].injuryType+" Report") else setStrText(damageHeading,"")
        }else {
            Log.e("error","data particular item null")

        }

    }

    class ViewHolder {


        var damage_heading: TextView? = null
        var name: TextView? = null
        var phoneNo: TextView? = null
        var emailAddress: TextView? = null
        var address: TextView? = null
        var cancel_icon: ImageView? = null

        var detail_layout:LinearLayout? = null

    }

    init {
        mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        sessionManager = SessionManager(activity)
    }

}