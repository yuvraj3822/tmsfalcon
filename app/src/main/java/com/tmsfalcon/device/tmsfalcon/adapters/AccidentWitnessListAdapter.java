package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.CallLogDetail;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;
import com.tmsfalcon.device.tmsfalcon.entities.SuggestedLoadsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Android on 8/23/2017.
 */
public class AccidentWitnessListAdapter extends BaseAdapter{

    private ArrayList<AccidentWitnessModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    SessionManager sessionManager;
    private boolean isPlaying = false;
    boolean wasPlaying = false;
    private MediaPlayer mediaPlayer = null;
    String audioPlayerName;
    AccidentWitnessTable accidentWitnessTable;
    AccidentWitnessListAdapter.ViewHolder holder = null;


    public AccidentWitnessListAdapter(Activity activity, ArrayList<AccidentWitnessModel> data) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {

        return mData.get(position);
    }

    private void onPlay(boolean start,ViewHolder local_holder) {
        if (start) {
            startPlaying(local_holder);
        } else {
            stopPlaying(local_holder);
        }
    }

    private void stopPlaying(ViewHolder local_holder) {
       if (mediaPlayer != null) {
            mediaPlayer.reset();
            isPlaying = false;

            local_holder.play_img.setBackground(activity.getResources().getDrawable(R.drawable.ic_play));
        }
    }

    public void startPlaying(final ViewHolder local_holder) {

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                local_holder.seekBar.setProgress(0);
                wasPlaying = true;
                local_holder.play_img.setBackground(activity.getResources().getDrawable(R.drawable.ic_play));
            }


            if (!wasPlaying) {

                if (mediaPlayer == null) {
                    mediaPlayer = new MediaPlayer();
                }

                local_holder.play_img.setBackground(activity.getResources().getDrawable(R.drawable.ic_pause));
                mediaPlayer.setDataSource(audioPlayerName);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        stopPlaying(local_holder);
                    }
                });
                mediaPlayer.prepare();
                // mediaPlayer.setVolume(0.5f, 0.5f);
                mediaPlayer.setLooping(false);
                local_holder.seekBar.setMax(mediaPlayer.getDuration());

                mediaPlayer.start();
                new Thread(new Runnable() {
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
                            local_holder.seekBar.setProgress(currentPosition);
                        }
                    }
                }).start();

            }

            wasPlaying = false;
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        final AccidentWitnessModel model = mData.get(position);
        accidentWitnessTable = new AccidentWitnessTable(activity);
        if (convertView == null) {
            holder = new AccidentWitnessListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_accident_witness, null);

            holder.witness_name_textview = convertView.findViewById(R.id.witness_name);
            holder.witness_phone_textview = convertView.findViewById(R.id.witness_phone);
            holder.witness_statement_textview = convertView.findViewById(R.id.witness_statement);
            holder.audio_layout = convertView.findViewById(R.id.audio_layout);
            holder.seekBar = convertView.findViewById(R.id.seekbar);
            holder.play_img = convertView.findViewById(R.id.play_img);
            holder.cancel_img = convertView.findViewById(R.id.cancel_icon);

            convertView.setTag(holder);
        } else {

            holder = (AccidentWitnessListAdapter.ViewHolder)convertView.getTag();

        }

        convertView.setId(model.getId());
        holder.witness_name_textview.setText(model.getWitness_name());
        holder.witness_phone_textview.setText(model.getWitness_phone());
        holder.witness_statement_textview.setText(model.getWitness_statement());
        if (model.getWitness_audio_url() != "" && model.getWitness_audio_url().length() > 0) {
            holder.audio_layout.setVisibility(View.VISIBLE);
            holder.play_img.setTag(holder);
            holder.play_img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    ViewHolder local_holder = (ViewHolder) view.getTag();
                    Log.e("url"," is "+model.getWitness_audio_url());
                    isPlaying = !isPlaying;
                    audioPlayerName = model.getWitness_audio_url();
                    onPlay(isPlaying,local_holder);
                    if (isPlaying) {
                        local_holder.play_img.setBackground(activity.getResources().getDrawable(R.drawable.ic_pause)); //Pause Button
                    } else {
                        local_holder.play_img.setBackground(activity.getResources().getDrawable(R.drawable.ic_play)); //Play Button
                    }
                }
            });
        }  else {
            holder.audio_layout.setVisibility(View.GONE);
        }

        holder.cancel_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = model.getId();
                mData.remove(model);
                notifyDataSetChanged();
                accidentWitnessTable.deleteWitnessByPrimaryId(id);
            }
        });


        return convertView;
    }
    public static class ViewHolder {
        TextView witness_name_textview,witness_phone_textview,witness_statement_textview;
        LinearLayout audio_layout;
        SeekBar seekBar;
        Button play_img;
        ImageView cancel_img;

    }

}
