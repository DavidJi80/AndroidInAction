package com.github.davidji80.guilib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 自定义控件
 */
public class CustomView extends View implements View.OnClickListener {
    private final static String TAG = CustomView.class.getSimpleName();

    // 计数值，每点击一次本控件，其值增加1
    private int mCount = 0;

    /**
     * 初始化
     */
    private void init() {
        // 本控件的点击事件
        setOnClickListener(this);
    }

    /**
     * 构造函数
     * 只能在代码中动态的添加
     *
     * @param context
     */
    public CustomView(Context context) {
        super(context);
        init();

    }

    /**
     * 构造函数
     * 可以在代码和xml中都可以用
     *
     * @param context
     * @param attrs
     */
    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 重构onMeasure
     * 打印MeasureSpec信息
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.e(TAG, "onMeasure--widthMode-->" + widthMode);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
                break;
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        Log.e(TAG, "onMeasure--widthSize-->" + widthSize);
        Log.e(TAG, "onMeasure--heightMode-->" + heightMode);
        Log.e(TAG, "onMeasure--heightSize-->" + heightSize);
    }

    /**
     * 重构onLayout
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.e(TAG, "onLayout");
    }

    /**
     * 重构onDraw
     * 在View类中onDraw函数是个空函数，最终的绘制需求需要在自定义的onDraw函数中进行实现
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 定义画笔
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // FILL填充, STROKE描边,FILL_AND_STROKE填充和描边
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        // 获取控件大小
        int with = getWidth();
        int height = getHeight();
        float radius = with / 2;
        // 绘制一个填充色为灰色的矩形
        mPaint.setColor(Color.GRAY);
        canvas.drawRect(0, 0, with, height, mPaint);
        // 绘制一个填充色为蓝色的内切圆
        mPaint.setColor(Color.BLUE);
        canvas.drawCircle(with / 2, with / 2, radius, mPaint);
        // 绘制一个填充色为红色的扇形
        mPaint.setColor(Color.RED);
        // 用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF();
        oval.set(with / 2 - radius, with / 2 - radius, with / 2
                + radius, with / 2 + radius);
        // 根据mCount画圆弧
        canvas.drawArc(oval, 270, mCount, true, mPaint);
        // 获取文字的宽和高
        String text = String.valueOf(mCount);
        Rect mRect=new Rect();
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        float textWidth = mRect.width();
        float textHeight = mRect.height();
        // 在圆心处绘制白色字符串
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(50);
        canvas.drawText(text, with / 2 - textWidth / 2, with / 2
                + textHeight / 2, mPaint);
    }

    /**
     * 重构点击事件
     * @param view
     */
    @Override
    public void onClick(View view) {
        mCount++;
        invalidate();
    }
}