package com.hunglevan.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.hunglevan.musicplayer.fragment.PlayerFragment;


public class PlayerActivity extends AppCompatActivity {

    int position;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        setupFragment(getDataIntent());
        setDataIntent(getDataIntent());
    }


    private String getDataIntent() {
        String songString = getIntent().getStringExtra(MainActivity.KEY_SONG);
        position = getIntent().getIntExtra(MainActivity._POSITION, 0);
        return songString;
    }

    private void setDataIntent(String songString) {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.KEY_SONG, songString);
        intent.putExtra(MainActivity._POSITION, position);
        setResult(RESULT_OK, intent);
    }

    private void setupFragment(String songString) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.KEY_SONG, songString);
        bundle.putBoolean(MainActivity.IS_SMALL, false);
        bundle.putInt(MainActivity._POSITION, position);
        playerFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.frBigController, playerFragment).commit();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
