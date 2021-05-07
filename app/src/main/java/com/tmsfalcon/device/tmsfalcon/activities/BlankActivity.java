package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BlankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);
        ButterKnife.bind(this);
        lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);
        bell_layout.setVisibility(View.GONE);
        Intent startDocumentTypeDialogActivity = new Intent(BlankActivity.this,DocumentTypeDialogActivity.class);
        startActivity(startDocumentTypeDialogActivity);
    }
    @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+ SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }
    @Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;
    @Bind(R.id.cart_badge)
    TextView cartBadgeTextView;
    @Bind(R.id.bell_layout)
    RelativeLayout bell_layout;
}
