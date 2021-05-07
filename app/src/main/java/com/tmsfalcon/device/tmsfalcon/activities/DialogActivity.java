package com.tmsfalcon.device.tmsfalcon.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;

import org.json.JSONArray;
import org.json.JSONException;

public class DialogActivity extends AppCompatActivity {

    String messages,messageListString,customMessage;
    TextView message_texview;
    Button ok_btn;
    boolean is_location;
    JSONArray messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dialog);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setFinishOnTouchOutside(false);
        initIds();
        is_location = getIntent().getBooleanExtra("is_location_dialog",false);
        if(is_location){
            customMessage = "To enable App access, Go to Settings and enable location permissions for Tmsfalcon.";
        }
        else{
            messageListString = getIntent().getStringExtra("messageList");
            messages = getIntent().getStringExtra("messages");
            try {
                messagesList = new JSONArray(messageListString);
                if(messagesList.get(0).equals("GPS Device Id Not found")){
                    customMessage = "IMEI not found,Please Try Again.If problem persists please call dispatcher.";
                }
                else{
                    customMessage = messages;
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }


        message_texview.setText(customMessage);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.logoutApi(DialogActivity.this);

            }
        });
    }

    public void initIds(){
        message_texview = findViewById(R.id.message_textview);
        ok_btn = findViewById(R.id.ok_btn);
    }
}
