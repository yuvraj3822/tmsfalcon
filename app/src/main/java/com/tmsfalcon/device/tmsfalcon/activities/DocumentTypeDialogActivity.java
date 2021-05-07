package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DocumentTypeDialogActivity extends AppCompatActivity {

    DirectUploadTable db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_document_type_dialog);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);
        db = new DirectUploadTable(DocumentTypeDialogActivity.this);

    }

    @Override
    public void onBackPressed() {
        performBackAction();
    }

    public void performBackAction(){
        new AlertDialog.Builder(this)
                .setTitle("TmsFalcon")
                .setMessage("Are you sure you don't want to select any document type?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        db.deleteAllRecords();
                        Intent i = new Intent(DocumentTypeDialogActivity.this,DashboardActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        dialog.dismiss();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
    @Bind(R.id.radioDocumentType)
    RadioGroup radioGroup;

    @OnClick(R.id.cancel_btn)
    void performCancelTask(){
        performBackAction();
    }

    @OnClick(R.id.get_types_btn)
    void getDocumentTypes(){

        int selectedId = radioGroup.getCheckedRadioButtonId();
        Log.e("selectedId",""+selectedId);
        if(selectedId != -1){
            RadioButton selectedRadioButton = findViewById(selectedId);
            String selectedText = selectedRadioButton.getText().toString();
            Intent nextScreen = new Intent(DocumentTypeDialogActivity.this,LoadDocumentTypeDialogActivity.class);
            nextScreen.putExtra("selected_option",selectedText);
            startActivity(nextScreen);
            this.finish();
        }
        else{
            Toast.makeText(DocumentTypeDialogActivity.this,"Please select Document Type first.",Toast.LENGTH_LONG).show();
        }

    }

}
