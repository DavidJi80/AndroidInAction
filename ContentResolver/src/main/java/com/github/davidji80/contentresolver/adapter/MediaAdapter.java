package com.github.davidji80.contentresolver.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.davidji80.contentresolver.R;
import com.github.davidji80.contentresolver.model.AudioInfo;
import com.github.davidji80.contentresolver.model.ImageInfo;
import com.github.davidji80.contentresolver.model.MediaInfo;
import com.github.davidji80.contentresolver.model.VideoInfo;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.VH> {
    //数据源
    private List<MediaInfo> mDatas;

    private Context context;


    public MediaAdapter(List<MediaInfo> data, Context context) {
        this.mDatas = data;
        this.context=context;
    }



    /**
     * 为每个Item inflater出一个View
     * 该方法把View直接封装在ViewHolder中，并返回这个ViewHolder
     *
     * @param parent
     * @param viewType
     * @return 该方法返回的是一个ViewHolder
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_media, parent, false));
    }

    /**
     * 适配渲染数据到View中
     * 方法提供给你了一ViewHolder，而不是BaseAdapter中的View
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (mDatas.get(position) instanceof VideoInfo){
            VideoInfo videoInfo= (VideoInfo) mDatas.get(position);
            holder.tv.setText(videoInfo.filePath);
            Glide.with(context).load(videoInfo.thumbnail).into(holder.ivShowImg);
        }else if (mDatas.get(position) instanceof ImageInfo){
            ImageInfo imageInfo= (ImageInfo) mDatas.get(position);
            holder.tv.setText(imageInfo.filePath);
            Glide.with(context).load(imageInfo.thumbnail).into(holder.ivShowImg);
        }else if (mDatas.get(position) instanceof AudioInfo){
            AudioInfo audioInfo= (AudioInfo) mDatas.get(position);
            holder.tv.setText(audioInfo.filePath);
        }

    }

    /**
     * 类似于BaseAdapter的getCount方法了，即总共有多少个条目
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     * 在Adapter中创建一个继承RecyclerView.ViewHolder的静态内部类
     * ViewHolder的实现和ListView的ViewHolder实现几乎一样
     */
    public static class VH extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView ivShowImg;

        public VH(View view) {
            super(view);
            tv = (TextView) view.findViewById(R.id.id_num);
            ivShowImg = view.findViewById(R.id.ivShowImg);
        }
    }
}
