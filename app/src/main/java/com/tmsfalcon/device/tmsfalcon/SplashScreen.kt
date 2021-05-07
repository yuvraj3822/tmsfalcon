package com.tmsfalcon.device.tmsfalcon

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.KotLogin
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
import java.util.*

/**
 * Created by Android on 6/30/2017.
 */
class SplashScreen : Activity() {
    lateinit var logo: ImageView
    var session: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        //Fabric.with(this, new Crashlytics());
        val ctx = this.applicationContext

        var date = System.currentTimeMillis()
//        1614241198322
//        1614241262817
        Log.e("date check",": "+date.toString())

        // Use the Sentry DSN (client key) from the Project Settings page on Sentry
        val sentryDsn = "https://ae9cd827cef0407d9c8090f1e5549cfd@sentry.io/1404572"
        Sentry.init(sentryDsn, AndroidSentryClientFactory(ctx))
        Log.e("OS", Build.VERSION.RELEASE)
        //Log.e("is64Bit", String.valueOf(android.os.Process.is64Bit()));
        //forceCrash();
        logo = findViewById(R.id.imgLogo)
        session = SessionManager(this@SplashScreen)
        val animation1 = AnimationUtils.loadAnimation(applicationContext,
                R.anim.fade)
        logo.startAnimation(animation1)
        Handler().postDelayed({ // This method will be executed once the timer is over
            // Start your app main activity
            if (session!!.isLoggedIn) {
                val i = Intent(this@SplashScreen, DashboardActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@SplashScreen, KotLogin::class.java)
                startActivity(i)
            }

            // close this activity
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }

    fun forceCrash() {
        throw RuntimeException("This is a crash")
    }

    companion object {
        // Splash screen timer
        private const val SPLASH_TIME_OUT = 2200
    }
}