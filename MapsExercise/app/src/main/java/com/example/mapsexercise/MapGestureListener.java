package com.example.mapsexercise;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector.SimpleOnGestureListener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class MapGestureListener extends SimpleOnGestureListener implements GoogleMap.OnMapLongClickListener {

    public static final String LOG_TAG = MapGestureListener.class.getSimpleName();
    private MapsActivity mapsActivity;

    public MapGestureListener(Context context){
        mapsActivity = (MapsActivity) context;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mapsActivity.switchMapType(MapsActivity.currentMapType);
        Log.v(LOG_TAG,"long Click detected Map: " +latLng.toString());
    }

}