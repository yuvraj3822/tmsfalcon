package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChatScreen extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    private Context context = ChatScreen.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat_screen);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_chat_screen, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        displayUserImage();
        if(!networkValidator.isNetworkConnected()){
            Toast.makeText(context,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    public void displayUserImage(){

        String image_url = UrlController.HOST+session.get_driver_thumb();
        Glide.with(ChatScreen.this)
                .load(image_url)
                .into(user_imageview);
    }

    private void initIds() {
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
    }

    @Bind(R.id.user_image)
    ImageView user_imageview;


}
