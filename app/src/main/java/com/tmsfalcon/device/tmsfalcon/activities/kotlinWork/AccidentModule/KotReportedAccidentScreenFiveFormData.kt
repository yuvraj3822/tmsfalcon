package com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.*
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.textfield.TextInputLayout
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters.KotAccidentWitnessListAdapter
import com.tmsfalcon.device.tmsfalcon.customtools.*
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.WitnessFormAccident
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel
import com.tmsfalcon.device.tmsfalcon.interfacess.clickOnWitness
import com.tmsfalcon.device.tmsfalcon.interfacess.deleteWitness
import kotlinx.android.synthetic.main.activity_reported_accident_screen_five_form_data.*
import kotlinx.android.synthetic.main.dialog_accident_witness.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class KotReportedAccidentScreenFiveFormData : NavigationBaseActivity(), Runnable, deleteWitness, clickOnWitness {
    lateinit var witnessAlertDialog: AlertDialog
    var sessionManager: SessionManager? = null
    var witness_arrayList = ArrayList<WitnessFormAccident>()
    var adapter: KotAccidentWitnessListAdapter? = null
    var customValidator: CustomValidator? = null
    var isFormValid = false
    private var isPlaying = false
    private var isRecording = false
    var permissionManager: PermissionManager? = null
    private var recorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    lateinit var mic_img: Button
    var root_directory_name = "tmsfalcon"
    var audioPlayerName = ""
    var root = Environment.getExternalStorageDirectory().toString() + "/" + root_directory_name
    var record_layout: LinearLayout? = null
    var chronometer: Chronometer? = null
    var play_layout: LinearLayout? = null
    lateinit var play_img: Button
    lateinit var seekBar: SeekBar
    lateinit var seekBarHint: TextView
    var wasPlaying = false
    var myDir: File? = null

    lateinit var witness_name_edittext: EditText
    lateinit var witness_phone_edittext: EditText
    lateinit var witness_note_edittext: EditText
    var witness_name_textinput: TextInputLayout? = null
    var witness_phone_textinput: TextInputLayout? = null

    var witness_statement_textinput: TextInputLayout? = null
    var error_witness_phone: TextView? = null
    var error_witness_name: TextView? = null
    var error_witness_statement: TextView? = null
    var accidentWitnessTable: AccidentWitnessTable? = null
    lateinit var db: AccidentModuleDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_reported_accident_screen_five_form_data, null, false)
        drawer.addView(contentView, 0)
        ButterKnife.bind(this)
        init()
        // witnessListView.init(ReportedAccidentScreenFiveFormData.this);
        myDir = File("$root/witnessAudio")

        if (!myDir!!.exists()) {
            myDir!!.mkdirs()
        }

        CoroutineScope(IO).launch {
            var result: List<WitnessFormAccident> = db.wfaDao().getWitnessFormData()
            CoroutineScope(Main).launch {
                if (result.isEmpty()) {

                    next_btn.setText(R.string.no_witness)
                    message.visibility = View.VISIBLE
                    //showPopUp()

                } else {
                    witness_arrayList.clear()
                    witness_arrayList.addAll(result)
                    adapter?.notifyDataSetChanged()
                    next_btn.setText(R.string.done)
                    message.visibility = View.GONE

                }
            }
        }
    }


    fun uniqueFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss")
                .format(Date())

        return "TMSFALCON-WITNESS-AUDIO-$timeStamp.mp4"
    }

    fun init() {
        sessionManager = SessionManager(this@KotReportedAccidentScreenFiveFormData)
        customValidator = CustomValidator(this@KotReportedAccidentScreenFiveFormData)
        permissionManager = PermissionManager()
        accidentWitnessTable = AccidentWitnessTable(this@KotReportedAccidentScreenFiveFormData)
        db = AccidentModuleDb.getDatabase(this@KotReportedAccidentScreenFiveFormData)
        adapter = KotAccidentWitnessListAdapter(this@KotReportedAccidentScreenFiveFormData, witness_arrayList, db, this@KotReportedAccidentScreenFiveFormData,this@KotReportedAccidentScreenFiveFormData)
        list_view_witness!!.adapter = adapter
    }

    fun validateForm(): Boolean {
        if (!customValidator!!.setRequired(witness_name_edittext.text.toString())) {
            error_witness_name!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_witness_name!!.visibility = View.GONE
            isFormValid = true
        }
        if (!customValidator!!.setRequired(witness_phone_edittext.text.toString())) {
            error_witness_phone!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_witness_phone!!.visibility = View.GONE
            isFormValid = true
        }
        if (!customValidator!!.setRequired(witness_note_edittext.text.toString())) {
            error_witness_statement!!.visibility = View.VISIBLE
            isFormValid = false
        } else {
            error_witness_statement!!.visibility = View.GONE
            isFormValid = true
        }
        return isFormValid
    }

//    witness_name_edittext
//    witness_phone_edittext
//    witness_note_edittext

    fun updateDialogWithData(view:View,obj:WitnessFormAccident?){
        view.witness_name_edittext.setText(obj?.witnessName)
        view.witness_phone_edittext.setText(obj?.witnessPhone)
        view.witness_note_edittext.setText(obj?.witnessStatement)

        view.ok_btn.setText("Update")
    }


    fun showPopUp(isUpdate:Boolean, obj: WitnessFormAccident?) {
        val dialogBuilder = AlertDialog.Builder(this@KotReportedAccidentScreenFiveFormData)
        val inflater = this@KotReportedAccidentScreenFiveFormData.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_accident_witness, null)
        dialogBuilder.setView(dialogView)
        val zoomLinearLayout: ZoomLinearLayout = dialogView.findViewById(R.id.body_layout)
        zoomLinearLayout.init(this@KotReportedAccidentScreenFiveFormData)
        val cancel_btn = dialogView.findViewById<Button>(R.id.cancel_btn)
        witness_name_edittext = dialogView.findViewById(R.id.witness_name_edittext)
        witness_phone_edittext = dialogView.findViewById(R.id.witness_phone_edittext)
        witness_note_edittext = dialogView.findViewById(R.id.witness_note_edittext)
        witness_name_edittext.addTextChangedListener(GenericTextWatcher(witness_name_edittext))
        witness_phone_edittext.addTextChangedListener(GenericTextWatcher(witness_phone_edittext))
        witness_note_edittext.addTextChangedListener(GenericTextWatcher(witness_note_edittext))
        chronometer = dialogView.findViewById(R.id.chronometer)
        mic_img = dialogView.findViewById(R.id.mic_img)
        record_layout = dialogView.findViewById(R.id.record_layout)
        play_layout = dialogView.findViewById(R.id.play_layout)
        play_img = dialogView.findViewById(R.id.play_img)
        seekBar = dialogView.findViewById(R.id.seekbar)
        seekBarHint = dialogView.findViewById(R.id.seekbar_hint)
        witness_name_textinput = dialogView.findViewById(R.id.witness_name_textinput)
        witness_phone_textinput = dialogView.findViewById(R.id.witness_phone_textinput)
        witness_statement_textinput = dialogView.findViewById(R.id.witness_statement_textinput)
        error_witness_name = dialogView.findViewById(R.id.error_witness_name)
        error_witness_phone = dialogView.findViewById(R.id.error_witness_phone)
        error_witness_statement = dialogView.findViewById(R.id.error_witness_statement)
        mic_img.setOnClickListener(View.OnClickListener { record_functionality() })

        dialogView.witness_phone_edittext.setText("")
        play_img.setOnClickListener(View.OnClickListener {
            isPlaying = !isPlaying
            onPlay(isPlaying)
            if (isPlaying) {
                play_img.background = resources.getDrawable(R.drawable.ic_pause) //Pause Button
            } else {
                play_img.background = resources.getDrawable(R.drawable.ic_play) //Play Button
            }
            mic_img.isEnabled = !isPlaying
        })
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                seekBarHint.visibility = View.VISIBLE
                val x = Math.ceil(progress / 1000f.toDouble()).toInt()
                if (x != 0 && mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                    clearMediaPlayer()
                    play_img.background = resources.getDrawable(R.drawable.ic_play)
                    this@KotReportedAccidentScreenFiveFormData.seekBar.progress = 0
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekBarHint.visibility = View.VISIBLE
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.seekTo(seekBar.progress)
                }
            }
        })
        cancel_btn.setOnClickListener { witnessAlertDialog.dismiss() }
        val ok_btn = dialogView.findViewById<Button>(R.id.ok_btn)
        ok_btn.setOnClickListener {
            validateForm()
            if (isFormValid) {
                val model = AccidentWitnessModel()
                model.driver_id = sessionManager!!._driver_id
                model.accident_report_id = AppController.accident_report_id
                model.witness_name = witness_name_edittext.text.toString()
                model.witness_phone = witness_phone_edittext.text.toString()
                model.witness_statement = witness_note_edittext.text.toString()
                model.witness_audio_url = audioPlayerName
                val inserted_id = accidentWitnessTable!!.saveWitness(model)

                CoroutineScope(IO).launch {

                    var no:Int = 0
                    var retrieveLastItem = db.wfaDao().getWitnessFormData()
                    if (retrieveLastItem.size>0){
                        var value =  retrieveLastItem.get(retrieveLastItem.size-1)
                        if (isUpdate) no = obj?.id!! else  no = value.id!!+1
                    }


                    var witnessFormData = WitnessFormAccident(no, sessionManager!!._driver_id, AppController.accident_report_id, witness_name_edittext.text.toString()
                            , witness_phone_edittext.text.toString(), witness_note_edittext.text.toString(), audioPlayerName)
                    db.wfaDao().insert(witnessFormData)
                    Log.e("audio_url", " is $audioPlayerName")
                    setUpWitnessList(inserted_id, witnessFormData)
                }
            }
        }

        dialogView.ok_btn.setText("Save")

        if(isUpdate){
            updateDialogWithData(dialogView,obj)
        }

        witnessAlertDialog = dialogBuilder.create()
        witnessAlertDialog.setCancelable(false)
        //emailAlertDialog.setTitle("Send Documents By Email");
        witnessAlertDialog.show()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(witnessAlertDialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        witnessAlertDialog.window!!.attributes = lp

    }


    suspend fun setUpWitnessList(inserted_id: Long, wfaSchema: WitnessFormAccident) {
        CoroutineScope(Main).launch {
//            witness_arrayList.add(WitnessFormAccident(0, sessionManager!!._driver_id,
//                    AppController.accident_report_id, witness_name_edittext.text.toString(),
//                    witness_phone_edittext.text.toString(), witness_note_edittext.text.toString(), audioPlayerName))
//
//


            CoroutineScope(IO).launch {
                witness_arrayList.clear()
                var result: List<WitnessFormAccident> = db.wfaDao().getWitnessFormData()
                witness_arrayList.addAll(result)
            }

            adapter?.notifyDataSetChanged()
            // set
//            setUpWitnessList(witness_arrayList)

            witnessAlertDialog.dismiss()
            audioPlayerName = ""
//            Toast.makeText(this@KotReportedAccidentScreenFiveFormData, "Witness Saved", Toast.LENGTH_LONG).show()
            message.visibility = View.GONE
            next_btn.setText(R.string.done)

        }

    }


    private fun onPlay(start: Boolean) {
        if (start) {
            startPlaying()
        } else {
            stopPlaying()
        }
    }

    fun startPlaying() {
        try {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                clearMediaPlayer()
                seekBar.progress = 0
                wasPlaying = true
                play_img.background = resources.getDrawable(R.drawable.ic_play)
            }
            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                }
                play_img.background = resources.getDrawable(R.drawable.ic_pause)
                mediaPlayer!!.setDataSource(audioPlayerName)
                mediaPlayer!!.setOnCompletionListener(completionListener)
                mediaPlayer!!.prepare()
                // mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer!!.isLooping = false
                seekBar.max = mediaPlayer!!.duration
                mediaPlayer!!.start()
                Thread(this).start()
            }
            wasPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun record_functionality() {
        isRecording = !isRecording
        onRecord(isRecording)
        if (isRecording) {
            record_layout!!.setBackgroundColor(resources.getColor(R.color.red_dark)) // stop recording
            mic_img.background = resources.getDrawable(R.drawable.ic_stop)
        } else {
            chronometer!!.base = chronometer!!.base
            chronometer!!.stop()
            record_layout!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
            mic_img.background = resources.getDrawable(R.drawable.ic_mic_white) // start recording
        }
        play_img.isEnabled = !isRecording
    }

    private fun onRecord(start: Boolean) {
        if (start) {
            val file_name = uniqueFileName()
            audioPlayerName = myDir.toString() + "/" + file_name
            startRecording()
        } else {
            stopRecording()
        }
    }

    private val completionListener = MediaPlayer.OnCompletionListener { stopPlaying() }

    private fun startRecording() {
        recorder = MediaRecorder()
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        // ACC_ELD is supported only from SDK 16+.
        // You can use other encoders for lower vesions.
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        //recorder.setAudioSamplingRate(44100);
        //recorder.setAudioEncodingBitRate(96000);
        recorder!!.setOutputFile(audioPlayerName)
        try {
            recorder!!.prepare()
            recorder!!.start()
            chronometer!!.visibility = View.VISIBLE
            chronometer!!.base = SystemClock.elapsedRealtime()
            chronometer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        if (recorder != null) {
            recorder!!.release()
            completionRecording()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        if (witnessAlertDialog != null) {
//            witnessAlertDialog.dismiss()
//        }
        clearMediaPlayer()
    }

    private fun stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer!!.reset()
            completionPlaying()
        }
    }

    private fun clearMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    private fun completionPlaying() {
        reset()
        play_img.background = resources.getDrawable(R.drawable.ic_play)
        mic_img.isEnabled = true
    }

    private fun reset() {
        isRecording = false
        isPlaying = false
    }

    private fun completionRecording() {
        reset()
        record_layout!!.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        mic_img.background = resources.getDrawable(R.drawable.ic_mic_white) // start recording
        play_img.background = resources.getDrawable(R.drawable.ic_play)
        play_img.isEnabled = true
    }

    public override fun onPause() {
        super.onPause()
        stopRecording()
        stopPlaying()
    }

    public override fun onStop() {
        super.onStop()
        stopRecording()
        stopPlaying()
    }


    @OnClick(R.id.add_btn)
    fun showDialog() {
        showPopUp(false,null)
    }

    @OnClick(R.id.next_btn)
    fun goToNext() {
//        val i = Intent(this@KotReportedAccidentScreenFiveFormData, ReportedAccidentScreenSix::class.java)
//        startActivity(i)

//        saveDBStatus()
//        val returnIntent = Intent()
//        returnIntent.putExtra("result", "result")
//        setResult(Activity.RESULT_OK, returnIntent)
//        finish()
//

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

    fun saveDBStatus() {
        sessionManager?.saveStatusWitenessPref()
    }

    fun saveTextStatus() {
        if (next_btn.text.toString().equals("Done",ignoreCase = true)){
            sessionManager?.saveStatusTextWitenessPref()
        }else {
            sessionManager?.clearTextWitnessPref()
        }

    }

    fun clearTextStatus(){
        sessionManager?.clearTextWitnessPref()
    }

    override fun run() {
        var currentPosition = mediaPlayer!!.currentPosition
        val total = mediaPlayer!!.duration
        while (mediaPlayer != null && mediaPlayer!!.isPlaying && currentPosition < total) {
            currentPosition = try {
                Thread.sleep(1000)
                mediaPlayer!!.currentPosition
            } catch (e: InterruptedException) {
                return
            } catch (e: Exception) {
                return
            }
            seekBar.progress = currentPosition
        }
    }

    private inner class GenericTextWatcher(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.witness_name_edittext -> {
                    val str1 = witness_name_edittext.text.toString()
                    if (str1 !== "" && str1.length > 0) {
                        error_witness_name!!.visibility = View.GONE
                    } else {
                        error_witness_name!!.visibility = View.VISIBLE
                    }
                }
                R.id.witness_phone_edittext -> {
                    val str2 = witness_phone_edittext.text.toString()
//                    if (str2 !== "" && str2.length > 0) {
//                        error_witness_phone!!.visibility = View.GONE
//                    } else {
//                        error_witness_phone!!.visibility = View.VISIBLE
//                    }
                }
                R.id.witness_note_edittext -> {
                    val str3 = witness_note_edittext.text.toString()
                    if (str3 !== "" && str3.length > 0) {
                        error_witness_statement!!.visibility = View.GONE
                    } else {
                        error_witness_statement!!.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    companion object {
        const val RECORD_AUDIO = 0x5
    }

    override fun deletePersonData(model: WitnessFormAccident) {
        witness_arrayList.remove(model)
        adapter?.notifyDataSetChanged()
        if (witness_arrayList.isEmpty()){
            message.visibility = View.VISIBLE
            next_btn.setText(R.string.no_witness)

        }else {
            message.visibility = View.GONE
            next_btn.setText(R.string.done)

        }
    }

    override fun clickWitnessItem(obj:WitnessFormAccident) {
        Log.e("clicked","clicked")
        showPopUp(true,obj)
    }
}