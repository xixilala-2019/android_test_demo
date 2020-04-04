package com.demo.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.IntDef;

/**
 * Created by hc on 2019.4.11.
 */
public class PlayProgressView extends View   {

    public static final int LOCK = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCK,PLAY,PAUSE})
    public @interface STATUS {}

    private Paint paint;
    private int width;
    private int height;
    private Bitmap initICON;

    boolean isRunning = false;
    private int swapAngle = 0;
    public static final int START_ANGLE = -90;
    public static final int OUT_STROKE_WIDTH = 10;
    private GestureDetector simpleOnGestureListener;

    private int mInitColor ;
    private int delayTime = 300;
    private @STATUS int status = PAUSE;
    private OnClickListener mClickListener;
    private Bitmap header ;

    private void init(Context context) {

        try {
            InputStream open = context.getAssets().open("home.png");
            Bitmap bitmap = BitmapFactory.decodeStream(open);
            header = makeRoundCorner(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        paint = new Paint();
        paint.setColor(mInitColor);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(OUT_STROKE_WIDTH);

        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_play_arrow_black_24dp);
        initICON = drawableToBitmap(drawable);

        simpleOnGestureListener = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                onClick(PlayProgressView.this);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });
    }

    public void onClick(View v) {
        Toast.makeText(getContext().getApplicationContext(), " status " + status , Toast.LENGTH_SHORT).show();
        switch (status) {
            case PLAY:
                pause();
                break;
            case PAUSE:
                start();
                break;
            case LOCK:
                if (mClickListener != null) {
                    mClickListener.onClick(this);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "have no onclick Listener " , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    Timer timer ;
    private Handler handler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

                if (swapAngle == 360) {
                    pause();
                    invalidate();
                    return;
                }
                swapAngle += 1;
                Log.e("time ", swapAngle+"");
                invalidate();
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                handler.sendEmptyMessageDelayed(1, delayTime);
            }
        }
    };

    public void setTotalDuring  (int during) {
        delayTime = during * 1000 / 360;
        Log.e( "time  ", " time   " + delayTime );
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = width / 2;
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setFilterBitmap(true);
        canvas.drawCircle(size,size,size, paint);

        if (header != null) {
            int width = header.getWidth();
            int height = header.getHeight();
            canvas.rotate(swapAngle,width/2, height/2);
            canvas.drawBitmap(header, 0,0, paint);
            canvas.rotate(-swapAngle,width/2, height/2);
        }

        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(OUT_STROKE_WIDTH);
        canvas.drawCircle(width/2,height/2, width/2-10, paint);

        paint.setStrokeWidth(OUT_STROKE_WIDTH*3);
        int innerWidth = OUT_STROKE_WIDTH*3;
        paint.setColor(Color.BLACK);
        RectF rectF = new RectF(innerWidth ,innerWidth, width - innerWidth,height - innerWidth);
        canvas.drawArc(rectF, START_ANGLE,swapAngle, false, paint);

        initICON = getICON(status);

        int iconWidth = initICON.getWidth();
        int iconHeight = initICON.getHeight();
        canvas.drawBitmap(initICON, (width - iconWidth) >> 1,  (height-iconHeight) >> 1 ,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        simpleOnGestureListener.onTouchEvent(event);
        return true;
    }

    private Bitmap getICON(int status) {
        int id = 0;
        switch (status) {
            case PLAY:
                id = R.drawable.ic_pause_black_24dp;
                break;
            case PAUSE:
                id = R.drawable.ic_play_arrow_black_24dp;
                break;
            case LOCK:
                id = R.drawable.ic_lock_black_24dp;
                if (mClickListener != null) {
                    mClickListener.onClick(this);
                } else {
                    Toast.makeText(getContext().getApplicationContext(), "have no onclick Listener " , Toast.LENGTH_SHORT).show();
                }
                break;
        }
        if (id > 0) {
            Drawable drawable = getResources().getDrawable(id);
            return drawableToBitmap(drawable);
        } else {
            return null;
        }
    }

    public static Bitmap makeRoundCorner(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height/2;
        if (width > height) {
            left = (width - height)/2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width)/2;
            right = width;
            bottom = top + width;
            roundPx = width/2;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public void start() {
        if (isRunning) return;

        isRunning = true;
        status = PLAY;

        if (timer == null)
            timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                isRunning = true;
                handler.sendEmptyMessage(1);
            }
        };
        timer.schedule(task, 0, delayTime);
    }

    public void pause() {
        isRunning = false;
        status = PAUSE;
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }


    public PlayProgressView(Context context) {
        this(context,null);
    }

    public PlayProgressView(Context context,  AttributeSet attrs) {
        super(context,attrs);

        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.PlayProgressView);
        mInitColor = t.getColor(R.styleable.PlayProgressView_color, Color.BLUE);

        init(context);
    }
}
