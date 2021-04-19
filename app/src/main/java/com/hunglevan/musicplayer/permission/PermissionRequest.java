package com.hunglevan.musicplayer.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionRequest {

    public static final int PERMISSION_REQUEST_READ = 1;

    public  boolean hasStoragePermission(Activity activity) {
        int permission = ContextCompat.checkSelfPermission(
                activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission == PackageManager.PERMISSION_GRANTED;
    }

    public  void checkPermission(Activity activity, PermissionCallback permissionCallback) {
        boolean hasPermission = hasStoragePermission(activity);
        if (hasPermission) {
            permissionCallback.permissionGranted();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallback permissionCallback) {
        if (requestCode == PERMISSION_REQUEST_READ
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionCallback.permissionGranted();
        }
    }

}
