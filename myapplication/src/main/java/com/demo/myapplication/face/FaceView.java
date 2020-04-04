package com.demo.myapplication.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.demo.myapplication.R;

import androidx.annotation.RequiresApi;

import static android.graphics.Bitmap.Config.RGB_565;

/**
 * Created by hc on 2019.3.22.
 */
public class FaceView extends View {

    private Context context;

    int out = 8;
    int startAngel = 0;
    boolean isRunning = false;

    private int mHeight;
    private int mWidth;
    private int mRadius;
    private int circleX;
    private int circleY;

    private int mTop,mLeft,mBottom,mRight;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                startAngel += 1;
                invalidate();
                handler.post(runnable);
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                handler.sendEmptyMessage(1);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        Bitmap b1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.rec_face);
        Bitmap b1 = Bitmap.createBitmap(mWidth,mHeight,RGB_565);
        b1.setPixel(0,0, Color.parseColor("#80a0f0"));
        Drawable drawable = context.getDrawable(R.drawable.rec_face);

        Bitmap b2 = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.RGB_565);

        Shader shader1 = new BitmapShader(b1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Shader shader2 = new BitmapShader(b2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        Shader shader = new ComposeShader(shader1,shader2,PorterDuff.Mode.SRC_OVER);
        circlePaint.setShader(shader);

        canvas.drawRect(0,0,mWidth,mHeight,circlePaint);


        canvas.drawCircle(circleX, circleY, mRadius, circlePaint);

        int left   = circleX-mRadius-out;
        int top    = circleY-mRadius-out;
        int right  = circleX+mRadius+out;
        int bottom = circleY+mRadius+out;

        canvas.drawArc(left, top, right , bottom,startAngel+000, 45, false, circlePaint);
        canvas.drawArc(left, top, right , bottom,startAngel+ 90, 45, false, circlePaint);
        canvas.drawArc(left, top, right , bottom,startAngel+180, 45, false, circlePaint);
        canvas.drawArc(left, top, right , bottom,startAngel+270, 45, false, circlePaint);
    }

    public void start() {
        isRunning = true;
        handler.post(runnable);
    }

    public void pause() {
        isRunning = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                start();
            }
        },2 * 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();

        initSize();
    }

    private void initSize() {
        mRadius = (int) (mWidth*1f/7*5/2);
        circleX = mWidth / 2;
        circleY = (int) (mHeight * 0.1f + mRadius);

        mLeft   = circleX - mRadius;
        mTop    = circleY - mRadius;
        mRight  = circleX + mRadius;
        mBottom = circleY + mRadius;
    }

    Paint circlePaint ;
    private void initSelf(Context context) {
        this.context = context;
        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);

    }

    public int getmTop() {
        return mTop;
    }

    public int getmLeft() {
        return mLeft;
    }

    public int getmBottom() {
        return mBottom;
    }

    public int getmRight() {
        return mRight;
    }

    public FaceView(Context context) { this(context,null); }
    public FaceView(Context context, AttributeSet attrs) { this(context,null,0); }
    public FaceView(Context context, AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); initSelf(context);  }
}
