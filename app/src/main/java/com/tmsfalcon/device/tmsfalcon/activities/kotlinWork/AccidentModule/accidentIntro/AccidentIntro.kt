package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.accidentIntro

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.AccidentBasicFormDataK
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.ReportedAccidentListK
import kotlinx.android.synthetic.main.activity_accident_intro.*

class AccidentIntro : NavigationBaseActivity() , ViewPager.OnPageChangeListener, View.OnClickListener{

    var currentPageFragNo:Int = 0
    val TAG = this@AccidentIntro::class.simpleName
    var BACK = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayoutAndDrawer()
        setUpWidgets()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val returnIntent = Intent()
        returnIntent.putExtra("result", "result")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun setLayoutAndDrawer(){
        /**
         * Setup base activity
         */
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_accident_intro, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this@AccidentIntro)
    }


    fun setUpWidgets(){
        vpPager.adapter = IntroPagerAdapter(supportFragmentManager)
        tab_layout.setupWithViewPager(vpPager)
        vpPager.addOnPageChangeListener(this)
        skip.setOnClickListener(this@AccidentIntro)
        next.setOnClickListener(this@AccidentIntro)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        currentPageFragNo = position
        visibilityBottomNavigation(position)
    }


    fun visibilityBottomNavigation(position: Int){
        if (position == 4){
            next.text = "Got it!!"
            skip.visibility = View.GONE

        } else {
            next.text = "Next"
            skip.visibility = View.VISIBLE
        }
    }

    override fun onClick(p0: View?) {
        if (p0!!.id == skip.id){
            navigateToNextScreen()
        }else if (p0!!.id == next.id){
           currentPageFragNo++
            Log.e(TAG,"onNextClick: $currentPageFragNo")
            if(currentPageFragNo == 5){
                navigateToNextScreen()
            }else {
                vpPager.currentItem = currentPageFragNo
            }
        }
    }

    fun navigateToNextScreen(){
        var intent = Intent(this@AccidentIntro,AccidentBasicFormDataK::class.java)
        startActivityForResult(intent,BACK)
    }
}