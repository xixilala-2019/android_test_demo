package com.demo.myapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import androidx.appcompat.app.AppCompatActivity;
import it.sephiroth.android.library.xtooltip.Tooltip;

import static com.demo.myapplication.PlayProgressView.PAUSE;
import static com.demo.myapplication.PlayProgressView.PLAY;
import static com.nhaarman.supertooltips.ToolTip.AnimationType.FROM_MASTER_VIEW;
import static com.nhaarman.supertooltips.ToolTip.AnimationType.FROM_TOP;
import static com.nhaarman.supertooltips.ToolTip.AnimationType.NONE;

public class ScrollingActivity extends AppCompatActivity implements ToolTipView.OnToolTipViewClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        /*ToolTipRelativeLayout toolTipRelativeLayout = findViewById(R.id.activity_main_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("A beautiful View")
                .withTextColor(Color.WHITE)
                .withColor(Color.BLUE)
                .withShadow()
                .withAnimationType(NONE);
        ToolTipView myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_redtv));
        myToolTipView.setOnToolTipViewClickedListener(this);
        findViewById(R.id.activity_main_redtv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ScrollingActivity.this,"11111111111", Toast.LENGTH_SHORT).show();
            }
        });*/
//        final ImageView iv = findViewById(R.id.image);
//        iv.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                Bitmap bitmap = drawableToBitmap(getResources().getDrawable(R.mipmap.apple ));
//                DetecteSDK sdk = new DetecteSDK();
//                Bitmap detectionBitmap = sdk.DetectionBitmap(bitmap);
//
//                iv.setImageBitmap(detectionBitmap);
//
//            }
//        },1000);


        final PlayProgressView ppv = findViewById(R.id.ppv);
        ppv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ppv.getStatus() == PLAY)
                    ppv.pause();
                else if(ppv.getStatus() == PAUSE)
                    ppv.start();
            }
        });


    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        TextView fab = findViewById(R.id.activity_main_redtv);
//        Tooltip.Builder builder = new Tooltip.Builder(this);
//        Tooltip tooltip = builder.anchor(0, 0).text("哈哈").arrow(true).showDuration(1000).fadeDuration(500).overlay(true).create();
//        tooltip.show(fab, Tooltip.Gravity.TOP, false);
    }

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
        Toast.makeText(this,"21323", Toast.LENGTH_SHORT).show();
    }
}
