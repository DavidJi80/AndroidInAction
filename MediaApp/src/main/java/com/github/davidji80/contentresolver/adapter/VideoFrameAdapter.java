package com.github.davidji80.contentresolver.adapter;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.davidji80.contentresolver.R;
import com.github.davidji80.contentresolver.model.VideoFrameInfo;

import java.util.ArrayList;
import java.util.List;

public class VideoFrameAdapter extends RecyclerView.Adapter<VideoFrameAdapter.VH> {
    private String TAG = VideoFrameAdapter.class.getSimpleName();
    //时间间隔
    private final int timeInterval = 1000;
    //数据
    private List<VideoFrameInfo> mDatas;

    private MediaMetadataRetriever retriever;

    //在窗口显示的item
    private int attachedPostion = 0;
    private int detachedPostion = 8;

    public VideoFrameAdapter(String filePath) {
        retriever = new MediaMetadataRetriever();
        retriever.setDataSource(filePath);
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int timeUnit = Integer.valueOf(time) / timeInterval;
        Bitmap bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        mDatas = new ArrayList<>();
        for (int i = 1; i <= timeUnit; i++) {
            VideoFrameInfo videoFrameInfo = new VideoFrameInfo();
            videoFrameInfo.atTime = i;
            videoFrameInfo.frame = bitmap;
            mDatas.add(videoFrameInfo);
        }
        execFrameAsyncTask();
    }

    /**
     * 1. 返回所在位置的视图类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        //Log.e(TAG, "getItemViewType:" + position);
        return super.getItemViewType(position);
    }

    /**
     * 2. 加载ViewHolder的布局
     *
     * @param viewGroup
     * @param i
     * @return
     */
    @NonNull
    @Override
    public VideoFrameAdapter.VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //Log.e(TAG, "onCreateViewHolder:" + i);
        return new VH(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_item_video_frame, viewGroup, false));
    }

    /**
     * 3. 显示指定位置数据
     *
     * @param vh
     * @param i
     */
    @Override
    public void onBindViewHolder(@NonNull VideoFrameAdapter.VH vh, int i) {
        //Log.e(TAG, "onBindViewHolder:" + vh.toString() + ",position:" + i);
        vh.ivVideoFrame.setImageBitmap(mDatas.get(i).frame);
    }

    /**
     * 4. 当Item进入这个页面的时候调用
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(@NonNull VH holder) {
        attachedPostion = holder.getLayoutPosition();
        //Log.e(TAG, "onViewAttachedToWindow:" + attachedPostion);
        super.onViewAttachedToWindow(holder);
    }

    /**
     * 5. 当Item离开这个页面的时候调用
     *
     * @param holder
     */
    @Override
    public void onViewDetachedFromWindow(@NonNull VH holder) {
        //Log.e(TAG, "onViewDetachedFromWindow:" + holder.toString());
        detachedPostion = holder.getLayoutPosition();
        super.onViewDetachedFromWindow(holder);
    }

    /**
     * 6. 当Item被回收的时候调用
     *
     * @param holder
     */
    @Override
    public void onViewRecycled(@NonNull VH holder) {
        //Log.e(TAG, "onViewRecycled:" + holder.toString());
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView ivVideoFrame;

        public VH(@NonNull View view) {
            super(view);
            ivVideoFrame = view.findViewById(R.id.iv_video_frame);
        }
    }

    public void execFrameAsyncTask() {
        //Log.e(TAG,"detachedPostion:"+detachedPostion+",attachedPostion:"+attachedPostion);
        int start, end;
        if (detachedPostion > attachedPostion) {
            start = attachedPostion;
            end = detachedPostion;
        } else {
            end = attachedPostion;
            start = detachedPostion;
        }
        List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            if (!(mDatas.get(i).hadCal)) {
                list.add(i);
            }
        }
        if (list.size() > 0)
            new FrameAsyncTask().execute(list);
    }

    private class FrameAsyncTask extends AsyncTask<List<Integer>, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(List<Integer>... integers) {
            Boolean dataChange = false;
            List<Integer> list = integers[0];
            for (int i = 0; i < list.size(); i++) {
                VideoFrameInfo videoFrameInfo = mDatas.get(list.get(i));
                if (!(videoFrameInfo.hadCal)) {
                    videoFrameInfo.frame = retriever.getFrameAtTime(videoFrameInfo.atTime * timeInterval * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    videoFrameInfo.hadCal = true;
                    mDatas.set(i, videoFrameInfo);
                    dataChange = true;
                }
                publishProgress(videoFrameInfo.atTime);
            }
            return dataChange;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Integer value0 = values[0];
            //Log.e(TAG, "AsyncTask...:" + value0);
            notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Boolean dataChange) {
            /*
            if (dataChange)
                notifyDataSetChanged();
            */
        }
    }

}
