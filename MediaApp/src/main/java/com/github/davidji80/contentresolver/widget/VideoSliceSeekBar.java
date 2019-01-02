package com.github.davidji80.contentresolver.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.github.davidji80.contentresolver.R;
import com.github.davidji80.contentresolver.utility.DistUtility;

public class VideoSliceSeekBar extends View {
    private String TAG = VideoSliceSeekBar.class.getSimpleName();

    private static final int PADDING_BOTTOM_TOP = 3;
    private static final int PADDING_LEFT_RIGHT = 5;
    private static final int BORDER_SIZE = 10;
    private static final int DRAG_OFFSET = 50;
    private static final int MIX_SELECT_SIZE = 100;
    private static final int TO_EDGE_SIZE = 20;

    enum SelectThumb {
        /*
          选中左边滑块
         */
        SELECT_THUMB_LEFT,
        /*
          选中右边滑块
         */
        SELECT_THUMB_RIGHT,
        NO_SELECT
    }

    //定义一个paint
    private Paint mPaint = new Paint();

    private Bitmap thumbSliceLeft;
    private Bitmap thumbSliceRight;

    private int touchLeft = 0;
    private int touchRight = 0;

    private SelectThumb selectThumb;

    public VideoSliceSeekBar(Context context) {
        super(context);
        init();
    }

    public VideoSliceSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoSliceSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        thumbSliceLeft = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_sweep_left);
        thumbSliceRight = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_sweep_right);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //view
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        //dip2px
        int paddingLeftRight = DistUtility.dip2px(getContext(), PADDING_LEFT_RIGHT);
        int paddingBottomTop = DistUtility.dip2px(getContext(), PADDING_BOTTOM_TOP);
        //cal max right
        int maxDrawRight = viewWidth - thumbSliceRight.getWidth() - paddingLeftRight;
        //To Edge
        if (touchLeft < TO_EDGE_SIZE) touchLeft = 0;
        if (touchRight > (maxDrawRight - TO_EDGE_SIZE)) touchRight = maxDrawRight;
        //draw
        int drawLeft;
        int drawRight;
        int drawTop;
        int drawBottom;
        drawLeft = touchLeft + DistUtility.dip2px(getContext(), PADDING_LEFT_RIGHT);
        if (touchRight == 0) {
            touchRight = maxDrawRight;
        }
        if (touchRight > maxDrawRight) {
            drawRight = maxDrawRight;
        } else {
            drawRight = touchRight;
        }
        drawTop = paddingBottomTop;
        drawBottom = viewHeight - paddingBottomTop * 2;


        mPaint.setColor(getResources().getColor(R.color.holo_red_dark));
        canvas.drawRect(drawLeft, drawTop, drawRight + thumbSliceRight.getWidth(), paddingBottomTop + BORDER_SIZE, mPaint);
        canvas.drawRect(drawLeft, drawBottom, drawRight + thumbSliceRight.getWidth(), drawBottom + BORDER_SIZE, mPaint);
        int drawImgTop = (viewHeight - thumbSliceLeft.getHeight()) / 2;
        canvas.drawRect(drawLeft, drawTop, drawLeft + thumbSliceLeft.getWidth(), drawBottom, mPaint);
        canvas.drawRect(drawRight, drawTop, drawRight + thumbSliceLeft.getWidth(), drawBottom, mPaint);
        canvas.drawBitmap(thumbSliceLeft, drawLeft, drawImgTop, mPaint);
        canvas.drawBitmap(thumbSliceRight, drawRight, drawImgTop, mPaint);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //Log.e(TAG, "dispatchTouchEvent");
        return super.dispatchTouchEvent(event);
        //return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Log.e(TAG, "onTouchEvent");
        float x = event.getX();
        float y = event.getY();
        Log.e("POINT", "X:" + x + ",Y:" + y);
        Log.e("RECT", "LEFT:" + (touchRight - DRAG_OFFSET) + ",Right:" + (touchRight + DRAG_OFFSET));
        boolean result = true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if ((x > (touchLeft - DRAG_OFFSET)) && (x < (touchLeft + DRAG_OFFSET))) {
                    selectThumb = SelectThumb.SELECT_THUMB_LEFT;
                } else if ((x > (touchRight - DRAG_OFFSET)) && (x < (touchRight + DRAG_OFFSET))) {
                    selectThumb = SelectThumb.SELECT_THUMB_RIGHT;
                } else {
                    selectThumb = SelectThumb.NO_SELECT;
                    result = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (selectThumb == SelectThumb.SELECT_THUMB_LEFT) {
                    if (x < (touchRight - MIX_SELECT_SIZE)) {
                        touchLeft = (int) x;
                        invalidate();
                    }
                } else if (selectThumb == SelectThumb.SELECT_THUMB_RIGHT) {
                    if (x > (touchLeft + MIX_SELECT_SIZE)) {
                        touchRight = (int) x;
                        invalidate();
                    }
                } else {
                    result = false;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
        return result;
    }
}
