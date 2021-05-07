package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotAccidentFinalScreen;
import com.tmsfalcon.device.tmsfalcon.adapters.ReportAccidentQueueAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ImageHelper;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.AccidentCaptureImageTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.tmsfalcon.device.tmsfalcon.activities.CaptureDocument.CAMERA;

public class UpdateAccidentCaptureScreen extends NavigationBaseActivity {

    AlertDialog alertDialog;
    ArrayAdapter doc_type_adapter;
    ArrayList<String> doc_type_arraylist = new ArrayList<>();
    ImageHelper imageHelper;
    ReportAccidentQueueAdapter adapter;
    SessionManager sessionManager;
    AppCompatSpinner doc_type;
    AccidentCaptureImageTable table;

    ArrayList<AccidentCaptureModel> model_arrayList = new ArrayList<>();
    ArrayList<String> images_arraylist = new ArrayList<>();
    int accident_id = 0;

    String mCameraFileName = "";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alertDialog != null){
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_update_accident_capture_screen);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_update_accident_capture_screen, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        accident_id = getIntent().getIntExtra("accident_id",0);
        init();
        setData();
    }

    private void setData(){
        AppController.accident_report_id = accident_id;
        model_arrayList = table.getAllAccidentImagesById(accident_id);
        images_arraylist.clear();
        for (AccidentCaptureModel cn : model_arrayList) {
            images_arraylist.add(cn.getImage_url());
        }
        Log.e("main imageslist"," is "+images_arraylist.size());
        adapter = new ReportAccidentQueueAdapter(UpdateAccidentCaptureScreen.this, UpdateAccidentCaptureScreen.this, model_arrayList, images_arraylist);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
    }

    public void init(){
        imageHelper = new ImageHelper();
        sessionManager = new SessionManager(UpdateAccidentCaptureScreen.this);
        table = new AccidentCaptureImageTable(UpdateAccidentCaptureScreen.this);
    }

    void showPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_accident_capture_picture, null);
        dialogBuilder.setView(dialogView);


        doc_type = dialogView.findViewById(R.id.doc_type_spinner);
        final Button capture_btn = dialogView.findViewById(R.id.capture_btn);

        doc_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) adapterView.getChildAt(0);
                if(textView != null){
                    textView.setTextColor(getResources().getColor(R.color.light_greyish));
                    textView.setTextSize(15);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        doc_type_arraylist.add("Truck");
        doc_type_arraylist.add("Trailer");
        doc_type_arraylist.add("DOT Numbers");
        doc_type_arraylist.add("Logos");
        doc_type_arraylist.add("License plate photo");
        doc_type_arraylist.add("Other");

        doc_type_adapter = new ArrayAdapter(UpdateAccidentCaptureScreen.this,android.R.layout.simple_spinner_item,doc_type_arraylist);
        doc_type_adapter.setDropDownViewResource(R.layout.custom_textview_to_spinner);
        doc_type.setAdapter(doc_type_adapter);


        alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(true);
        //emailAlertDialog.setTitle("Send Documents By Email");
        alertDialog.show();

        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                String newPicFile = imageHelper.uniqueFileName()+".jpg";


                String root_directory_name = "tmsfalcon";
                String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
                String outPath = root +"/"+ newPicFile;

                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);

                Log.e("outPath"," is "+outPath);
                Log.e("mCameraFileName"," is "+mCameraFileName);
                Log.e("outuri"," is "+outuri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);

                startActivityForResult(intent, CAMERA);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(lp);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == CAMERA) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Uri image = null;
            if (data != null) {
                image = data.getData();
                //imageView.setImageURI(image);
                // imageView.setVisibility(View.VISIBLE);
            }
            if (image == null && mCameraFileName != null) {
                image = Uri.fromFile(new File(mCameraFileName));
                // imageView.setImageURI(image);
                //imageView.setVisibility(View.VISIBLE);
            }

            Log.e("image"," is "+image);
            Log.e("mCameraFileName"," is "+mCameraFileName);

            // String image_url = imageHelper.saveImageBitmap(thumbnail,"Accident Images",imageHelper.uniqueFileName());

            // Log.e("image_url"," is "+image_url);

            //save in db -> accident report id,image_url,document_type;

            AccidentCaptureModel model = new AccidentCaptureModel();
            model.setDriver_id(sessionManager.get_driver_id());
            model.setDoc_type(doc_type.getSelectedItem().toString());
            model.setImage_url(mCameraFileName);
            model.setAccident_report_id(accident_id);

            table.saveAccidentImage(model);
            model_arrayList.add(model);
           // model_arrayList.clear();
           // model_arrayList = table.getAllAccidentImagesById(AppController.accident_report_id);
           // images_arraylist.clear();
            /*for (AccidentCaptureModel cn : model_arrayList) {

                images_arraylist.add(cn.getImage_url());

            }*/
            images_arraylist.add(mCameraFileName);
            Log.e("main imageslist"," is "+images_arraylist.size());
            //adapter = new ReportAccidentQueueAdapter(UpdateAccidentCaptureScreen.this, UpdateAccidentCaptureScreen.this, model_arrayList, images_arraylist);
            adapter.notifyDataSetChanged();
            //gridView.setAdapter(adapter);

            Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
        /*if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    circleImageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            circleImageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Bind(R.id.grid_view)
    GridView gridView;

    @OnClick(R.id.next_btn)
    void goToNext(){
        Intent i = new Intent(UpdateAccidentCaptureScreen.this, KotAccidentFinalScreen.class);
        startActivity(i);
    }

    @OnClick(R.id.capture)
    void capturePic(){
        showPopUp();

    }
}
