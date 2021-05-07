package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import butterknife.ButterKnife
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.adapters.ReportedAccidentListAdapter
import com.tmsfalcon.device.tmsfalcon.customtools.*
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.entities.ReportedAccidentListModel
import kotlinx.android.synthetic.main.activity_reported_accident_list.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ReportedAccidentListK : NavigationBaseActivity() {
    var session: SessionManager? = null
    var networkValidator: NetworkValidator? = null
    var accidentListView: ListView? = null
    var mAdapter: ReportedAccidentListAdapter? = null
    var arrayList_accidents = ArrayList<ReportedAccidentListModel>()
    private var currentPage = 0
    private var previousTotal = 0
    private var loading = true
    var is_first_data_call = 1
    var total_count = 0
    var footer_layout: LinearLayout? = null
    var footer_text: TextView? = null
    lateinit var db : AccidentModuleDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_reported_accident_list, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this)
        initIds()
        checkInternet(true)
        clickListeners()
    }

    fun clickListeners(){
        fab.setOnClickListener {
            val i = Intent(this@ReportedAccidentListK, ProgressIntroScreen::class.java)
            startActivity(i)
        }

        add_accident.setOnClickListener {
            val i = Intent(this@ReportedAccidentListK,ProgressIntroScreen::class.java)
            startActivity(i)
        }
    }


    fun setScrollEvent() {
        accidentListView!!.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                //Log.e("in", "firstVisibleItem " + firstVisibleItem + " visibleItemCount " + visibleItemCount + " totalItemCount " + totalItemCount);
                val lastInScreen = firstVisibleItem + visibleItemCount
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                        currentPage++
                    }
                }
                if (!loading && lastInScreen == totalItemCount) {
                    if (is_first_data_call != 1) {
                        getAccidentData(true)
                        getAccidentDataFromDb(true)
                        loading = true
                    }
                }
            }
        })
    }

    private fun getAccidentData(showProgressBar: Boolean) {
        // Tag used to cancel the request
        currentPage += 1
        val tag_json_obj = "get_accident_records_tag"
        val url = UrlController.ACCIDENT_RECORDS
        if (showProgressBar) {
            progress_bar!!.visibility = View.VISIBLE
        }

        //doc_arrayList.clear();
        val params: MutableMap<String?, String?> = HashMap()
        params["length"] = "10"
        params["start"] = arrayList_accidents.size.toString()
        Log.e("params", " $params")
        val jsonObjReq: JsonObjectRequest = @SuppressLint("RestrictedApi")
        object : JsonObjectRequest(Method.POST,
                url, JSONObject(params as Map<*, *>),
                Response.Listener { response ->
                    var status: Boolean? = null
                    Log.e("Response", response.toString())
                    try {
                        status = response.getBoolean("status")
                        var dataArray: JSONArray? = JSONArray()
                        if (status) {
                            dataArray = response.getJSONArray("accidents")
                            total_count = response.getInt("total_count")
                            // System.out.print(response.getString("q"));
                            if (dataArray != null && dataArray.length() > 0) {
                                for (i in 0 until dataArray.length()) {
                                    val document_single = dataArray[i] as JSONObject
                                    val accident_date = document_single.getString("accident_date")
                                    val accident_time = document_single.getString("accident_time")
                                    val view_url = document_single.getString("view_url")
                                    val location_name = document_single.getString("location_name")
                                    val accident_status = document_single.getString("accident_status")
                                    val accident_id = document_single.getString("accident_id")
                                    val model = ReportedAccidentListModel(accident_id, accident_date, accident_time, location_name, accident_status, view_url)
                                    arrayList_accidents.add(model)
                                }
                                footer_layout!!.visibility = View.VISIBLE
                                footer_text!!.text = setFooterText(arrayList_accidents.size, total_count)
                                if (is_first_data_call == 1) {
                                    mAdapter = ReportedAccidentListAdapter(this@ReportedAccidentListK, arrayList_accidents)
                                    accidentListView!!.adapter = mAdapter
                                    is_first_data_call = 0
                                } else {
                                    mAdapter!!.notifyDataSetChanged()
                                }

                                if (arrayList_accidents.size == 0) {
                                    no_data_textview!!.visibility = View.GONE
                                    add_accident.visibility = View.VISIBLE
                                    fab.visibility = View.GONE
                                } else {
                                    add_accident.visibility = View.GONE
                                    fab.visibility = View.VISIBLE
                                }

                            } else {

                                if (arrayList_accidents.size == 0) {
                                    no_data_textview!!.visibility = View.GONE
                                    add_accident.visibility = View.VISIBLE
                                    fab.visibility = View.GONE
                                } else {
                                    add_accident.visibility = View.GONE
                                    fab.visibility = View.VISIBLE
                                }

                            }
                        }
                    } catch (e: JSONException) {
                        Log.e("exception ", e.toString())
                    }
                    if (progress_bar != null) {
                        progress_bar!!.visibility = View.GONE
                    }
                }, Response.ErrorListener { error ->
            ErrorHandler.setVolleyMessage(this@ReportedAccidentListK, error)
            if (progress_bar != null) {
                progress_bar!!.visibility = View.GONE
            }
        }) {
            /**
             * Passing some request headers
             */
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // headers.put("Content-Type", "application/json");
                headers["Token"] = session!!._token
                return headers
            }
        }
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj)
    }


        private fun getAccidentDataFromDb(showProgressBar: Boolean) {

// TODO:- Uncomment

//            arrayList_accidents.clear()




//        if (showProgressBar) {
//            progress_bar!!.visibility = View.VISIBLE
//        }



//        CoroutineScope(IO).launch {
//            var obj  = db.bfaDao().getBasicFormData()
//
//
//            if ( obj != null) {
//                val geocoder: Geocoder
//                val addresses: List<Address>
//                geocoder = Geocoder(this@ReportedAccidentListK, Locale.getDefault())
//                addresses = geocoder.getFromLocation(obj.lat.toDouble(), obj.lng.toDouble(), 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                val address: String = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                val city: String = addresses[0].getLocality()
//                val state: String = addresses[0].getAdminArea()
//                val country: String = addresses[0].getCountryName()
//                val postalCode: String = addresses[0].getPostalCode()
//                val knownName: String = addresses[0].getFeatureName()
//                var locationForModel = "$city,$state"
//                val rnd = Random()
//                val n = 100000 + rnd.nextInt(900000)
//                var objReported = ReportedAccidentListModel(n.toString(), obj.date, obj.time, locationForModel, obj.type, "")
//                arrayList_accidents.add(objReported)
//                CoroutineScope(Main).launch {
//                    mAdapter = ReportedAccidentListAdapter(this@ReportedAccidentListK, arrayList_accidents)
//                    accidentListView!!.adapter = mAdapter
//                }
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        getAccidentDataFromDb(true)
    }




    private fun checkInternet(showProgressBar: Boolean) {
        if (networkValidator!!.isNetworkConnected) {
            getAccidentData(showProgressBar)
            getAccidentDataFromDb(true)
            setScrollEvent()
        } else {
            Toast.makeText(this@ReportedAccidentListK, resources.getString(R.string.network_error), Toast.LENGTH_LONG).show()
        }
    }

    fun setFooterText(arrayListSize: Int, totalRecords: Int): String {
        return "Showing $arrayListSize of $totalRecords Records."
    }

    fun initIds() {
        networkValidator = NetworkValidator(this@ReportedAccidentListK)
        session = SessionManager(this@ReportedAccidentListK)
        accidentListView = findViewById(R.id.listViewAccident)
        footer_layout = findViewById(R.id.footer_layout)
        footer_text = findViewById(R.id.footer_text)
        db = AccidentModuleDb.getDatabase(this@ReportedAccidentListK)
    }

    fun showProgessBar() {
        progress_bar!!.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progress_bar!!.visibility = View.GONE
    }

}