package com.example.mapsexercise;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout{

    protected GestureDetector.SimpleOnGestureListener mGestureListener;

    private GestureDetectorCompat mDetector;

    public TouchableWrapper(Context context) {

        super(context);

        mGestureListener = new MapGestureListener(context);
        mDetector = new GestureDetectorCompat(context,mGestureListener);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        mDetector.onTouchEvent(event);

        return super.dispatchTouchEvent(event);
    }
}
