package com.hunglevan.musicplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.gson.Gson;
import com.hunglevan.musicplayer.adapter.Callback;
import com.hunglevan.musicplayer.adapter.SongAdapter;
import com.hunglevan.musicplayer.fragment.PlayerFragment;
import com.hunglevan.musicplayer.model.Song;
import com.hunglevan.musicplayer.utils.Utils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Utils utils;
    private ArrayList<Song> songArrayList;
    private SongAdapter songAdapter;
    private int selectIndex;
    public static final String _POSITION = "position";
    public static final String KEY_SONG = "song";
    public static final String IS_SMALL = "is small";
    public static final int REQUEST_CODE = 61;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSong();
        setSongRecycleView();
        sendDataToFragment(songArrayList, selectIndex);

    }

    private void getSong(){
        utils = new Utils();
        utils.getSong(this);
        songArrayList = utils.getSongArrayList();
    }


    private void setSongRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.songRecycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        songAdapter = new SongAdapter(utils.getSongArrayList(), new Callback() {
            @Override
            public void onItemClick(int index, ArrayList<Song> songArrayList) {
                openDetailActivity(index, songArrayList);
                startMusicPlayer(index, songArrayList);
            }
        });
        recyclerView.setAdapter(songAdapter);
    }

    private String setGsonObject(ArrayList<Song> songArrayList){
        Gson gson = new Gson();
        String songString = gson.toJson(songArrayList);
        return songString;
    }
    private void openDetailActivity(int position, ArrayList<Song> songArrayList){
        String songString = setGsonObject(songArrayList);
        selectIndex = position;
        Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra(KEY_SONG, songString);
        intent.putExtra(_POSITION, position);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null){
            String songString = data.getStringExtra(KEY_SONG);
            selectIndex = data.getIntExtra(_POSITION, 0);
            setupFragmentForBackPress(songString, selectIndex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setupSearchView(menu);
        return true;
    }

    private void setupSearchView(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search Songs Here");
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        // Here is where we are going to implement the filter logic
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            songAdapter.getFilter().filter("");
        } else {
            songAdapter.getFilter().filter(s);
        }
        return true;
    }

    private void sendDataToFragment(ArrayList<Song> songArrayList, int position){
        String songString = setGsonObject(songArrayList);
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SONG, songString);
        bundle.putBoolean(IS_SMALL, true);
        bundle.putInt(_POSITION, position);
        playerFragment.setArguments(bundle);
        fragmentManager.beginTransaction().add(R.id.frSmallController, playerFragment).commit();
    }

    private void setupFragmentForBackPress(String songString, int position){
        FragmentManager fragmentManager = getSupportFragmentManager();
        PlayerFragment playerFragment = new PlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SONG, songString);
        bundle.putBoolean(IS_SMALL, true);
        bundle.putInt(_POSITION, position);
        playerFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.frSmallController, playerFragment).commit();
    }

    private void startMusicPlayer(int position, ArrayList<Song> songArrayList){
        String songString = setGsonObject(songArrayList);
        Intent intent = new Intent(this, MusicPlayerService.class);
        intent.putExtra(KEY_SONG, songString);
        intent.putExtra(_POSITION, position);
        startService(intent);
    }

}