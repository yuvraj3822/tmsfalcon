package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.ZoomLinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportedAccidentScreenTwo extends NavigationBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_reported_accident_screen_two, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        zoom_linear_layout.init(ReportedAccidentScreenTwo.this);
    }

    @Bind(R.id.zoom_linear_layout)
    ZoomLinearLayout zoom_linear_layout;

    @OnClick(R.id.next_btn)
    void goToNext(){
        Intent i = new Intent(ReportedAccidentScreenTwo.this,ReportedAccidentScreenThree.class);
        startActivity(i);
    }

    @OnClick(R.id.call_layout)
    void dialNumber(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + 911));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
