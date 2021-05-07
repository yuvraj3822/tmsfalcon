package com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.activities.test.TestVideo
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.AccidentModuleDb
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.CaptureScreenAccident
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

/**
 * Created by Android on 10/12/2017.
 */
class KotReportAccidentQueueAdapter(var activity: Activity, var context: Context, list: ArrayList<CaptureScreenAccident>) : BaseAdapter() {
    //    var imagesList = ArrayList<String>()
    private val mInflater: LayoutInflater
    var db: AccidentModuleDb
    var arrayList = ArrayList<CaptureScreenAccident>()
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(position: Int): Any {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        Log.e("position", "" + position)
        var holder: ViewHolder? = null
        if (convertView == null) {

            holder = ViewHolder()
            convertView = mInflater.inflate(R.layout.grid_item_accident_queue, null)
            holder.imageView = convertView.findViewById(R.id.single_image)
            holder.deleteImage = convertView.findViewById(R.id.delete_img)
            holder.doc_type = convertView.findViewById(R.id.doc_type)
            holder.playImage = convertView.findViewById(R.id.play_image)
            holder.textTime = convertView.findViewById(R.id.time)



            convertView.tag = holder

        } else {
            holder = convertView.tag as ViewHolder
        }
        val model = arrayList[position]
        var fileLocation: File? = null
        try {
            fileLocation = File(model.image_url)
        } catch (e: Exception) {
        }
        holder.doc_type!!.text = model.doc_type

        if (!arrayList[position].isItVideo){
            holder.playImage!!.visibility = View.GONE
            Glide.with(context)
                    .load(fileLocation)
                    .into(holder.imageView!!)

        } else {
            holder.playImage!!.visibility = View.VISIBLE
            try {


            val bMap: Bitmap = ThumbnailUtils.createVideoThumbnail(model.image_url, MediaStore.Video.Thumbnails.MICRO_KIND)
            Glide.with(context)
                    .load(bMap)
                    .into(holder.imageView!!)
            }
            catch (e:java.lang.Exception){
                Log.e("excepttion",": "+e)
            }
        }


        holder.textTime?.setText(arrayList.get(position).time)
        // Picasso.with(context).load(fileLocation).into(holder.imageView);
        holder.deleteImage!!.setOnClickListener {
            AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Do you really want to delete?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes) { dialog, whichButton ->
                        //val result = db.deleteImagesById(model.id.toString())
                        CoroutineScope(IO).launch {
                            var result = db.csaDao().deleteImage(arrayList[position])

                            //                        Log.e("result ", " is " + result + " id is " + model.id)

                            CoroutineScope(Main).launch {
                                if (result != 0) {
                                    arrayList.removeAt(position)
                                    Toast.makeText(activity, "Image deleted successfully.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(activity, "There was some issue deleting the Image.Please try again later.", Toast.LENGTH_SHORT).show()
                                }
                                notifyDataSetChanged()
                                // db.showRecords(db);
                                dialog.dismiss()
                            }
                            
                        }
                    }


                    .setNegativeButton(android.R.string.no) { dialog, which -> dialog.dismiss() }.show()
        }

        holder.imageView!!.setOnClickListener {
//Todo to uncomment


            val dialogBuilder = AlertDialog.Builder(context)
            val inflater = activity.layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_images_swipe_viewpager, null)
            dialogBuilder.setView(dialogView)
            dialogBuilder.setCancelable(false)
            val doc_image = dialogView.findViewById<ImageView>(R.id.doc_image)
            val fileLocation = File(/*imagesList[position]*/model.image_url)
            /* Glide.with(context)
                        .load(fileLocation)
                        .into(doc_image);
                Button close_btn = dialogView.findViewById(R.id.close_btn);*/
            val b = dialogBuilder.create()
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(b.window!!.attributes)
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            b.window!!.attributes = lp
            val viewPager: ViewPager = dialogView.findViewById(R.id.pager)
            Log.e("imageslist", " is " + /*imagesList.size*/arrayList.size)
            val adapter = KotDialogImageSwipeAdapter(activity, b, arrayList)
            viewPager.adapter = adapter
            viewPager.currentItem = position
            b.show()

//            var intent = Intent(activity,TestVideo::class.java)
//            intent.putExtra("path",arrayList.get(position).image_url)
//            activity.startActivity(intent)



        }
        return convertView!!
    }

    class ViewHolder {
        var playImage: ImageView? = null
        var imageView: ImageView? = null
        var deleteImage: ImageView? = null
        var doc_type: TextView? = null
        var textTime: TextView? = null

    }

    init {
        arrayList = list
//        imagesList = image_list
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        db = AccidentModuleDb.getDatabase(context)
    }
}