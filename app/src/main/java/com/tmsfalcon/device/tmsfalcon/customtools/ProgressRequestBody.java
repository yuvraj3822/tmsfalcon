package com.tmsfalcon.device.tmsfalcon.customtools;

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
 * Created by Android on 10/30/2017.
 */

public class ProgressRequestBody extends RequestBody {
    private List<File> mFile=new ArrayList<>();
    @SuppressWarnings("unused")
    private String mPath;
    private UploadCallbacks mListener;
    long totaLength = 0;
    long totalL = 0;
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {
        void onProgressUpdate(int percentage);
        @SuppressWarnings("unused")
        void onError();
        @SuppressWarnings("unused")
        void onFinish();
    }

    public ProgressRequestBody(final List<File> file, final UploadCallbacks listener) {
        Log.e("in","constructor file size"+file.size());
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        // i want to upload only images
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {
        long length=0;
        for(int i=0;i<mFile.size();i++)
        {
            length+=mFile.get(i).length();
            totalL+=mFile.get(i).length();
        }
        this.totaLength = length;
        Log.e("total length", String.valueOf(this.totaLength));
        Log.e("total l", String.valueOf(this.totalL));
        return length;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
//      long fileLength = mFile.totaLength();
        long uploaded = 0;

        for (int i=0;i<mFile.size();i++) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            FileInputStream in = new FileInputStream(mFile.get(i));

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());

                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    handler.post(new ProgressUpdater(uploaded, totaLength));
                    uploaded += read;
                    sink.write(buffer, 0, read);

                }
            } finally {
                in.close();
            }
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
            //Log.e("On Progress", String.valueOf(mUploaded)+"total=>"+mTotal);
            mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
//            mListener.onFinish();
        }
    }
}