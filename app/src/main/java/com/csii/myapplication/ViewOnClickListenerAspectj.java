package com.csii.myapplication;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

/**
 * Created by hc on 2018.8.21.
 */
@Aspect
public class ViewOnClickListenerAspectj {
    /**
     * android.view.View.OnClickListener.onClick(android.view.View)
     *
     *@paramjoinPoint JoinPoint
     *@throwsThrowable Exception
     */
    @After("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
    public void onViewClickAOP(final JoinPoint joinPoint)throws Throwable {
        Log.e("------hhhhh "," 在点击事件之后");
    }

    @Before("execution(* android.view.View.OnClickListener.onClick(android.view.View))")
    public void abcd() throws Throwable {
        Log.e("------hhhhh "," 在点击事件之前");
    }

    @Before("execution(* android.view.View.OnLongClickListener.onLongClick(android.view.View))")
    public void viewLongClick() throws Throwable {
        Log.e("------hhhhh "," 在长按事件之前");
    }
}
