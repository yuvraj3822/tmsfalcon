package com.tmsfalcon.device.tmsfalcon.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.SystemClock;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.AccidentWitnessListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdateAccidentWitnessData extends NavigationBaseActivity implements Runnable{

    AlertDialog witnessAlertDialog;
    SessionManager sessionManager;
    ArrayList<AccidentWitnessModel> witness_arrayList = new ArrayList<>();
    AccidentWitnessListAdapter adapter;
    CustomValidator customValidator;
    boolean isFormValid ;

    private boolean isPlaying = false;
    private boolean isRecording = false;
    PermissionManager permissionManager;
    private MediaRecorder recorder = null;
    private MediaPlayer mediaPlayer = null;

    static final Integer RECORD_AUDIO = 0x5;
    Button mic_img;
    String root_directory_name = "tmsfalcon";
    String audioPlayerName = "";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    LinearLayout record_layout;
    Chronometer chronometer;

    LinearLayout play_layout;
    Button play_img;
    SeekBar seekBar;
    TextView seekBarHint;
    boolean wasPlaying = false;
    File myDir;
    EditText witness_name_edittext,witness_phone_edittext,witness_note_edittext;
    TextInputLayout witness_name_textinput,witness_phone_textinput,witness_statement_textinput;
    TextView error_witness_phone,error_witness_name,error_witness_statement;
    AccidentWitnessTable accidentWitnessTable;
    int accident_id = 0;
    ArrayList<AccidentWitnessModel> previous_records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_update_accident_witness_data);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_update_accident_witness_data, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        accident_id = getIntent().getIntExtra("accident_id",0);
        init();
        myDir = new File(root+"/witnessAudio");
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        showData();
    }

    private void showData(){

        previous_records =  accidentWitnessTable.getWitnessDetailById(accident_id);

        adapter = new AccidentWitnessListAdapter(UpdateAccidentWitnessData.this,previous_records);

        witnessListView.setAdapter(adapter);
    }

    public String uniqueFileName(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        return "TMSFALCON-WITNESS-AUDIO-"+timeStamp+".mp4";
    }

    public void init(){

        sessionManager = new SessionManager(UpdateAccidentWitnessData.this);
        customValidator = new CustomValidator(UpdateAccidentWitnessData.this);
        permissionManager = new PermissionManager();
        accidentWitnessTable = new AccidentWitnessTable(UpdateAccidentWitnessData.this);
    }

    public boolean validateForm(){

        if(!customValidator.setRequired(witness_name_edittext.getText().toString())){

            error_witness_name.setVisibility(View.VISIBLE);
            isFormValid = false;
        }
        else{
            error_witness_name.setVisibility(View.GONE);
            isFormValid = true;
        }
        if(!customValidator.setRequired(witness_phone_edittext.getText().toString())){
            error_witness_phone.setVisibility(View.VISIBLE);
            isFormValid = false;
        }
        else{
            error_witness_phone.setVisibility(View.GONE);
            isFormValid = true;
        }
        if(!customValidator.setRequired(witness_note_edittext.getText().toString())){
            error_witness_statement.setVisibility(View.VISIBLE);
            isFormValid = false;
        }
        else{
            error_witness_statement.setVisibility(View.GONE);
            isFormValid = true;
        }

        return isFormValid;
    }

    void showPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UpdateAccidentWitnessData.this);
        LayoutInflater inflater = UpdateAccidentWitnessData.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_accident_witness, null);
        dialogBuilder.setView(dialogView);

        Button cancel_btn = dialogView.findViewById(R.id.cancel_btn);
        witness_name_edittext = dialogView.findViewById(R.id.witness_name_edittext);
        witness_phone_edittext = dialogView.findViewById(R.id.witness_phone_edittext);
        witness_note_edittext = dialogView.findViewById(R.id.witness_note_edittext);

        witness_name_edittext.addTextChangedListener(new GenericTextWatcher(witness_name_edittext));
        witness_phone_edittext.addTextChangedListener(new GenericTextWatcher(witness_phone_edittext));
        witness_note_edittext.addTextChangedListener(new GenericTextWatcher(witness_note_edittext));

        chronometer = dialogView.findViewById(R.id.chronometer);

        mic_img = dialogView.findViewById(R.id.mic_img);
        record_layout = dialogView.findViewById(R.id.record_layout);
        play_layout = dialogView.findViewById(R.id.play_layout);
        play_img = dialogView.findViewById(R.id.play_img);
        seekBar = dialogView.findViewById(R.id.seekbar);
        seekBarHint = dialogView.findViewById(R.id.seekbar_hint);

        witness_name_textinput = dialogView.findViewById(R.id.witness_name_textinput);
        witness_phone_textinput = dialogView.findViewById(R.id.witness_phone_textinput);
        witness_statement_textinput = dialogView.findViewById(R.id.witness_statement_textinput);

        error_witness_name = dialogView.findViewById(R.id.error_witness_name);
        error_witness_phone = dialogView.findViewById(R.id.error_witness_phone);
        error_witness_statement = dialogView.findViewById(R.id.error_witness_statement);

        mic_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record_functionality();

            }
        });

        play_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPlaying = !isPlaying;

                onPlay(isPlaying);
                if(isPlaying){
                    play_img.setBackground(getResources().getDrawable(R.drawable.ic_pause)); //Pause Button
                }
                else{
                    play_img.setBackground(getResources().getDrawable(R.drawable.ic_play)); //Play Button
                }
                mic_img.setEnabled(!isPlaying);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                seekBarHint.setVisibility(View.VISIBLE);
                int x = (int) Math.ceil(progress / 1000f);

                if (x != 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    clearMediaPlayer();
                    play_img.setBackground(getResources().getDrawable(R.drawable.ic_play));
                    UpdateAccidentWitnessData.this.seekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }

            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                witnessAlertDialog.dismiss();
            }
        });


        Button ok_btn = dialogView.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateForm();
                if(isFormValid){

                    AccidentWitnessModel model = new AccidentWitnessModel();
                    model.setDriver_id(sessionManager.get_driver_id());
                    model.setAccident_report_id(accident_id);
                    model.setWitness_name(witness_name_edittext.getText().toString());
                    model.setWitness_phone(witness_phone_edittext.getText().toString());
                    model.setWitness_statement(witness_note_edittext.getText().toString());
                    model.setWitness_audio_url(audioPlayerName);

                    long inserted_id = accidentWitnessTable.saveWitness(model);
                    Log.e("audio_url"," is "+audioPlayerName);

                    witness_arrayList.add(new AccidentWitnessModel(inserted_id,witness_name_edittext.getText().toString(),
                            witness_phone_edittext.getText().toString(),witness_note_edittext.getText().toString(),
                            AppController.accident_report_id,sessionManager.get_driver_id(),audioPlayerName));
                   // witness_arrayList.addAll(previous_records);
                   // adapter = new AccidentWitnessListAdapter(UpdateAccidentWitnessData.this,witness_arrayList);
                   // witnessListView.notifyAll();
                   // witnessListView.setAdapter(adapter);
                    previous_records.add(model);
                    adapter.notifyDataSetChanged();
                    witnessAlertDialog.dismiss();

                    audioPlayerName = "";

                    Toast.makeText(UpdateAccidentWitnessData.this,"Witness Saved",Toast.LENGTH_LONG).show();
                }

            }
        });


        witnessAlertDialog = dialogBuilder.create();
        witnessAlertDialog.setCancelable(false);
        //emailAlertDialog.setTitle("Send Documents By Email");
        witnessAlertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(witnessAlertDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        witnessAlertDialog.getWindow().setAttributes(lp);

    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    public void startPlaying() {

        try {

            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
                play_img.setBackground(getResources().getDrawable(R.drawable.ic_play));
            }


            if (!wasPlaying) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                play_img.setBackground(getResources().getDrawable(R.drawable.ic_pause));
                mediaPlayer.setDataSource(audioPlayerName);
                mediaPlayer.setOnCompletionListener(completionListener);
                mediaPlayer.prepare();
                // mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(false);
                seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
                new Thread(this).start();

            }

            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void record_functionality(){
        isRecording = !isRecording;

        onRecord(isRecording);
        if(isRecording){

            record_layout.setBackgroundColor(getResources().getColor(R.color.red_dark)); // stop recording
            mic_img.setBackground(getResources().getDrawable(R.drawable.ic_stop));

        }
        else{
            chronometer.setBase(chronometer.getBase());
            chronometer.stop();
            record_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            mic_img.setBackground(getResources().getDrawable(R.drawable.ic_mic_white)); // start recording
        }
        play_img.setEnabled(!isRecording);
    }

    private void onRecord(boolean start) {
        if (start) {
            String file_name = uniqueFileName();
            audioPlayerName = myDir+"/"+file_name;
            startRecording();
        } else {
            stopRecording();
        }
    }


    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            stopPlaying();
        }
    };

    private void startRecording() {

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // ACC_ELD is supported only from SDK 16+.
        // You can use other encoders for lower vesions.
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //recorder.setAudioSamplingRate(44100);
        //recorder.setAudioEncodingBitRate(96000);
        recorder.setOutputFile(audioPlayerName);

        try {
            recorder.prepare();
            recorder.start();
            chronometer.setVisibility(View.VISIBLE);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.release();
            completionRecording();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(witnessAlertDialog != null){
            witnessAlertDialog.dismiss();

        }
        clearMediaPlayer();
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            completionPlaying();
        }
    }

    private void clearMediaPlayer() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    private void completionPlaying() {
        reset();
        play_img.setBackground(getResources().getDrawable(R.drawable.ic_play));
        mic_img.setEnabled(true);
    }

    private void reset() {
        isRecording = false;
        isPlaying = false;
    }

    private void completionRecording() {
        reset();

        record_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mic_img.setBackground(getResources().getDrawable(R.drawable.ic_mic_white)); // start recording

        play_img.setBackground(getResources().getDrawable(R.drawable.ic_play));
        play_img.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
        stopPlaying();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRecording();
        stopPlaying();
    }


    @Bind(R.id.list_view_witness)
    ListView witnessListView;

    @OnClick(R.id.add_btn)
    void showDialog(){
        showPopUp();
    }

    @OnClick(R.id.next_btn)
    void goToNext(){
        Intent i = new Intent(UpdateAccidentWitnessData.this,UpdateAccidentCaptureScreen.class);
        i.putExtra("accident_id",accident_id);
        startActivity(i);
    }

    @Override
    public void run() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();

        while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mediaPlayer.getCurrentPosition();

            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }

    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;
        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            switch(view.getId()){
                case R.id.witness_name_edittext:
                    String str1 = witness_name_edittext.getText().toString();
                    if(str1 != "" && str1.length() > 0){
                        error_witness_name.setVisibility(View.GONE);
                    }
                    else{
                        error_witness_name.setVisibility(View.VISIBLE);
                    }

                    break;
                case R.id.witness_phone_edittext:
                    String str2 = witness_phone_edittext.getText().toString();
                    if(str2 != "" && str2.length() > 0){
                        error_witness_phone.setVisibility(View.GONE);
                    }
                    else{
                        error_witness_phone.setVisibility(View.VISIBLE);
                    }

                    break;
                case R.id.witness_note_edittext:
                    String str3 = witness_note_edittext.getText().toString();
                    if(str3 != "" && str3.length() > 0){
                        error_witness_statement.setVisibility(View.GONE);
                    }
                    else{
                        error_witness_statement.setVisibility(View.VISIBLE);
                    }

                    break;

            }
        }
    }
}
