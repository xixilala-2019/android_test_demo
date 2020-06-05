package com.demo.drawtext.aop

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature

@Aspect//标识切面
class CheckLoginAspect {

    private var isLogin = false

    //切入点
    @Pointcut("execution(@com.demo.drawtext.aop.CheckLogin * *(..))")
    fun checkLogin(){
    }

    @Around("checkLogin()")//环绕通知，先执行通知
    @Throws(Throwable::class)//可能抛出的异常
    fun aroundJoinPoint(joinPoint: ProceedingJoinPoint){
        val methodSignature = joinPoint.signature as MethodSignature
        val checkLogin : CheckLogin? = methodSignature.method.getAnnotation(CheckLogin::class.java)
        if(checkLogin != null){
            val that = joinPoint.`this`
            val context:Context
            context = if (that is Fragment) {
                that.activity as Context
            } else {
                that as Context
            }

            Log.e("","注解执行了")
            if(isLogin){//如果已经登入再去执行对应的内容
                joinPoint.proceed()//执行标注的方法中的内容
            }else{
                Toast.makeText(context,"请先登入", Toast.LENGTH_SHORT).show()
            }
        }
    }
}