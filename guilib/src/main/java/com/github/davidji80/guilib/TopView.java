package com.github.davidji80.guilib;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * TopView继承RelativeLayout
 */
public class TopView extends RelativeLayout {
    // 返回按钮控件
    private TextView top_left;
    // 标题
    private TextView top_title;
    // 右边按钮
    private TextView top_right;

    /**
     * 构造函数，实现父类RelativeLayout默认构造函数
     * 只能在代码中动态的添加
     *
     * @param context
     */
    public TopView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     * 可以在代码和xml中都可以用，可以动态添加或者在xml中写布局
     *
     * @param context
     * @param attrs
     */
    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 加载布局，将布局和控件绑定在一起
        LayoutInflater.from(context).inflate(R.layout.view_top, this,true);
        // 获取TextView控件
        top_left = findViewById(R.id.top_left);
        top_title = findViewById(R.id.top_title);
        top_right = findViewById(R.id.top_right);
    }

    /**
     * 为左侧返回按钮添加自定义点击事件
     * @param listener
     */
    public void setOnclickLeft(OnClickListener listener) {
        top_left.setOnClickListener(listener);
    }

    /**
     * 设置标题的方法
     * @param title
     */
    public void setTitle(String title) {
        top_title.setText(title);
    }

    /**
     * 设置标题的方法
     * @param title
     */
    public void setRightTitle(String title) {
        top_right.setText(title);
    }

}
