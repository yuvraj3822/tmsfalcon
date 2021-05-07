package com.tmsfalcon.device.tmsfalcon.customtools;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.tmsfalcon.device.tmsfalcon.activities.TripDetail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 11/13/2017.
 */

public class ManagePdf {

    private String file_url = "";
    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    downloadPdfInterface dowloadInterace;

    public void registerDownloadInterface(downloadPdfInterface pdfInterface){
        this.dowloadInterace = pdfInterface;
    }

    public void downloadPdf(final Context context, String url, String name){
        final Context c_ontext = context;
        final String file_name = name;
        Log.e("url ","is "+url);
        RestClient.get().downloadFileWithDynamicUrlSync(url).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {

                if(response.body() != null || response.isSuccessful()){
                   if (response.isSuccessful()) {
                       Log.e("", "server contacted and has file "+response);

                       new AsyncTask<Void, Void, Void>() {
                           @Override
                           protected Void doInBackground(Void... voids) {
                               boolean writtenToDisk = writeResponseBodyToDisk(response.body(),c_ontext,file_name);
                               if(writtenToDisk){ //file download was a success?
                                   dowloadInterace.downloadCompleteCallback();
                               }
                               else{
                                   dowloadInterace.downloadFailureCallback();
                               }
                               Log.e("", "file download was a success? " + writtenToDisk);
                               return null;
                           }
                       }.execute();
                   } else {
                       dowloadInterace.urlNotFoundCallback();
                       Log.d("", "server contact failed");
                   }
               }
               else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(context,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("", "error");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body,Context context,String file_name) {
        Log.e("in","writeResponseBodyToDisk");
        try {
            // todo change the file location/name according to your needs
            File myDir = new File(root+"/"+"uploaded_docs");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File futureStudioIconFile = new File (myDir, file_name);
            if (futureStudioIconFile.exists ()) {
                if(!futureStudioIconFile.delete()){
                    Log.e("Error","File not deleted.");
                }
                //futureStudioIconFile.delete ();
            }
            //File futureStudioIconFile = new File(context.getExternalFilesDir(null) + File.separator + "Future Studio Icon.pdf");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                    int perc = (int)(100 * fileSizeDownloaded / fileSize);
                    dowloadInterace.downloadPercentageCallback(perc);
                    Log.e("", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                    outputStream.flush();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public interface downloadPdfInterface{
        void downloadPercentageCallback(int percentage);
        void downloadCompleteCallback();
        void downloadFailureCallback();
        void urlNotFoundCallback();
    }

}
