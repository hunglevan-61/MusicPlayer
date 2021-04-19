package com.hunglevan.musicplayer.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hunglevan.musicplayer.MainActivity;
import com.hunglevan.musicplayer.MusicPlayerService;
import com.hunglevan.musicplayer.R;
import com.hunglevan.musicplayer.model.Song;

import java.util.ArrayList;

public class PlayerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    public static final String ACTION_SEND_PROGRESS = "com.hunglevan.musicplayer.SEND_PROGRESS";
    public static final String PROGRESS_CHANGE = "ProgressChange";
    public static final String ACTION_NEXT = "com.hunglevan.musicplayer.NEXT";
    public static final String ACTION_PLAY_AND_PAUSE = "com.hunglevan.musicplayer.PLAY_AND_PAUSE";
    public static final String ACTION_PREVIOUS = "com.hunglevan.musicplayer.PREVIOUS";
    private ArrayList<Song> songs;
    private int position = -1;
    private boolean isSmall = true;
    ImageView artistProfile;
    TextView txtTitle, txtArtist, txtProgressDuration, txtDuration;
    SeekBar pgDuration;
    ImageButton btnNext, btnPrevious, btnPlay;
    private ServiceBroadcastReceiver mServiceBroadcastReceiver = new ServiceBroadcastReceiver();
    IntentFilter intentFilter;
    private int currentProgress = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();
    }

    private void registerReceiver(){
        intentFilter = new IntentFilter(MusicPlayerService.ACTION_SEND_SONG_DATA);
        intentFilter.addAction(MusicPlayerService.ACTION_PLAY);
        intentFilter.addAction(MusicPlayerService.ACTION_PAUSE);
        getActivity().registerReceiver(mServiceBroadcastReceiver, intentFilter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isSmall = getArguments().getBoolean(MainActivity.IS_SMALL);
        int layout = isSmall ? R.layout.small_controller : R.layout.big_controller;
        View view = inflater.inflate(layout, container, false);
        getSong();
        mapIdToView(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void getSong() {
        String songString = getArguments().getString(MainActivity.KEY_SONG);
        position = getArguments().getInt(MainActivity._POSITION);
        Gson gson = new Gson();
        TypeToken<ArrayList<Song>> token = new TypeToken<ArrayList<Song>>() {
        };
        songs = gson.fromJson(songString, token.getType());
    }

    private void mapIdToView(View view) {
        Song song = songs.get(position);

        txtTitle = view.findViewById(R.id.txtSong);
        txtTitle.setText(song.getTitle());

        artistProfile = view.findViewById(R.id.profile_artist);
        artistProfile.setImageBitmap(song.getArtistProfile());

        txtArtist = view.findViewById(R.id.txtArtist);
        txtArtist.setText(song.getArtistName());

        btnNext = view.findViewById(R.id.btnNext);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPrevious = view.findViewById(R.id.btnPrevious);

        btnNext.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);

        if (!isSmall) {
            txtProgressDuration = view.findViewById(R.id.txtProgressDuration);
            txtProgressDuration.setText(song.getDurationFormat(currentProgress));

            txtDuration = view.findViewById(R.id.txtDuration);
            txtDuration.setText(song.getDurationFormat((int) song.getDuration()));

            pgDuration = view.findViewById(R.id.pgDuration);
            pgDuration.setOnSeekBarChangeListener(this);
            pgDuration.setMax((int) (song.getDuration()/1000));
            pgDuration.setProgress(currentProgress/1000);
        }


    }

    public void updateUI(int indexSong, int currentProgress) {
        Song song = songs.get(indexSong);
        txtTitle.setText(song.getTitle());
        artistProfile.setImageBitmap(song.getArtistProfile());
        txtArtist.setText(song.getArtistName());

        if (!isSmall) {
            txtProgressDuration.setText(song.getDurationFormat(currentProgress));
            txtDuration.setText(song.getDurationFormat((int) song.getDuration()));
            pgDuration.setMax((int) (song.getDuration()/1000));
            pgDuration.setProgress(currentProgress/1000);
        }
    }

    @Override
    public void onProgressChanged(SeekBar pro, int progress, boolean b) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        sendProgressChange(seekBar.getProgress()*1000);
    }

    private void sendProgressChange(int currentProgressChange){
        Intent intent = new Intent(ACTION_SEND_PROGRESS);
        intent.putExtra(PROGRESS_CHANGE, currentProgressChange);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnNext:
                sendActionButton(ACTION_NEXT);
                break;
            case R.id.btnPlay:
                sendActionButton(ACTION_PLAY_AND_PAUSE);
                break;
            case R.id.btnPrevious:
                sendActionButton(ACTION_PREVIOUS);
                break;
        }
    }

    private void sendActionButton(String action){
        Intent intent = new Intent(action);
        getActivity().sendBroadcast(intent);
    }

    class ServiceBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case MusicPlayerService.ACTION_SEND_SONG_DATA:
                    int indexSong = intent.getIntExtra(MusicPlayerService.INDEX_SONG, 0);
                    if (indexSong != position) {
                        position = indexSong;
                    }
                    currentProgress = intent.getIntExtra(MusicPlayerService.PROGRESS, 0);
                    updateUI(position, currentProgress);
                    break;
                case MusicPlayerService.ACTION_PLAY:
                    Log.e("Played","Music is Played");
                    btnPlay.setImageResource(R.drawable.ic_pause);
                    break;
                case MusicPlayerService.ACTION_PAUSE:
                    Log.e("Paused","Music is Paused");
                    btnPlay.setImageResource(R.drawable.ic_play);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mServiceBroadcastReceiver);
    }
}
