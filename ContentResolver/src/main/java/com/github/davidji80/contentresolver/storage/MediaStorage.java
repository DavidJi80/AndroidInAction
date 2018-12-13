package com.github.davidji80.contentresolver.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.github.davidji80.contentresolver.R;
import com.github.davidji80.contentresolver.model.AudioInfo;
import com.github.davidji80.contentresolver.model.ImageInfo;
import com.github.davidji80.contentresolver.model.MediaInfo;
import com.github.davidji80.contentresolver.model.VideoInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaStorage {
    private final String[] mediaTypes = {"视频", "图片", "音频"};

    private Context context;

    public MediaStorage(Context context) {
        this.context = context;
    }

    public List getMediaTypeList() {
        List<Map<String, Object>> lists = new ArrayList<>();
        for (int i = 0; i < mediaTypes.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("TypeImg", R.mipmap.ic_launcher);
            map.put("TypeName", mediaTypes[i]);
            lists.add(map);
        }
        return lists;
    }


    /**
     * 获取视频列表
     */
    public List<MediaInfo> getVideoList() {
        List<MediaInfo> list = new ArrayList();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;
        String selection = null;
        //MediaStore.Video.Media.MIME_TYPE + "=? or " + MediaStore.Video.Media.MIME_TYPE + "=?";
        String[] selectionArgs = null;
        //new String[]{"video/mp4", "video/avi"};
        String sortOrder = null;
        //MediaStore.Video.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        Cursor thumbCursor = null;
        while (cursor.moveToNext()) {
            MediaInfo videoInfo = new VideoInfo();
            videoInfo.id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            videoInfo.filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            videoInfo.mimeType= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            videoInfo.title= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
            videoInfo.addTime=cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
            ((VideoInfo) videoInfo).duration=cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            thumbCursor = contentResolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Video.Thumbnails.DATA,
                            MediaStore.Video.Thumbnails.VIDEO_ID
                    },
                    MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
                    new String[]{String.valueOf(videoInfo.id)}, null);

            if (thumbCursor.moveToFirst()) {
                ((VideoInfo) videoInfo).thumbnail = thumbCursor.getString(
                        thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
            }
            list.add(videoInfo);
        }
        thumbCursor.close();
        cursor.close();
        return list;
    }

    public List<MediaInfo> getImageList() {
        List<MediaInfo> list = new ArrayList();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        Cursor thumbCursor = null;
        while (cursor.moveToNext()) {
            MediaInfo imageInfo = new ImageInfo();
            imageInfo.id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            imageInfo.filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imageInfo.mimeType= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
            imageInfo.title= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
            imageInfo.addTime=cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
            thumbCursor = contentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    new String[]{
                            MediaStore.Images.Thumbnails.DATA,
                            MediaStore.Images.Thumbnails.IMAGE_ID
                    },
                    MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                    new String[]{String.valueOf(imageInfo.id)}, null);

            if (thumbCursor.moveToFirst()) {
                ((ImageInfo) imageInfo).thumbnail = thumbCursor.getString(
                        thumbCursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails.DATA));
            }
            list.add(imageInfo);
        }
        thumbCursor.close();
        cursor.close();
        return list;
    }

    public List<MediaInfo> getAudioList() {
        List<MediaInfo> list = new ArrayList();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
        while (cursor.moveToNext()) {
            MediaInfo audioInfo = new AudioInfo();
            audioInfo.id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
            audioInfo.filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            audioInfo.mimeType= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE));
            audioInfo.title= cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            audioInfo.addTime=cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED));
            list.add(audioInfo);
        }
        cursor.close();
        return list;
    }
}
