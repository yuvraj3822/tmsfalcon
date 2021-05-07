package com.tmsfalcon.device.tmsfalcon.customtools;

import okhttp3.RequestBody;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by Dell on 1/22/2019.
 */

public class UploadMultipleRequestBody extends RequestBody{

    private String mPath;
    private UploadCallbacks mListener;
    private int current_i;
    long total_uploaded = 0;
    private List<File> mFile=new ArrayList<>();

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(long uploaded,int current_i_val);
        void onError();
        void onFinish();
    }

    public UploadMultipleRequestBody(final List<File> file, int current_iteraton,final UploadCallbacks listener) {
        Log.e("in","constructor");
        this.mFile = file;
        this.mListener = listener;
        this.current_i = current_iteraton;
}

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {
        return getTotalFilesLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        Log.e("in","writeTo method");
        long fileLength = getTotalFilesLength();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile.get(current_i));
        long uploaded = 0;
        AppController.total_upload[0] = 0;
        Log.e("total upload at i",+current_i+" => "+AppController.total_upload[current_i]);
        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {
                // update progress on UI thread
                Log.e("read ",""+read);

                handler.post(new ProgressUpdater(total_uploaded, fileLength));
                uploaded += read;
                total_uploaded = AppController.total_upload[current_i] + uploaded;
                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
        if(current_i+1 >= AppController.total_upload.length){
            //index not exists
        }else{
            // index exists
            AppController.total_upload[current_i+1] = total_uploaded;
            Log.e("total upload at i+1"," => "+AppController.total_upload[current_i+1]);
        }

        Log.e("uploaded => ",""+uploaded+" total uploaded => "+""+total_uploaded+" total file length => "+fileLength+" current file length => "+mFile.get(current_i).length());

    }

    public long getTotalFilesLength(){
        long total_files_length = 0;
        for(int i=0;i<mFile.size();i++)
        {
            total_files_length+=mFile.get(i).length();
        }
        return total_files_length;
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
