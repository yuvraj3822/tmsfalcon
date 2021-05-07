package com.tmsfalcon.device.tmsfalcon.customtools;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Dell on 3/8/2018.
 */
public class ProgressRequestBodyMultiple extends RequestBody {
    private File mFile;
    private String mPath;
    private UploadCallbacks mListener;
    private int current_i;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(long uploaded,int current_i_val);
        void onError();
        void onFinish();
    }

    public ProgressRequestBodyMultiple(final File file, int current_iteraton,final UploadCallbacks listener) {
        mFile = file;
        mListener = listener;
        current_i = current_iteraton;
    }

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;
        /*AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code

            }
        });*/
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {
                // update progress on UI thread
                Log.e("PrgrsRqustBodyMultiple","Utils.cancel_call => "+Utils.cancel_call);
                /*if(Utils.cancel_call){
                    //sendBroadcast("demo");
                    sendBroadcast(Utils.CANCEL_UPLOADING);
                }*/
                if(Utils.confirm_cancel_call){
                    //Log.e("in","confirm_cancel_call");
                    break;
                }
                if(Utils.pause_call){
                  //  Log.e("in","pause job intent service call");
                }
                handler.post(new ProgressUpdater(uploaded, fileLength));
                uploaded += read;
                sink.write(buffer, 0, read);
//                handler.post(new ProgressUpdater(uploaded, fileLength));

            }
        } finally {
            in.close();
        }
    }


    private class ProgressUpdater implements Runnable {
        private long mUploaded;
        private long mTotal;
        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            //mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
            mListener.onProgressUpdate(mUploaded,current_i);
        }
    }
}