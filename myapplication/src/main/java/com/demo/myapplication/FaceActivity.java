package com.demo.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.cameraview.CameraView;

import java.util.List;

public class FaceActivity extends AppCompatActivity {

    static{
        System.loadLibrary("mp3");
    }

    private CameraView cameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



















        final View view = LayoutInflater.from(this).inflate(R.layout.activity_face,null);
        setContentView(view);

        view.findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), " "  + abc(), Toast.LENGTH_SHORT).show();
            }
        });


        if ( true) //
            return;


        // 刘海适配
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //开局就一张背景图
//        final View view = LayoutInflater.from(this).inflate(R.layout.activity_face,null);
//        setContentView(view);

        //全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        WindowManager.LayoutParams lp = getWindow().getAttributes();


//下面图1
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
        //下面图2
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        //下面图3
//        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT;
        getWindow().setAttributes(lp);


        cameraView = findViewById(R.id.camera);

        getNotchParams();
    }

    public native String abc();

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraView != null)
        cameraView.start();
    }

    @Override
    protected void onPause() {
        if (cameraView != null)
        cameraView.stop();
        super.onPause();
    }

    @TargetApi(28)
    public void getNotchParams () {
        final View decorView = getWindow().getDecorView();

        decorView.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getApplicationContext(), "" + getNotchHeight(getApplicationContext()), Toast.LENGTH_SHORT).show();
                Log.e("TAG", " status height1 " + getNotchHeight(getApplicationContext()));
                Log.e("TAG", " status height2 " + getStatusHeight(getApplicationContext()));

                DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();

                if (displayCutout != null) {

                    Log.e("TAG", "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                    Log.e("TAG", "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
                    Log.e("TAG", "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
                    Log.e("TAG", "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());

                    List<Rect> rects = displayCutout.getBoundingRects();
                    if (rects == null || rects.size() == 0) {
                        Log.e("TAG", "不是刘海屏");
                    } else {
                        Log.e("TAG", "刘海屏数量:" + rects.size());
                        for (Rect rect : rects) {
                            Log.e("TAG", "刘海屏区域：" + rect);
                        }
                    }
                }
            }
        });
    }

    public static float getNotchHeight (Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            int result = 0;
            int resourceId = context.getResources().getIdentifier("status_bar_height","dimen", "android");
            if (resourceId > 0) {
                result = context.getResources().getDimensionPixelSize(resourceId);
            }
            return result;
        }
        return 0;
    }

    public static int getStatusHeight(Context context)
    {

        int statusHeight = -1;
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
