package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutUsActivity extends NavigationBaseActivity {

// yuvraj

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_about_us, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            Log.e("in","versionName "+versionName+" versionCode "+versionCode);
            version_textview.setText("Version : "+versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Bind(R.id.version_textview)
    TextView version_textview;

    @OnClick(R.id.privacy_policy_layout)
    void setPrivacyPolicy(){
        Intent i = new Intent(AboutUsActivity.this,AppPrivacyPolicy.class);
        startActivity(i);
    }

    @OnClick(R.id.whats_new_layout)
    void setWhats_new_layout(){
        Intent i = new Intent(AboutUsActivity.this,AppNewFeatures.class);
        startActivity(i);
    }

    @OnClick(R.id.feedback_email)
    void sendFeedbackEmail(){
        Intent i = new Intent(AboutUsActivity.this,FeedbackEmailActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.app_tour_layout)
    void startAppTour(){
        Intent i = new Intent(AboutUsActivity.this,AppTour.class);
        startActivity(i);
    }
}
