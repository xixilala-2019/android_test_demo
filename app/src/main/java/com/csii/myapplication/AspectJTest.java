package com.csii.myapplication;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Created by hc on 2018.8.20.
 */
@Aspect
public class AspectJTest {
    private static final String TAG = "tag00";

    @Pointcut("execution(@com.csii.myapplication.AspectJAnnotation  * *(..))")
    public void executionAspectJ() {

    }

    @Around("executionAspectJ()")
    public Object aroundAspectJ(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        // 调用之前
        Log.i(TAG, "aroundAspectJ(ProceedingJoinPoint joinPoint)");
        AspectJAnnotation aspectJAnnotation = methodSignature.getMethod().getAnnotation(AspectJAnnotation.class);
        String permission = aspectJAnnotation.value();
        if(permission.equals("权限A")) {
            // 执行原代码
            Object result=joinPoint.proceed();
            Log.i(TAG, "有权限："+permission);
            return result;
        }
        return "";
    }

}