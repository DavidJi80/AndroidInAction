package com.github.davidji80.contentresolver.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class VideoFrameRecyclerView extends RecyclerView {
    private String TAG = VideoFrameRecyclerView.class.getSimpleName();

    public VideoFrameRecyclerView(@NonNull Context context) {
        super(context);
    }

    public VideoFrameRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoFrameRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.e(TAG, "dispatchTouchEvent");
        return super.dispatchTouchEvent(ev);
        //return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        //Log.e(TAG, "onInterceptTouchEvent");
        //return super.onInterceptTouchEvent(e);
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //Log.e(TAG, "onTouchEvent");
        return super.onTouchEvent(e);
    }
}
