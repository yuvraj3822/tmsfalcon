package com.tmsfalcon.device.tmsfalcon.activities.uploadCapturePhotos;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.JsonElement;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Url;

public class FileUploader {

    public FileUploaderCallback fileUploaderCallback;
    private ArrayList<File> files;
    public int uploadIndex = -1;
    private String uploadURL = "";
    private long totalFileLength = 0;
    private long totalFileUploaded = 0;
    private String filekey="";
    private UploadInterface uploadInterface;
    private String auth_token = "";
    private String[] responses;
    private Map<String,RequestBody> req;


    private interface UploadInterface {

        @Multipart
        @POST
        Call<PostResponse> uploadFile(@Url String url, @Part List<MultipartBody.Part> file, @PartMap Map<String, RequestBody> field, @Header("token") String authorization);

        @Multipart
        @POST
        Call<JsonElement> uploadFile(@Url String url, @Part MultipartBody.Part file);
    }

    public interface FileUploaderCallback{
        void onError();
        void onFinish(String[] responses,String resp,boolean isSuccesful);
        void onProgressUpdate(int currentpercent, int totalpercent, int filenumber);
    }

    public class PRRequestBody extends RequestBody {
        private File mFile;

        private static final int DEFAULT_BUFFER_SIZE = 2048;

        public PRRequestBody(final File file) {
            mFile = file;

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

            try {
                int read;
                Handler handler = new Handler(Looper.getMainLooper());
                while ((read = in.read(buffer)) != -1) {

                    // update progress on UI thread
                    uploaded += read;
                    sink.write(buffer, 0, read);
                    handler.post(new ProgressUpdater(uploaded, fileLength));

                }
            } finally {
                in.close();
            }
        }
    }

    public FileUploader(){
        uploadInterface = ApiClient.getClient().create(UploadInterface.class);
    }

    public void uploadFiles(String url, String filekey, ArrayList<File> files, String auth_token, Map<String,RequestBody> req, FileUploaderCallback fileUploaderCallback){
        uploadFiless(url,filekey,files,auth_token,req,fileUploaderCallback);
    }

    public void uploadFiless(String url,String filekey,ArrayList<File> files, String auth_token,Map<String,RequestBody> req, FileUploaderCallback fileUploaderCallback){
        this.fileUploaderCallback = fileUploaderCallback;
        this.files = files;
        this.uploadIndex = -1;
        this.uploadURL = url;
        this.filekey = filekey;
        this.req = req;
        this.auth_token = auth_token;
        totalFileUploaded = 0;
        totalFileLength = 0;
        uploadIndex = -1;
        responses = new String[files.size()];
        for(int i=0; i<files.size(); i++){
            totalFileLength = totalFileLength + files.get(i).length();
        }
        uploadNext();
    }

    private void uploadNext(){
//        if(files.size()>0){
//            if(uploadIndex!= -1)
//                totalFileUploaded = totalFileUploaded + files.get(uploadIndex).length();
//            uploadIndex++;
//            if(uploadIndex < files.size()){
//                uploadSingleFile(uploadIndex);
//            }else{
//                fileUploaderCallback.onFinish(responses);
//            }
//        }else{
//            fileUploaderCallback.onFinish(responses);
//        }



        for(int i = 0;i<files.size();i++) {

            PRRequestBody fileBody = new PRRequestBody(files.get(i));
            MultipartBody.Part filePart = MultipartBody.Part.createFormData(/*filekey*/"fileUpload[" + i + "]", files.get(i).getName(), fileBody);
            listMultipart.add(filePart);
        }

        uploadSingleFile();

    }


    private List<MultipartBody.Part> listMultipart = new ArrayList<>();

    private void uploadSingleFile(/*final int index*/){
//        PRRequestBody fileBody = new PRRequestBody(files.get(index));
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData(/*filekey*/"fileUpload["+index+"]", files.get(index).getName(), fileBody);
        Call<PostResponse> call;
//        if(auth_token.isEmpty()){
//            call  = uploadInterface.uploadFile(uploadURL, filePart);
//        }else{
//            call  = uploadInterface.uploadFile(uploadURL, listMultipart, req,auth_token);
       // }

        call  = RestClient.get().uploadDocument(auth_token, listMultipart, req);



        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {


              Log.e("response","resp: "+response);

              if (response.isSuccessful()){
                  Log.e("successful","succesful");
                  fileUploaderCallback.onFinish(null,"Document uploaded successfully",true);
              }else {
                  Log.e("error","error");
                  fileUploaderCallback.onFinish(null,"Internal server error",false);

              }

             // fileUploaderCallback.onFinish();

                // Log.e("gson to json success",new Gson().toJson(response));
//                handleOnRespnse(response);
//                sendBroadcast(Utils.HIDE_PROGRESS_BAR);
                //queueActivity.progressBar.setProgress(0);
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {

                Log.e("error","resp: "+t);


//                sendBroadcast(Utils.HIDE_PROGRESS_BAR);
//                //queueActivity.progressBar.setProgress(0);
//                handleOnFailure(t);

            }
        });

//        call.enqueue(new Callback<PostResponse>() {
//            @Override
//            public void onResponse(Call<PostResponse> call, retrofit2.Response<PostResponse> response) {
//
//                Log.e("response: ","check: "+response);
////                if (response.isSuccessful()) {
////                    JsonElement jsonElement = response.body();
////                    responses[index] = jsonElement.toString();
////                }else{
////                    responses[index] = "";
////                }
//               // uploadNext();
//            }
//
//            @Override
//            public void onFailure(Call<PostResponse> call, Throwable t) {
//                Log.e("error","error: "+t);
//                fileUploaderCallback.onError();
//            }
//        });
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
            int current_percent = (int)(100 * mUploaded / mTotal);
            int total_percent = (int)(100 * (totalFileUploaded+mUploaded) / totalFileLength);
            fileUploaderCallback.onProgressUpdate(current_percent, total_percent,uploadIndex+1 );
        }
    }
}
