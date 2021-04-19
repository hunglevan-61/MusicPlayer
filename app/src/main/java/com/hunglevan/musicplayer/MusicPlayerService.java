package com.hunglevan.musicplayer;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hunglevan.musicplayer.fragment.PlayerFragment;
import com.hunglevan.musicplayer.model.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private ArrayList<Song> songArrayList;
    int indexSong = -1;
    public static final String ACTION_SEND_SONG_DATA = "com.hunglevan.musicplayer.SEND_SONG_DATA";
    public static final String ACTION_PLAY  = "com.hunglevan.musicplayer.PLAY";
    public static final String ACTION_PAUSE  = "com.hunglevan.musicplayer.PAUSE";
    public static final String INDEX_SONG = "IndexSong";
    public static final String PROGRESS = "Progress";
    Handler handler;
    private FragmentBroadcastReceiver fragmentBroadcastReceiver = new FragmentBroadcastReceiver();
    IntentFilter intentFilter;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        registerReceiver();
        Runnable mServiceMessage = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
//                    sendProgress(mediaPlayer.getCurrentPosition());
                    sendIndexSong(indexSong);
                    handler.postDelayed(this::run, 1000);
                }
            }
        };
        handler.postDelayed(mServiceMessage, 1000);
    }

    private void registerReceiver(){
        intentFilter = new IntentFilter(PlayerFragment.ACTION_SEND_PROGRESS);
        intentFilter.addAction(PlayerFragment.ACTION_NEXT);
        intentFilter.addAction(PlayerFragment.ACTION_PLAY_AND_PAUSE);
        intentFilter.addAction(PlayerFragment.ACTION_PREVIOUS);
        registerReceiver(fragmentBroadcastReceiver, intentFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSongData(intent);
        playMusic(songArrayList.get(indexSong).getUrl());
        return super.onStartCommand(intent, flags, startId);
    }

    private void getSongData(Intent intent) {
        String songString = intent.getStringExtra(MainActivity.KEY_SONG);
        indexSong = intent.getIntExtra(MainActivity._POSITION, 0);
        Gson gson = new Gson();
        TypeToken<ArrayList<Song>> token = new TypeToken<ArrayList<Song>>() {};
        songArrayList = gson.fromJson(songString, token.getType());
    }

    private void playMusic(String url) {
        try {
            if(mediaPlayer != null){
                mediaPlayer.reset();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
            sendActionPlayAndPause(ACTION_PLAY);
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        indexSong++;
        playMusic(songArrayList.get(indexSong).getUrl());
    }

    public void sendIndexSong(int indexSong){
        Intent intent = new Intent(ACTION_SEND_SONG_DATA);
        intent.putExtra(INDEX_SONG, indexSong);
        intent.putExtra(PROGRESS, mediaPlayer.getCurrentPosition());
        sendBroadcast(intent);
    }

//    private void sendProgress(int currentDuration){
//        Intent intent = new Intent(ACTION_SEND_PROGRESS);
//        intent.putExtra(PROGRESS, currentDuration);
//        Log.e("Hello", "Xin chao cac ban");
//        sendBroadcast(intent);
//    }

    class FragmentBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case PlayerFragment.ACTION_SEND_PROGRESS:
                    int currentProgress = intent.getIntExtra(PlayerFragment.PROGRESS_CHANGE, 0);
                    if(mediaPlayer != null){
                        mediaPlayer.seekTo(currentProgress);
                    }
                    break;
                case PlayerFragment.ACTION_NEXT:
                    indexSong++;
                    playMusic(songArrayList.get(indexSong).getUrl());
                    break;
                case PlayerFragment.ACTION_PLAY_AND_PAUSE:
                    playAndPause();
                    break;
                case PlayerFragment.ACTION_PREVIOUS:
                    indexSong--;
                    playMusic(songArrayList.get(indexSong).getUrl());
                    break;
            }
        }
    }

    private void playAndPause(){
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                sendActionPlayAndPause(ACTION_PAUSE);
            } else{
                mediaPlayer.start();
                sendActionPlayAndPause(ACTION_PLAY);
            }
        }
    }

    private void sendActionPlayAndPause(String action){
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(fragmentBroadcastReceiver);
    }
}
