package com.hunglevan.musicplayer.adapter;

import com.hunglevan.musicplayer.model.Song;

import java.util.ArrayList;

public interface Callback {

    void onItemClick(int index, ArrayList<Song> songArrayList);
}