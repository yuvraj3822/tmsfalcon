package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackEmailActivity extends NavigationBaseActivity {

    NetworkValidator networkValidator;
    Call<PostResponse> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_feedback_email, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        networkValidator = new NetworkValidator(FeedbackEmailActivity.this);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.subject)
    EditText subject_editText;

    @Bind(R.id.message)
    EditText message_editText;

    @OnClick(R.id.submit_btn)
    void sendEmail(){
        showProgessBar();
        String subject = subject_editText.getText().toString();
        String message = message_editText.getText().toString();
        if(networkValidator.isNetworkConnected()){

            Map<String, RequestBody> postFields = new HashMap<>();
            postFields.put("subject", requestBody(subject));
            postFields.put("message", requestBody(message));

            //fragment.showProgessBar();
            call = RestClient.get().feedBackEmail(SessionManager.getInstance().get_token(),postFields);
            call.enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                    //Log.e("documents response ",new Gson().toJson(response));
                    if(response.body() != null || response.isSuccessful()){
                        List messagesList = response.body().getMessages();
                        String messages = "";
                        for(int i = 0; i<messagesList.size();i++){
                            messages += messagesList.get(i);
                        }
                        if (response.body().getStatus()) {
                            message_editText.setText("");
                            subject_editText.setText("");

                        }
                        Toast.makeText(FeedbackEmailActivity.this, ""+messages, Toast.LENGTH_SHORT).show();

                    }
                    else {

                        try {
                            String error_string = response.errorBody().string();
                            ErrorHandler.setRestClientMessage(FeedbackEmailActivity.this,error_string);

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
                    Toast.makeText(FeedbackEmailActivity.this, "Response Call Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
        else{
            Toast.makeText(FeedbackEmailActivity.this, ""+getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }
}
