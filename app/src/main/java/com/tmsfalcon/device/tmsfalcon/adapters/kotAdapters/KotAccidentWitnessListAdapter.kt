package com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.WitnessFormAccident
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel
import com.tmsfalcon.device.tmsfalcon.interfacess.clickOnWitness
import com.tmsfalcon.device.tmsfalcon.interfacess.deleteWitness
import com.tmsfalcon.device.tmsfalcon.widgets.Alert
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Android on 8/23/2017.
 */
class KotAccidentWitnessListAdapter(private val activity: Activity, data: ArrayList<WitnessFormAccident>, db: AccidentModuleDb,deleteWitness: deleteWitness,val clickOnWitness: clickOnWitness) : BaseAdapter() {
    private var mData = ArrayList<WitnessFormAccident>()
    private val mInflater: LayoutInflater
    var sessionManager: SessionManager
    private var isPlaying = false
    var wasPlaying = false
    private var mediaPlayer: MediaPlayer? = null
    var audioPlayerName: String? = null
    var holder: ViewHolder? = null
    var db: AccidentModuleDb
    var deleteWitness:deleteWitness



    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(position: Int): Any {
        return mData[position]
    }

    private fun onPlay(start: Boolean, local_holder: ViewHolder) {
        if (start) {
            startPlaying(local_holder)
        } else {
            stopPlaying(local_holder)
        }
    }

    private fun stopPlaying(local_holder: ViewHolder) {
        if (mediaPlayer != null) {
            mediaPlayer!!.reset()
            isPlaying = false
            local_holder.play_img!!.background = activity.resources.getDrawable(R.drawable.ic_play)
        }
    }

    fun startPlaying(local_holder: ViewHolder) {
        try {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                if (mediaPlayer != null) {
                    mediaPlayer!!.stop()
                    mediaPlayer!!.release()
                    mediaPlayer = null
                }
                local_holder.seekBar!!.progress = 0
                wasPlaying = true
                local_holder.play_img!!.background = activity.resources.getDrawable(R.drawable.ic_play)
            }
            if (!wasPlaying) {
                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer()
                }
                mData
                local_holder.play_img!!.background = activity.resources.getDrawable(R.drawable.ic_pause)
                mediaPlayer!!.setDataSource(audioPlayerName)
                mediaPlayer!!.setOnCompletionListener { stopPlaying(local_holder) }
                mediaPlayer!!.prepare()
                // mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer!!.isLooping = false
                local_holder.seekBar!!.max = mediaPlayer!!.duration
                mediaPlayer!!.start()
                Thread(Runnable {
                    var currentPosition = mediaPlayer!!.currentPosition
                    val total = mediaPlayer!!.duration
                    while (mediaPlayer != null && mediaPlayer!!.isPlaying && currentPosition < total) {
                        currentPosition = try {
                            Thread.sleep(1000)
                            mediaPlayer!!.currentPosition
                        } catch (e: InterruptedException) {
                            return@Runnable
                        } catch (e: Exception) {
                            return@Runnable
                        }
                        local_holder.seekBar!!.progress = currentPosition
                    }
                }).start()
            }
            wasPlaying = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val type = getItemViewType(position)
        val model = mData[position]
        if (convertView == null) {
            holder = ViewHolder()
            convertView = mInflater.inflate(R.layout.list_item_accident_witness, null)
            holder!!.witness_name_textview = convertView.findViewById(R.id.witness_name)
            holder!!.witness_phone_textview = convertView.findViewById(R.id.witness_phone)
            holder!!.witness_statement_textview = convertView.findViewById(R.id.witness_statement)
            holder!!.audio_layout = convertView.findViewById(R.id.audio_layout)
            holder!!.seekBar = convertView.findViewById(R.id.seekbar)
            holder!!.play_img = convertView.findViewById(R.id.play_img)
            holder!!.cancel_img = convertView.findViewById(R.id.cancel_icon)
            holder!!.detail_layout = convertView.findViewById(R.id.detail_layout)
//            detail_layout
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        convertView!!.id = model.id!!
        holder!!.witness_name_textview!!.text = model.witnessName
        holder!!.witness_phone_textview!!.text = model.witnessPhone
        holder!!.witness_statement_textview!!.text = model.witnessStatement
        if (model.witnessAudioUrl !== "" && model.witnessAudioUrl.length > 0) {
            holder!!.audio_layout!!.visibility = View.VISIBLE
            holder!!.play_img!!.tag = holder
            holder!!.play_img!!.setOnClickListener { view ->
                val local_holder = view.tag as ViewHolder
                Log.e("url", " is " + model.witnessAudioUrl)
                isPlaying = !isPlaying
                audioPlayerName = model.witnessAudioUrl
                onPlay(isPlaying, local_holder)
                if (isPlaying) {
                    local_holder.play_img!!.background = activity.resources.getDrawable(R.drawable.ic_pause) //Pause Button
                } else {
                    local_holder.play_img!!.background = activity.resources.getDrawable(R.drawable.ic_play) //Play Button
                }
            }
        } else {
            holder!!.audio_layout!!.visibility = View.GONE
        }

        holder!!.detail_layout!!.setOnClickListener {
            clickOnWitness.clickWitnessItem(mData[position])
        }

        holder!!.cancel_img!!.setOnClickListener {
//            val id = model.id
//            mData.remove(model)
//            notifyDataSetChanged()


            Alert.showAlertDialog(activity,"Caution!!","Are you sure you want to delete this info",object :DialogInterface.OnClickListener{
                override fun onClick(p0: DialogInterface?, p1: Int) {
                    if (p1 == DialogInterface.BUTTON_POSITIVE){
                        CoroutineScope(IO).launch {
                            Log.e("deletecalled","deletecalled")
                            Log.e("dbDeleted",": "+db.wfaDao().deleteSingleWitness(model))
                            CoroutineScope(Main).launch {
                                deleteWitness.deletePersonData(model)
                            }
                        }
                    }else {
                        p0?.dismiss()
                    }

                }

            })





        }
        return convertView
    }

    class ViewHolder {
        var witness_name_textview: TextView? = null
        var witness_phone_textview: TextView? = null
        var witness_statement_textview: TextView? = null
        var audio_layout: LinearLayout? = null
        var seekBar: SeekBar? = null
        var play_img: Button? = null
        var cancel_img: ImageView? = null
        var detail_layout:LinearLayout? = null
    }

    init {
        mData = data
        mInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        sessionManager = SessionManager(activity)
        this.db = db
        this.deleteWitness = deleteWitness
    }
}