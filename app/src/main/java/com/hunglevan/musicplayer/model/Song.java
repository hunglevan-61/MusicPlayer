package com.hunglevan.musicplayer.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;

public class Song {

    private String title;
    private String artistName;
    private long duration;
    private Bitmap artistProfile;
    private String url;

    public Song(String title, String artistName, long duration, Bitmap artistProfile, String url) {
        this.title = title;
        this.artistName = artistName;
        this.duration = duration;
        this.artistProfile = artistProfile;
        this.url = url;
    }


    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    public long getDuration() {
        return duration;
    }

    public Bitmap getArtistProfile() {
        return artistProfile;
    }

    public String getUrl() {
        return url;
    }

    public String getDurationFormat(int duration) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        String timeText = simpleDateFormat.format(duration);
        return timeText;
    }

}
