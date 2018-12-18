package com.github.davidji80.contentresolver.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.davidji80.contentresolver.R;
import com.github.davidji80.contentresolver.adapter.MediaAdapter;
import com.github.davidji80.contentresolver.adapter.MediaTypeAdapter;
import com.github.davidji80.contentresolver.adapter.listener.OnItemClickListener;
import com.github.davidji80.contentresolver.decoration.HVItemDecoration;
import com.github.davidji80.contentresolver.model.MediaInfo;
import com.github.davidji80.contentresolver.storage.MediaStorage;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private MediaStorage mediaStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //进入全屏状态
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        //获取权限
        verifyStoragePermissions(this);

        mediaStorage = new MediaStorage(this);

        initView();
    }

    private void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void initView() {
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(view);
            }
        });

    }

    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.view_popup_media_type, null);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        final List<Map<String, Object>> lists = mediaStorage.getMediaTypeList();
        ListView lvMediaType = contentView.findViewById(R.id.lvMediaType);
        MediaTypeAdapter mediaTypeAdapter = new MediaTypeAdapter(lists, MainActivity.this);
        lvMediaType.setAdapter(mediaTypeAdapter);
        lvMediaType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String typeName = lists.get(i).get("TypeName").toString();
                popupWindow.dismiss();
                Toast.makeText(MainActivity.this, typeName, Toast.LENGTH_LONG).show();
                if (typeName.equals("视频")) {
                    showVideoList();
                } else if (typeName.equals("图片")) {
                    showImageList();
                } else if (typeName.equals("音频")) {
                    showAudioList();
                }
            }
        });

        popupWindow.showAsDropDown(view);
    }

    private void showVideoList() {
        List<MediaInfo> mData = mediaStorage.getVideoList();
        final RecyclerView mRecyclerView = findViewById(R.id.rv_1);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        MediaAdapter mediaAdapter = new MediaAdapter(mData, MainActivity.this);
        mediaAdapter.setOnItemClickLitener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                int position = mRecyclerView.getChildAdapterPosition(view);
                Toast.makeText(MainActivity.this, position + " long click",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, VideoActivity.class);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mediaAdapter);
        mRecyclerView.addItemDecoration(new HVItemDecoration(this));
    }

    private void showImageList() {
        List<MediaInfo> mData = mediaStorage.getImageList();
        RecyclerView mRecyclerView = findViewById(R.id.rv_1);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new MediaAdapter(mData, MainActivity.this));
        mRecyclerView.addItemDecoration(new HVItemDecoration(this));
    }

    private void showAudioList() {
        List<MediaInfo> mData = mediaStorage.getAudioList();
        RecyclerView mRecyclerView = findViewById(R.id.rv_1);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new MediaAdapter(mData, MainActivity.this));
        mRecyclerView.addItemDecoration(new HVItemDecoration(this));
    }


}
