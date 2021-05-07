package com.tmsfalcon.device.tmsfalcon.activities.test;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class TestToProgressRequestBodyObservable extends RequestBody {

    File file;
    int ignoreFirstNumberOfWriteToCalls;
    int numWriteToCalls = 0;//enter code here

    UploadCallbacksss mListener;

    public TestToProgressRequestBodyObservable(File file,UploadCallbacksss mListener) {
        this.file = file;

        ignoreFirstNumberOfWriteToCalls =0;
        this.mListener = mListener;
    }


    public interface UploadCallbacksss {
        void onProgressUpdate(double value);
        void onError();
        void onFinish();
    }


    public TestToProgressRequestBodyObservable(File file, int ignoreFirstNumberOfWriteToCalls) {
        this.file = file;
        this.ignoreFirstNumberOfWriteToCalls = ignoreFirstNumberOfWriteToCalls;
    }


    PublishSubject<Float> floatPublishSubject = PublishSubject.create();

    public Observable<Float> getProgressSubject(){
        return floatPublishSubject;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("image/*");
    }

    @Override
    public long contentLength() throws IOException {
        return file.length();
    }



    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        numWriteToCalls++;


        float fileLength = file.length();
        byte[] buffer = new byte[2048];
        FileInputStream in = new FileInputStream(file);
        float uploaded = 0;

        try {
            int read;
            read = in.read(buffer);
            float lastProgressPercentUpdate = 0;
            while (read != -1) {

                uploaded += read;
                sink.write(buffer, 0, read);
                read = in.read(buffer);

                // when using HttpLoggingInterceptor it calls writeTo and passes data into a local buffer just for logging purposes.
                // the second call to write to is the progress we actually want to track
                if (numWriteToCalls > ignoreFirstNumberOfWriteToCalls ) {
                    float progress = (uploaded / fileLength) * 100;
                    //prevent publishing too many updates, which slows upload, by checking if the upload has progressed by at least 1 percent
                    if (progress - lastProgressPercentUpdate > 1 || progress == 100f) {
                        // publish progress

                        Log.e("well","well:: "+progress);

                        mListener.onProgressUpdate(progress);

                        floatPublishSubject.onNext(progress);
                        lastProgressPercentUpdate = progress;
                    }
                }
            }
        } finally {
            in.close();
        }

    }


    private class ProgressUpdater implements Runnable {
//        private long mUploaded;
//        private long mTotal;
        private double value;

        public ProgressUpdater(Double value) {
//            mUploaded = uploaded;
//            mTotal = total;
            this.value = value;
        }

        @Override
        public void run() {
            //mListener.onProgressUpdate((int)(100 * mUploaded / mTotal));
            mListener.onProgressUpdate(value);
        }
    }


}