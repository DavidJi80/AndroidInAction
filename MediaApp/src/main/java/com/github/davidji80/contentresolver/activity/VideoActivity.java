package com.github.davidji80.contentresolver.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.davidji80.contentresolver.R;
import com.github.davidji80.contentresolver.adapter.VideoFrameAdapter;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

public class VideoActivity extends AppCompatActivity {
    private String TAG = VideoActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intent = getIntent();
        String filePath = intent.getStringExtra("FILE_PATH");

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
                Log.e(TAG, "onViewRecycled:" + state);
            }
        });
    }
}
