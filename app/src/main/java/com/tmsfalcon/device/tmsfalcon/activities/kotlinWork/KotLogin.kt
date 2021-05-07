package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.tmsfalcon.device.tmsfalcon.ForgotPassword
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.ShouldUpdatePassword
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity
import com.tmsfalcon.device.tmsfalcon.customtools.*
import com.tmsfalcon.device.tmsfalcon.database.Driver
import com.tmsfalcon.device.tmsfalcon.entities.DriverModel
import kotlinx.android.synthetic.main.login.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Android on 6/30/2017.
 */

class KotLogin : AppCompatActivity(), View.OnClickListener {

    var username_editText: EditText? = null
    var password_ediIext: EditText? = null
    var username: String? = null
    var password: String? = null
    var login_btn: Button? = null
    var forgot_password_btn: Button? = null
    var isLoginValid = false
    var networkValidator: NetworkValidator? = null
    var customValidator: CustomValidator? = null
    var session: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        ButterKnife.bind(this)
        InitIds()
        session!!.storeNotificationCount(session!!.notificationCount)
        login_btn!!.setOnClickListener(this)
        forgot_password_btn!!.setOnClickListener(this)
    }

    fun InitIds() {
        username_editText = findViewById(R.id.input_name)
        password_ediIext = findViewById(R.id.input_password)
        login_btn = findViewById(R.id.login_btn)
        forgot_password_btn = findViewById(R.id.forgot_password)
        networkValidator = NetworkValidator(this@KotLogin)
        customValidator = CustomValidator(this@KotLogin)
        // Session Manager
        session = SessionManager(this@KotLogin)
        val numCameras: Int = Camera.getNumberOfCameras()
        Log.e("camera", "no of camera: $numCameras")
        checkWhetherCameraIsPresent(isCameraAvailable(this@KotLogin))

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.login_btn -> if (networkValidator!!.isNetworkConnected) {
                if (validateLoginForm()) {
                    performLoginAction()
                }
            } else {
                Toast.makeText(this@KotLogin, resources.getString(R.string.network_error), Toast.LENGTH_LONG).show()
            }
            R.id.forgot_password -> {
                val goToForgotPassworScreen = Intent(this@KotLogin, ForgotPassword::class.java)
                startActivity(goToForgotPassworScreen)
            }
        }
    }

    private fun performLoginAction() {
        // Tag used to cancel the request
        val tag_json_obj = "login_tag"
        AppController.first_time_login = 1
        val url = UrlController.LOGIN_URL
        showProgessBar()
        val params: MutableMap<String?, String?> = HashMap()
        params["username"] = username
        params["password"] = password
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(Method.POST,
                url, JSONObject(params as Map<*, *>),
                Response.Listener { response ->
                    var status: Boolean? = null
                    var token: String? = ""
                    val driver_id: Int
                    val should_update_password: Int
                    val uid: String
                    val thumb: String
                    val nick_name: String
                    val full_name: String
                    val type: String
                    val company_id: String
                    val company_name: String
                    val gender: String
                    val dataObj: JSONObject
                    val driver = Driver(this@KotLogin)
                    try {
                        status = response.getBoolean("status")
                    } catch (e: JSONException) {
                        Log.e("exception ", e.toString())
                    }
                    if (status!!) {
                        try {
                            token = response.getString("token")
                            AppController.token = token
                            dataObj = response.getJSONObject("data")
                            driver_id = dataObj.getInt("driver_id")
                            uid = dataObj.getString("uid")
                            gender = dataObj.getString("gender")
                            thumb = dataObj.getString("thumb")
                            nick_name = dataObj.getString("nick_name")
                            should_update_password = dataObj.getInt("should_update_password")
                            full_name = dataObj.getString("full_name")
                            type = dataObj.getString("type")
                            company_id = dataObj.getString("company_id")
                            company_name = dataObj.getString("company_name")

                            session!!.storeEmailID(dataObj.getString("email"))
                            val topicToSubscribe = "$company_id-$type"
                            val topicAll = "$company_id-all"
                            if (should_update_password == 1) {
                                Log.e("thumb", "" + thumb)
                                FirebaseMessaging.getInstance().subscribeToTopic(topicToSubscribe)
                                FirebaseMessaging.getInstance().subscribeToTopic(topicAll)
                                session!!.createLoginSession(driver_id, full_name, nick_name, uid, thumb, token, type, company_name, gender, company_id.toInt())
                                val goToShouldUpdatePasswordScreen = Intent(this@KotLogin, ShouldUpdatePassword::class.java)
                                startActivity(goToShouldUpdatePasswordScreen)
                            } else {
                                /*FirebaseMessaging.getInstance().subscribeToTopic(topicToSubscribe);
                                            FirebaseMessaging.getInstance().subscribeToTopic(topicAll);*/
                                val result = driver.addDriverBasic(DriverModel(driver_id, full_name, should_update_password, nick_name, thumb, uid, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", token))
                                if (result != -1L) {
                                    Toast.makeText(applicationContext, resources.getString(R.string.login_successful), Toast.LENGTH_LONG).show()
                                    AppController.driver_id = driver_id
                                    session!!.createLoginSession(driver_id, full_name, nick_name, uid, thumb, token, type, company_name, gender, company_id.toInt())
                                    //GpsApiCalls.callFirstApi(Login.this);
                                    val goToDashBoard = Intent(this@KotLogin, DashboardActivity::class.java)
                                    startActivity(goToDashBoard)
                                } else {
                                    Toast.makeText(applicationContext, resources.getString(R.string.login_db_error), Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: JSONException) {
                            Log.e("exception ", e.toString())
                        }
                    }
                    Log.e("Response", response.toString())
                    hideProgressBar()
                }, Response.ErrorListener { error ->
            ErrorHandler.setVolleyMessage(this@KotLogin, error)
            hideProgressBar()
        }) {}
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj)
    }

    fun showProgessBar() {
        if (progress_bar != null) {
            progress_bar!!.visibility = View.VISIBLE
        }
    }

    fun hideProgressBar() {
        if (progress_bar != null) {
            progress_bar!!.visibility = View.GONE
        }
    }

    fun validateLoginForm(): Boolean {
        username = username_editText!!.text.toString()
        password = password_ediIext!!.text.toString()
        if (!customValidator!!.setRequired(username)) {
            username_editText!!.error = resources.getString(R.string.username_error)
            return false.also { isLoginValid = it }
        }
        if (!customValidator!!.setRequired(password)) {
            password_ediIext!!.error = resources.getString(R.string.password_error)
            return false.also { isLoginValid = it }
        }
        return true.also { isLoginValid = it }
    }


    fun isCameraAvailable(context: Context): Boolean {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    fun checkWhetherCameraIsPresent(isAvailable:Boolean){
        if (!isAvailable) {
            AlertDialog.Builder(this@KotLogin, R.style.Theme_AppCompat_Dialog_Alert)
                    .setTitle("Sorry!!")
                    .setCancelable(false)
                    .setMessage("This device does not support camera?") // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                        dialog.dismiss()
                        finishAffinity()
                    } // A null listener allows the button to dismiss the dialog and take no further action.
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }
    }
}