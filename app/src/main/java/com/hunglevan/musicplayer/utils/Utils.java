package com.hunglevan.musicplayer.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.hunglevan.musicplayer.model.Song;
import com.hunglevan.musicplayer.permission.PermissionCallback;
import com.hunglevan.musicplayer.permission.PermissionRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class Utils {

    private ArrayList<Song> songArrayList;

    public void getSong(Activity activity) {
        songArrayList = new ArrayList<>();
        PermissionRequest permissionRequest = new PermissionRequest();
        permissionRequest.checkPermission(activity, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                ContentResolver contentResolver = activity.getContentResolver();
                Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Cursor songCursor = contentResolver.query(songUri, null, null, null, null);
                if(songCursor != null && songCursor.moveToFirst()){
                    int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int songDuration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                    int songAlbum = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                    int songUrl = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                    do{
                        String currentTitle = songCursor.getString(songTitle);
                        String currentArtist = songCursor.getString(songArtist);
                        long currentDuration = songCursor.getLong(songDuration);
                        int album_id = songCursor.getInt(songAlbum);
                        String currentUrl = songCursor.getString(songUrl);

                        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
                        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
                        ContentResolver res = activity.getContentResolver();
                        InputStream in = null;
                        try {
                            in = res.openInputStream(uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap artistProfile = BitmapFactory.decodeStream(in);

                        Song song = new Song(currentTitle, currentArtist, currentDuration, artistProfile, currentUrl);
                        songArrayList.add(song);

                    } while (songCursor.moveToNext());

                    songCursor.close();
                }

            }
        });
    }

    public ArrayList<Song> getSongArrayList() {
        return songArrayList;
    }
}
