package com.tmsfalcon.device.tmsfalcon.widgets

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.tmsfalcon.device.tmsfalcon.R

class Alert {
    companion object{
        fun showAlertDialog(context: Context?, title: String?, msg: String?) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton("Ok",object:DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    p0?.dismiss()
                }

            })
            builder.create().show()
        }

        // Alert with Message and ok Click Event
        fun showAlertDialog(context: Context?, title: String?, msg: String?, listener: DialogInterface.OnClickListener?) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context!!, R.style.Theme_AppCompat_Dialog_Alert)
            builder.setTitle(title)
            builder.setMessage(msg)
            builder.setPositiveButton("OK", listener as DialogInterface.OnClickListener?)
            builder.setNegativeButton("Cancel", listener as DialogInterface.OnClickListener?)
            builder.create().show()
        }

    }

}