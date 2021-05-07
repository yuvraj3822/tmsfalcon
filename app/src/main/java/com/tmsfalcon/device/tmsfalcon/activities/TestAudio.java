package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.example.jean.jcplayer.JcPlayerManagerListener;
import com.example.jean.jcplayer.general.JcStatus;
import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.gson.Gson;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class TestAudio extends NavigationBaseActivity implements JcPlayerManagerListener {

    JcPlayerView jcPlayerView;
    SessionManager session;
    NetworkValidator networkValidator;
    String access_token,url;
    ArrayList<JcAudio> jcAudios;
    JcAudio jcAudio;
    String message_id,read_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_test_audio, null, false);
        drawer.addView(contentView, 0);
        //setContentView(R.layout.activity_test_audio);
        ButterKnife.bind(TestAudio.this);

        jcPlayerView = findViewById(R.id.jcplayer);
        jcAudios = new ArrayList<>();
        url = getIntent().getStringExtra("uri");
        access_token = getIntent().getStringExtra("access_token");
        message_id = getIntent().getStringExtra("message_id");
        read_status = getIntent().getStringExtra("read_status");
        initIds();
        setData();

    }
    public void setData(){
        if(networkValidator.isNetworkConnected()){
            //getData();
            String url_request = url+"?access_token="+access_token;
            Log.e("complete url ",url_request);
            jcAudio = JcAudio.createFromURL("Voicemail ",url_request);
            jcAudios.add(jcAudio);

            jcPlayerView.initPlaylist(jcAudios, TestAudio.this);
        }
        else {
            Toast.makeText(TestAudio.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        jcPlayerView.removeAudio(jcAudio);
        jcPlayerView.kill();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jcPlayerView.removeAudio(jcAudio);
        jcPlayerView.kill();
    }

    public void initIds(){

        networkValidator = new NetworkValidator(TestAudio.this);
        session = new SessionManager(TestAudio.this);

    }

    private void getData() {
        // Tag used to cancel the request
        String tag_json_obj = "fuel_rebates_tag";

        final String url_request = url+"?access_token="+access_token;
        Log.e("complete url ",url_request);
        showProgessBar();
        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, url_request,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        hideProgressBar();
                        try {
                            if (response!=null) {
                                try{
                                    jcAudios.add(JcAudio.createFromURL("Voicemail",url_request));
                                    jcPlayerView.initPlaylist(jcAudios, null);
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }


                                /*FileOutputStream outputStream;
                                String name="";
                                outputStream = openFileOutput(name, Context.MODE_PRIVATE);
                                outputStream.write(response);
                                outputStream.close();
                                Toast.makeText(TestAudio.this, "Download complete.", Toast.LENGTH_LONG).show();*/
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                            e.printStackTrace();
                        }
                    }
                } ,new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                hideProgressBar();
                error.printStackTrace();
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
        mRequestQueue.add(request);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    public void onPreparedAudio(JcStatus jcStatus) {

    }

    @Override
    public void onCompletedAudio() {
        if(Objects.equals(read_status,"Unread")){
            updateVoicemailReadStatus();
        }
    }

    @Override
    public void onPaused(JcStatus jcStatus) {

    }

    @Override
    public void onContinueAudio(JcStatus jcStatus) {

    }

    @Override
    public void onPlaying(JcStatus jcStatus) {

    }

    @Override
    public void onTimeChanged(JcStatus jcStatus) {

    }

    @Override
    public void onStopped(JcStatus jcStatus) {

    }

    @Override
    public void onJcpError(Throwable throwable) {

    }

    public void updateVoicemailReadStatus(){
        showProgessBar();
        RestClient.get().updateVoicemailReadStatus(SessionManager.getInstance().get_token(),message_id,"Read").enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, retrofit2.Response<PostResponse> response) {
                if(response.body() != null  || response.isSuccessful()){
                    Log.e("in",""+new Gson().toJson(response.body()));
                    List messagesList = response.body().getMessages();
                    String messages = "";
                    for(int i = 0; i<messagesList.size();i++){
                        messages += messagesList.get(i);
                    }
                    if(response.body().getStatus()){


                    }
                    else {
                        Log.e("in","status false body "+response.body());
                    }
                    Toast.makeText(TestAudio.this, ""+messages, Toast.LENGTH_SHORT).show();

                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(TestAudio.this,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });
    }
}
class InputStreamVolleyRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> mListener;
    private Map<String, String> mParams;

    //create a static map for directly accessing headers
    public Map<String, String> responseHeaders ;

    public InputStreamVolleyRequest(int method, String mUrl ,Response.Listener<byte[]> listener,
                                    Response.ErrorListener errorListener, HashMap<String, String> params) {
        // TODO Auto-generated constructor stub

        super(method, mUrl, errorListener);
        // this request would never use cache.
        setShouldCache(false);
        mListener = listener;
        mParams=params;
    }

    @Override
    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return mParams;
    };


    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

        //Initialise local responseHeaders map with response headers received
        responseHeaders = response.headers;

        //Pass the response data here
        return Response.success( response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
