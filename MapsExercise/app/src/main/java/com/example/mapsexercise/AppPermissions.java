package com.example.mapsexercise;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class AppPermissions {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String TAG = "AppPermissions";
    /**
     * Returns true if locations permission have been granted.
     * Otherwise return false, and asynchronously request permissions
     */
    public static boolean getLocationPermission(Context context) {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(context,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(context,
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                return true;
            }else{
                ActivityCompat.requestPermissions((Activity) context,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
                return false;
            }
        }else{
            ActivityCompat.requestPermissions((Activity) context,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
    }
}
