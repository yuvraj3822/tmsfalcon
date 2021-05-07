package com.tmsfalcon.device.tmsfalcon.customtools;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Android on 10/4/2017.
 */

public class ImageHelper {

    public static String root_directory_name = "tmsfalcon";
    public static String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;

    public String saveImageBitmap(Bitmap bitmap,String directory_name,String file_name){

        // directory_name is the name of directory inside Tmsfalcon folder

        if(isExternalStorageWritable()){
            Log.e("image helper","is in if");
            File myDir = new File(root+"/"+directory_name);
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File file = new File (myDir, file_name+".jpeg");
            if (file.exists ()){
                if(!file.delete()){
                    Log.e("Error ","File not deleted.");
                }
                //file.delete ();
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out);
                out.flush();
                out.close();
                return root+"/"+directory_name+"/"+file_name+".jpeg";

            } catch (Exception e) {
                 Log.e("exception ", String.valueOf(e));
            }
        }
        else{
            Log.e("image helper","is in else");
        }
        return null;
    }

    public String uniqueFileName(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        return "TMSFALCON-"+timeStamp;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static Bitmap resizeImageScaled(Bitmap bitmap) {
        int scaleSize =1024;
        Bitmap resizedBitmap = null;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if(originalHeight > originalWidth) {
            newHeight = scaleSize ;
            multFactor = (float) originalWidth/(float) originalHeight;
            newWidth = (int) (newHeight*multFactor);
        } else if(originalWidth > originalHeight) {
            newWidth = scaleSize ;
            multFactor = (float) originalHeight/ (float)originalWidth;
            newHeight = (int) (newWidth*multFactor);
        } else if(originalHeight == originalWidth) {
            newHeight = scaleSize ;
            newWidth = scaleSize ;
        }
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
        return resizedBitmap;
    }

    public static File bitmapToFile(Context context, Bitmap bitmap, String name) {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(context.getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }
}
