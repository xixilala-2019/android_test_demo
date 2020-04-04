package com.demo.mab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hc on 2019.5.18.
 */
public class VoiceFlashView extends ImageView implements FlashStateImp {
    public VoiceFlashView(Context context) {
        super(context);
        init();
    }

    public VoiceFlashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoiceFlashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Paint paint;
    List<Integer> mlist;
    int mItemHalfWidth;
    private void init() {

        paint = new Paint();
        paint.setStrokeWidth(2);

        mlist = new ArrayList<>();


        int mViewWidth = ScreenUtils.getScreenWidth(App.getInstance()) - DpPxUtils.dip2px(App.getInstance(),32);

         mItemHalfWidth = mViewWidth/341;
         if (mItemHalfWidth < 1)
             mItemHalfWidth = 1;

    }

    public void setDataList(List<Integer> mlist) {
        this.mlist.clear();
        this.mlist.addAll(mlist);
        invalidate();
    }


    public void reset() {
        mlist.clear();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        int centerVertical = height/2;

        paint.setColor(Color.BLACK);
        canvas.drawLine(0,centerVertical, width,centerVertical, paint);

        paint.setColor(Color.BLUE);
        Collections.reverse(mlist);
        for (int i = 0; i < mlist.size(); i++) {
            Integer mItemHeight = mlist.get(i)/2;
            if (mItemHeight < 4) mItemHeight = 4;
            if (mItemHeight > 46) mItemHeight = centerVertical;

            int left = i * (mItemHalfWidth+mItemHalfWidth*2);
            int top = Math.abs(centerVertical - mItemHeight);

            int right = (i+1) * mItemHalfWidth*2 + i*mItemHalfWidth;
            int bottom = centerVertical + mItemHeight;

            RectF rectF = new RectF(left,top,right,bottom);
            canvas.drawRect(rectF,paint);
        }
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }
}
