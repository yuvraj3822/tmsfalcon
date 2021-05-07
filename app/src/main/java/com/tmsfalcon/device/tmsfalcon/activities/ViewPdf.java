package com.tmsfalcon.device.tmsfalcon.activities;

import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.tmsfalcon.device.tmsfalcon.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewPdf extends AppCompatActivity {

    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    File myDir = new File(root+"/"+"downloaded_docs");
    String file_name;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);
        ButterKnife.bind(this);
        lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);
        file_name = getIntent().getExtras().getString("name");
        if(file_name != null){
            pdfName.setText(getIntent().getExtras().getString("document_name"));
            file = new File(myDir,file_name);
            pdf.fromFile(file)
                    .defaultPage(0)
                    .load();
        }
    }

    @Bind(R.id.pdfView)
    PDFView pdf;
    @Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on) ImageView lights_on;
    @Bind(R.id.pdf_name)
    TextView pdfName;

    @OnClick(R.id.back_btn)
    void PreviousScreen(){
        super.onBackPressed();
        if(file_name != null && file.exists()){
            if(!file.delete()){
              Log.e("Error","File not deleted");
            }
            //file.delete();
        }
    }

}
