package com.tmsfalcon.device.tmsfalcon.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
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

public class ReportedAccidentScreenEight extends NavigationBaseActivity {

    private static int next_screen = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_reported_accident_screen_eight, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        zoom_linear_layout.init(ReportedAccidentScreenEight.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == next_screen){
            if (resultCode == Activity.RESULT_OK){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "result");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    @Bind(R.id.zoom_linear_layout)
    ZoomLinearLayout zoom_linear_layout;

    @OnClick(R.id.next_btn)
    void goToNext() {
        Intent i = new Intent(ReportedAccidentScreenEight.this, AccidentSignatureActivity.class);
        startActivityForResult(i,next_screen);
    }
}
