package com.tmsfalcon.device.tmsfalcon.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotAccidentFinalScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CaptureSignatureView;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccidentSignatureActivity extends NavigationBaseActivity {

    CaptureSignatureView mSig;
    SessionManager sessionManager;
    private static int SignatureResponse = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_accident_signature, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        sessionManager = new SessionManager();
        mSig = new CaptureSignatureView(this, null);
        signature_block.addView(mSig, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SignatureResponse){
            if (resultCode == Activity.RESULT_OK) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "result");
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        }
    }

    private void saveSignature(){
        AppController.signatureAccident = mSig.getBitmap();
    }

    @OnClick(R.id.preview)
    void previewImg(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AccidentSignatureActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.image_doc_alert, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);

        final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
        final ProgressBar dialog_image_progress = dialogView.findViewById(R.id.progress_bar);
        dialog_image_progress.setVisibility(View.VISIBLE);

        Glide.with(AccidentSignatureActivity.this)
                .load(mSig.getBitmap())
                .error(R.drawable.no_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        dialog_image_progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        dialog_image_progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(doc_image);

        Button close_btn = dialogView.findViewById(R.id.close_btn);
        final AlertDialog b = dialogBuilder.create();
        b.show();
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.dismiss();
            }
        });
    }

    @OnClick(R.id.redo_textview)
    void redoSign(){
        mSig.ClearCanvas();
    }

    @Bind(R.id.signature_block)
    LinearLayout signature_block;

    @OnClick(R.id.next_btn)
    void goToNext() {
        saveSignature();
        saveDBStatus();
        Intent i = new Intent(AccidentSignatureActivity.this, KotAccidentFinalScreen.class);
        startActivityForResult(i,SignatureResponse);

//        Intent returnIntent = new Intent();
//        returnIntent.putExtra("result", "result");
//        setResult(Activity.RESULT_OK, returnIntent);
//        finish();

    }



    private void  saveDBStatus(){
        sessionManager.saveStatusSignaturePref();
    }

}
