package com.demo.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * Created by hc on 2019.1.11.
 */
public class LateralSlidingSelectionView extends LinearLayout {

    private Context mContext;
    private OverScroller mOverScroller;
    private LateralSlidingSelectionViewListener mOnListener;
    private float downX;
    private float moveX;
    private Adapter mAdapter;
    private int mIndex;
    private LinearLayout linearLayout;
    private int[] adapterWidth;
    private int mStartX = 0;
    private int linearLayoutWidth;
    private int disX;
    private int mMoveStartX;

    public LateralSlidingSelectionView(Context context) {
        super(context);
        init(context);
    }

    public LateralSlidingSelectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LateralSlidingSelectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public void startCanvase(){
        addView();
    }

    private void addView(){
        if(mAdapter == null)
            return;
        removeAllViews();
        adapterWidth = new int[mAdapter.getCount()];
        linearLayout = new LinearLayout(mContext);
        LayoutParams linearLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(linearLayoutParams);
        linearLayout.setGravity(Gravity.CENTER);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View v = mAdapter.getView(i, null, null);
            final int finalI = i;
            v.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = event.getRawX();
                            disX = 0;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            moveX = event.getRawX();
                            disX = (int)(moveX - downX);
                            smoothScrollTo(-disX-mStartX);
                            mMoveStartX = mStartX + disX;
                            int index = getUpIndex(mMoveStartX);//滑动过程中计算index
                            if(mIndex!=index){
                                mIndex = index;
                                if(mOnListener!=null){
                                    mOnListener.click(index);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            //判断滑动是否超过10
                            if(Math.abs(disX) > 10) {
                                mStartX += disX;
                                setIndex(getUpIndex(mStartX));
                            }else{
                                mIndex = finalI;
                                if(mOnListener!=null){
                                    mOnListener.click(finalI);
                                }
                                setIndex(finalI);
                            }
                            break;
                    }
                    return true;
                }
            });
            adapterWidth[i] = getViewWidth(v);
            linearLayout.addView(v);
        }
        //计算adapter 总宽度
        int adapterWidths = 0;
        for (int i = 0; i < adapterWidth.length; i++) {
            adapterWidths += adapterWidth[i];
        }
        linearLayout.setLayoutParams(new LayoutParams(adapterWidths, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(linearLayout);
        linearLayoutWidth = getViewWidth(linearLayout);

        setIndex(mIndex);
    }

    /**
     * 根据滑动偏移计算index
     * @return
     */
    private int getUpIndex(int movelenght){
        int index = 0;
        int leftWidth = linearLayoutWidth/2-movelenght;
        if(leftWidth>=linearLayoutWidth){//判断是否划过超过右边
            index = adapterWidth.length-1;
        }else{
            int leftValue = 0;
            for (int j = 0; j < adapterWidth.length; j++) {
                leftValue += adapterWidth[j];
                if (leftValue >= leftWidth) {
                    index = j;
                    break;
                }
            }
        }
        return index;
    }
    /**
     * 定位到该位置
     * @param index
     */
    private void setIndex(int index){
        if (index>adapterWidth.length-1)
            index = 0;
        int leftValue = 0;
        for (int i = 0; i < index; i++) {
            leftValue += adapterWidth[i];
        }

        mStartX = linearLayoutWidth/2-leftValue-adapterWidth[index]/2;
        smoothScrollTo(-mStartX);
    }

    /**
     * 更新adapter
     * @param adapter
     */
    public void updataAdapter(Adapter adapter){
        setAdapter(adapter);
        addView();
    }

    /**
     * 初始化
     * @param context
     */
    private void init(Context context) {
        mContext = context;
        mOverScroller = new OverScroller(getContext());
        setOverScrollMode(OVER_SCROLL_ALWAYS);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER);
    }
    public void setMyOnListener(LateralSlidingSelectionViewListener onListener){
        this.mOnListener = onListener;
    }
    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
    }
    public void setmIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx) {
        int dx = fx - mOverScroller.getFinalX();
        smoothScrollBy(dx, mOverScroller.getFinalY());
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mOverScroller.startScroll(mOverScroller.getFinalX(), mOverScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        //先判断mScroller滚动是否完成
        if (mOverScroller.computeScrollOffset()) {
            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }
    /**
     * 获取控件的宽度
     * @param view
     * @return
     */
    public static int getViewWidth(View view) {
        int width = 0;
        width = view.getWidth();
        if (width <= 0) {
            measureView(view);
            width = view.getMeasuredWidth();
        }
        return width;
    }
    /**
     * 测量角度
     * @param v
     */
    public static void measureView(View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
    }

    public interface LateralSlidingSelectionViewListener {
        void click(int index);
    }

}

