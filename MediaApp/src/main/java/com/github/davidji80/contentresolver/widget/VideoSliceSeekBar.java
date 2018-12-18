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

public class VideoSliceSeekBar extends View {
    private static final int PADDING_BOTTOM_TOP = 10;
    private static final int PADDING_LEFT_RIGHT = 5;
    private static final int BORDER_SIZE = 10;
    private static final int DRAG_OFFSET = 50;
    private static final int MIX_SELECT_SIZE=100;

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
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        int drawLeft;
        int drawRight;
        int drawTop;
        drawLeft = touchLeft + PADDING_LEFT_RIGHT;
        if (touchRight==0) {
            drawRight = viewWidth - thumbSliceRight.getWidth() - PADDING_LEFT_RIGHT * 2;
            touchRight=drawRight;
        }else if (touchRight>(viewWidth - thumbSliceRight.getWidth() - PADDING_LEFT_RIGHT * 2)){
            drawRight = viewWidth - thumbSliceRight.getWidth() - PADDING_LEFT_RIGHT * 2;
        }else{
            drawRight=touchRight;
        }
        drawTop = PADDING_BOTTOM_TOP + BORDER_SIZE;
        mPaint.setColor(getResources().getColor(R.color.holo_red_dark));
        canvas.drawRect(drawLeft, PADDING_BOTTOM_TOP, drawRight + thumbSliceRight.getWidth(), PADDING_BOTTOM_TOP + BORDER_SIZE, mPaint);
        canvas.drawRect(drawLeft, viewHeight - PADDING_BOTTOM_TOP * 2, drawRight + thumbSliceRight.getWidth(), viewHeight - PADDING_BOTTOM_TOP * 2 + BORDER_SIZE, mPaint);
        int drawImgTop = (viewHeight - thumbSliceLeft.getHeight()) / 2;
        canvas.drawRect(drawLeft, drawTop, drawLeft + thumbSliceLeft.getWidth(), viewHeight - PADDING_LEFT_RIGHT * 2, mPaint);
        canvas.drawRect(drawRight, drawTop, drawRight + thumbSliceLeft.getWidth(), viewHeight - PADDING_LEFT_RIGHT * 2, mPaint);
        canvas.drawBitmap(thumbSliceLeft, drawLeft, drawImgTop, mPaint);
        canvas.drawBitmap(thumbSliceRight, drawRight, drawImgTop, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("POINT", "X:" + x + ",Y:" + y);
                Log.e("RECT", "LEFT:" + (touchLeft - DRAG_OFFSET) + ",Right:" + (touchLeft + DRAG_OFFSET));
                if ((x > (touchLeft - DRAG_OFFSET)) && (x < (touchLeft + DRAG_OFFSET))) {
                    selectThumb = SelectThumb.SELECT_THUMB_LEFT;
                } else if ((x > (touchRight - DRAG_OFFSET)) && (x < (touchRight + DRAG_OFFSET))) {
                    selectThumb = SelectThumb.SELECT_THUMB_RIGHT;
                } else {
                    selectThumb = SelectThumb.NO_SELECT;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(selectThumb==SelectThumb.SELECT_THUMB_LEFT){
                    if (x<(touchRight-MIX_SELECT_SIZE)) {
                        touchLeft = (int) x;
                        invalidate();
                    }
                }else if(selectThumb==SelectThumb.SELECT_THUMB_RIGHT){
                    if (x>(touchLeft+MIX_SELECT_SIZE)) {
                        touchRight = (int) x;
                        invalidate();
                    }
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                break;
        }
        return true;
    }
}
