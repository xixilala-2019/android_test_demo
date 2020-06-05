package com.demo.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by hc on 2019.3.22.
 */
public class FaceView extends FrameLayout {

    Paint circlePaint;

    private void initSelf(Context context) {

        circlePaint = new Paint();
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStrokeWidth(2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int height = getHeight();
        if (height == 0) return;
        int width = getWidth();

        int radius = (int) (width * 1f / 7 * 5 / 2);
        int circleX = width / 2;
        int circleY = (int) (height * 0.3f + radius);

        canvas.drawCircle(circleX, circleY, radius, circlePaint);

        canvas.drawCircle(circleX, circleY, radius + 5, circlePaint);
    }

    public FaceView(Context context) {
        super(context);
        initSelf(context);
    }

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSelf(context);
    }

    public FaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initSelf(context);
    }
}
