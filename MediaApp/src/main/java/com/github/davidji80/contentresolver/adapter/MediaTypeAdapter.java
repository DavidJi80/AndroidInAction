package com.github.davidji80.contentresolver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.davidji80.contentresolver.R;

import java.util.List;
import java.util.Map;

public class MediaTypeAdapter extends BaseAdapter {
    //数据源
    private List<Map<String,Object>> datas;
    //上下文
    private Context context;

    public MediaTypeAdapter(List datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        //如果view未被实例化过，缓存池中没有对应的缓存
        if (view==null) {
            viewHolder=new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.view_item_media_type, viewGroup, false);
            //对viewHolder的属性进行赋值
            viewHolder.ivTypeImg= view.findViewById(R.id.ivTypeImg);
            viewHolder.tvTypeName =  view.findViewById(R.id.tvTypeName);
            //通过setTag将view与viewHolder关联
            view.setTag(viewHolder);
        }else{
            //如果缓存池中有对应的view缓存，则直接通过getTag取出viewHolder
            viewHolder= (ViewHolder) view.getTag();
        }
        // 设置控件的数据
        viewHolder.ivTypeImg.setImageResource((Integer)datas.get(i).get("TypeImg"));
        viewHolder.tvTypeName.setText((String)datas.get(i).get("TypeName"));
        return view;
    }

    private final class ViewHolder {
        private ImageView ivTypeImg;
        private TextView tvTypeName;
    }
}
