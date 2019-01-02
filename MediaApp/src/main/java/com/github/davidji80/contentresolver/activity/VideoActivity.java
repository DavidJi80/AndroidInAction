package com.github.davidji80.contentresolver.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.github.davidji80.contentresolver.R;
import com.github.davidji80.contentresolver.adapter.VideoFrameAdapter;

import java.io.IOException;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class VideoActivity extends AppCompatActivity {
    private String TAG = VideoActivity.class.getSimpleName();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("FILE_PATH");

        //取帧
        RecyclerView mRecyclerView = findViewById(R.id.rv_video_frame);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        final VideoFrameAdapter videoFrameAdapter = new VideoFrameAdapter(filePath);
        mRecyclerView.setAdapter(videoFrameAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                String state = "";
                if (newState == SCROLL_STATE_IDLE) {
                    state = "SCROLL_STATE_IDLE";
                    videoFrameAdapter.execFrameAsyncTask();
                } else if (newState == SCROLL_STATE_DRAGGING) {
                    state = "SCROLL_STATE_DRAGGING";
                } else if (newState == SCROLL_STATE_SETTLING) {
                    state = "SCROLL_STATE_SETTLING";
                }
                //Log.e(TAG, "onViewRecycled:" + state);
            }
        });

        palyVideo(filePath);

    }

    /**
     * 播放视频
     */
    private void palyVideo(String filePath) {
        surfaceView = findViewById(R.id.sv_video);
        //surfaceHolder可以通过surfaceview的getHolder()方法获得
        surfaceHolder = surfaceView.getHolder();
        //给surfaceHolder设置一个callback
        surfaceHolder.addCallback(new SHCallBack());
        mediaPlayer = new MediaPlayer();
        try {
            //设置要播放的资源，可以是文件、文件路径、或者URL
            mediaPlayer.setDataSource(this, Uri.parse(filePath));
            //调用MediaPlayer.prepare()来准备
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //调用MediaPlayer.start()来播放视频
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class SHCallBack implements SurfaceHolder.Callback {
        /**
         * 在SurfaceHolder被创建的时候回调，
         * 在这里可以做一些初始化的操作
         *
         * @param holder
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            //调用MediaPlayer.setDisplay(holder)设置surfaceHolder，surfaceHolder可以通过surfaceview的getHolder()方法获得
            mediaPlayer.setDisplay(holder);
            Log.e(TAG,"surfaceCreated");
        }

        /**
         * 当SurfaceHolder的尺寸发生变化的时候被回调
         *
         * @param holder
         * @param format
         * @param width
         * @param height
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        /**
         * surfaceDestroyed()会在SurfaceHolder被销毁的时候回调，
         * 在这里可以做一些释放资源的操作，防止内存泄漏。
         *
         * @param holder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e(TAG,"surfaceDestroyed");
            //mediaPlayer.pause();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //Log.e(TAG, "dispatchTouchEvent" /*+ ev.toString()*/);
        return super.dispatchTouchEvent(ev);
        //return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.e(TAG, "onTouchEvent" /*+ event.toString()*/);
        return super.onTouchEvent(event);
    }


}
