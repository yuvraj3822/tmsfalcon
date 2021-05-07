package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import butterknife.ButterKnife
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.activities.*
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.accidentIntro.AccidentIntro
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import kotlinx.android.synthetic.main.activity_progress_intro_screen.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class ProgressIntroScreen : NavigationBaseActivity() {

    var ACCIDENT_REPORT = 1
    var ACCIDENT_OTHER_REPORT = 2
    var DAMAGE_DETAILS = 3
    var WITNESS_DETAILS = 4
    var PHOTOGRAPHIC_DETAILS = 5
    var SIGN_REPORT = 6
    var UPLOADED_REPORT = 7
    lateinit var sessionManager: SessionManager
    lateinit var db :AccidentModuleDb


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_progress_intro_screen, null, false)

        drawer.addView(contentView, 0)
        ButterKnife.bind(this)

        sessionManager = SessionManager()
        db = AccidentModuleDb.getDatabase(this@ProgressIntroScreen)

        clickListener()
        checkWhetherTheDataIsSave()
        setProgress()
//      highLightButton()

    }


    fun highLightButton(){

        var anim = ObjectAnimator.ofInt(upload_view, "backgroundColor", Color.WHITE, Color.RED,Color.WHITE)
        anim.duration = 1500
        anim.setEvaluator(android.animation.ArgbEvaluator())
        anim.repeatCount = Animation.INFINITE
        anim.start()

    }



    fun setProgress(){
        var totalList = ArrayList<Boolean>()
        var resultList = ArrayList<Boolean>()
        totalList.clear()
        resultList.clear()

        totalList.add(sessionManager.statusBasicPref)
        totalList.add(sessionManager.statusOtherBasicPref)
        totalList.add(sessionManager.statusInjuredBasicPref)
        totalList.add(sessionManager.statusWitnessPref)
        totalList.add(sessionManager.statusPhotographicPref)
        totalList.add(sessionManager.statusSignaturePref)
        totalList.add(sessionManager.statusOfUploadedData)

        for(i in totalList){
            if (i){
                resultList.add(i)
            }
        }

        /**
         * percentageLogic
         */


        var result = (resultList.size.toFloat()/totalList.size.toFloat())*100
        Log.e("seeResult",": $result")

        var finalResult = String.format("%.2f", result).toFloat()
        progress_view.progress = finalResult
        progress_view.progressText = "$finalResult%"

    }


    fun checkWhetherTheDataIsSave(){


        changeTextInTextView(accident_incident_detail,sessionManager.statusTextBasicPref)
        changeTextInTextView(add_vehicles,sessionManager.statusOtherTextBasicPref)
        changeTextInTextView(person_property_injured,sessionManager.statusInjuredTextBasicPref)
        changeTextInTextView(add_witness,sessionManager.statusTextWitnessPref)
        changeTextInTextView(photograph_evidence,sessionManager.statusPhotographicTextPref)


        makeVisibleOrNot(image_report_accident,sessionManager.statusBasicPref)
        makeVisibleOrNot(image_report_other_accident,sessionManager.statusOtherBasicPref)
        makeVisibleOrNot(image_injured,sessionManager.statusInjuredBasicPref)
        makeVisibleOrNot(image_witness,sessionManager.statusWitnessPref)
        makeVisibleOrNot(image_photographic,sessionManager.statusPhotographicPref)
        makeVisibleOrNot(image_sign_report,sessionManager.statusSignaturePref)

        if(sessionManager.statusBasicPref && sessionManager.statusOtherBasicPref &&
                sessionManager.statusInjuredBasicPref && sessionManager.statusWitnessPref &&
                sessionManager.statusPhotographicPref){
        }

        if(sessionManager.statusBasicPref && sessionManager.statusOtherBasicPref &&
                sessionManager.statusInjuredBasicPref && sessionManager.statusWitnessPref &&
                sessionManager.statusPhotographicPref){
            sign_report.alpha = 1.toFloat()
        }

        if(sessionManager.statusBasicPref && sessionManager.statusOtherBasicPref &&
                sessionManager.statusInjuredBasicPref && sessionManager.statusWitnessPref &&
                sessionManager.statusPhotographicPref && sessionManager.statusSignaturePref){

            if(sessionManager.statusOfUploadedData){
                upload_view.visibility = View.GONE
                text_sign.text = "Signed\n&\nUploaded"
                image_sign_report.setImageResource(R.drawable.ok_acc)
                progress_view.visibility = View.GONE
                nested_view.visibility = View.GONE
                succful_view.visibility = View.VISIBLE
                countTimer.start()
            } else {
                upload_view.visibility = View.VISIBLE
                text_sign.text = "Signed \n Not Uploaded"
                image_sign_report.setImageResource(R.drawable.caution)
            }
        }
    }




    var countTimer =object : CountDownTimer(3000,1000){
        override fun onFinish() {
//            TODO send to previous screen
            sessionManager.clearSessionForAccidentDb()
            CoroutineScope(IO).launch {
                db.clearAllTables()
                CoroutineScope(Main).launch {
                    finish()
                }
            }
        }

        override fun onTick(p0: Long) {

        }

    }

    fun makeVisibleOrNot(imageView:ImageView,status:Boolean){
        Log.e("getnameplease",imageView.resources.getResourceEntryName(imageView.id))
        if (status) imageView.visibility = View.VISIBLE   else imageView.visibility = View.GONE
    }

    /**
     * change text if data is not saved
     */
    fun changeTextInTextView(textView: TextView, status:Boolean){
        var textId = textView.resources.getResourceEntryName(textView.id)
        if (textId.equals("accident_incident_detail",ignoreCase = true)){
            if (status){
                textView.setText(getString(R.string.no_accident_report))
            }else {
                textView.setText(getString(R.string.report_accident))
            }
        }else if (textId.equals("add_vehicles",ignoreCase = true)){
            if (status){
                textView.setText(getString(R.string.no_add_vehicle))
            }else {
                textView.setText(getString(R.string.add_vehicle))

            }
        }else if (textId.equals("person_property_injured",ignoreCase = true)){
            if (status){
                textView.setText(getString(R.string.no_injured_person_property_damage))

            }else {
                textView.setText(getString(R.string.injured_person_property_damage))

            }
        }else if (textId.equals("add_witness",ignoreCase = true)){
            if (status){
                textView.setText(getString(R.string.no_witness))

            }else {
                textView.setText(getString(R.string.add_witness))

            }
        }else if (textId.equals("photograph_evidence",ignoreCase = true)){
            if (status){
                textView.setText(getString(R.string.no_photographic_evidence))

            }else {
                textView.setText(getString(R.string.photographic_evidence))

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /**
         * update the screen from the respective screens
         */
        if (requestCode == ACCIDENT_REPORT){
            if (resultCode == Activity.RESULT_OK){
                Log.e("AccidentReport","AccidentReport")
                checkWhetherTheDataIsSave()
                setProgress()
            }
        }else if (requestCode == ACCIDENT_OTHER_REPORT){
            if (resultCode == Activity.RESULT_OK){
                Log.e("OtherParty","OtherParty")
                checkWhetherTheDataIsSave()
                setProgress()
            }
        }else if (requestCode == DAMAGE_DETAILS){
            if (resultCode == Activity.RESULT_OK){
                Log.e("DAMAGE_DETAILS","DAMAGE_DETAILS")

//                damage_text
                checkWhetherTheDataIsSave()
                setProgress()
            }
        }else if (requestCode == WITNESS_DETAILS){
            if (resultCode == Activity.RESULT_OK){
                Log.e("WITNESS_DETAILS","WITNESS_DETAILS")
                checkWhetherTheDataIsSave()
                setProgress()
            }
        }else if (requestCode == PHOTOGRAPHIC_DETAILS){
            if (resultCode == Activity.RESULT_OK){
                Log.e("PHOTOGRAPHIC_DETAILS","PHOTOGRAPHIC_DETAILS")
                checkWhetherTheDataIsSave()
                setProgress()
            }
        }else if (requestCode == SIGN_REPORT){
            if (resultCode == Activity.RESULT_OK){
                Log.e("SIGN_REPORT","SIGN_REPORT")
                checkWhetherTheDataIsSave()
                setProgress()
            }
        }else if (requestCode == UPLOADED_REPORT){
            if (resultCode == Activity.RESULT_OK){
                Log.e("UPLOADED_REPORT","UPLOADED_REPORT")
                checkWhetherTheDataIsSave()
                setProgress()
            }
        }
    }

    fun clickListener(){
        report_accident_view.setOnClickListener {
            var isEmpty = false
            CoroutineScope(IO).launch {
                 isEmpty = db.mvdDao().getVehicleDetailList().isEmpty()

                CoroutineScope(Main).launch {
                    if(isEmpty){
                        var i = Intent(this@ProgressIntroScreen,AccidentIntro::class.java)
                        startActivityForResult(i,ACCIDENT_REPORT)
                    } else {
                        var i = Intent(this@ProgressIntroScreen,AccidentBasicFormDataK::class.java)
                        startActivityForResult(i,ACCIDENT_REPORT)
                    }
                }
            }
        }

        other_accident_view.setOnClickListener {
            var i = Intent(this@ProgressIntroScreen, OtherPartyAccidentDetails::class.java)
            startActivityForResult(i,ACCIDENT_OTHER_REPORT)
        }

        injuredperson.setOnClickListener {
            var i = Intent(this@ProgressIntroScreen,KotAccidentDamageDetails::class.java)
            startActivityForResult(i,DAMAGE_DETAILS)
        }

        witness_report.setOnClickListener {




            var isEmpty = false
            CoroutineScope(IO).launch {
                isEmpty = db.wfaDao().getWitnessFormData().isEmpty()

                CoroutineScope(Main).launch {
                    if(isEmpty){
                        var i = Intent(this@ProgressIntroScreen, ReportedAccidentScreenFive::class.java)
                        startActivityForResult(i,WITNESS_DETAILS)
                    } else {
                        var i = Intent(this@ProgressIntroScreen,KotReportedAccidentScreenFiveFormData::class.java)
                        startActivityForResult(i,WITNESS_DETAILS)
                    }
                }
            }









        }

        photographic_evidence.setOnClickListener {

            var isEmpty = false
            CoroutineScope(IO).launch {
                isEmpty = db.csaDao().getImageListss().isEmpty()

                CoroutineScope(Main).launch {
                    if(isEmpty){

                        var i = Intent(this@ProgressIntroScreen, ReportedAccidentScreenSix::class.java)
                        startActivityForResult(i,PHOTOGRAPHIC_DETAILS)

                    } else {
                        var i = Intent(this@ProgressIntroScreen, KotAccidentCaptureScreen::class.java)
                        startActivityForResult(i,PHOTOGRAPHIC_DETAILS)
                    }
                }
            }





        }

        sign_report.setOnClickListener {
            var i = Intent(this@ProgressIntroScreen, ReportedAccidentScreenSeven::class.java)
            startActivityForResult(i,SIGN_REPORT)

//            if(sessionManager.statusBasicPref && sessionManager.statusOtherBasicPref &&
//                    sessionManager.statusInjuredBasicPref && sessionManager.statusWitnessPref &&
//                    sessionManager.statusPhotographicPref){
//                var i = Intent(this@ProgressIntroScreen, ReportedAccidentScreenSeven::class.java)
//                startActivityForResult(i,SIGN_REPORT)
//            } else {
//                Toast.makeText(this@ProgressIntroScreen,"Please fill out the remaining form\nbefore signing the report",Toast.LENGTH_SHORT).show()
//            }
        }

        upload.setOnClickListener {
            var i = Intent(this@ProgressIntroScreen,KotAccidentFinalScreen::class.java)
            startActivityForResult(i,UPLOADED_REPORT)
        }
    }
}