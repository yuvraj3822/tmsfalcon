package com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.tmsfalcon.device.tmsfalcon.R
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.CaptureScreenAccident
import java.io.File
import java.util.*

/**
 * Created by Dell on 4/17/2019.
 */
class KotDialogImageSwipeAdapter // constructor
(private val _activity: Activity, var dialog: AlertDialog,
 private val _imagePaths: ArrayList<CaptureScreenAccident>) : PagerAdapter() {
    private var inflater: LayoutInflater? = null
    override fun getCount(): Int {
        return _imagePaths.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imgDisplay: ImageView
        val btnClose: Button
        var playImage:ImageView
        var videoView:VideoView
        inflater = _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewLayout = inflater!!.inflate(R.layout.image_doc_alert, container,
                false)
        imgDisplay = viewLayout.findViewById(R.id.doc_image)
        btnClose = viewLayout.findViewById(R.id.close_btn)
        playImage = viewLayout.findViewById(R.id.play_image)

        videoView = viewLayout.findViewById(R.id.vid_view);




        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);*/

        val fileLocation = File(/*_imagePaths[position]*/_imagePaths[position].image_url)


        if (!_imagePaths[position].isItVideo){
//            image
            videoView.visibility = View.GONE
            playImage!!.visibility = View.GONE
            Glide.with(_activity)
                    .load(fileLocation)
                    .into(imgDisplay)
        } else {
//            video
            videoView.visibility = View.VISIBLE
            playImage!!.visibility = View.VISIBLE
//            val bMap: Bitmap = ThumbnailUtils.createVideoThumbnail(_imagePaths[position].image_url, MediaStore.Video.Thumbnails.MICRO_KIND)
            var  myUri = Uri.parse(_imagePaths.get(position).image_url)
            videoView.setVideoURI(myUri)
            videoView.seekTo(5)
        }

        playImage.setOnClickListener {
            if (_imagePaths[position].isItVideo){
                playImage!!.visibility = View.GONE
                videoView.setMediaController(MediaController(_activity))
                videoView.requestFocus()
                videoView.start()
            }
        }


        videoView.setOnCompletionListener(object :MediaPlayer.OnCompletionListener{
            override fun onCompletion(p0: MediaPlayer?) {
                Log.e("completion","completion")
                playImage.visibility = View.VISIBLE
            }
        })
        // close button click event
        btnClose.setOnClickListener { dialog.dismiss() }
        (container as ViewPager).addView(viewLayout)
        return viewLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as RelativeLayout)
    }

}