package com.demo.drawtext.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import java.lang.StringBuilder
import kotlin.math.abs

/**
 * Created by hc on 2020.5.16.
 */
class MyTextView :AppCompatTextView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val longString = "1234567890Hello你好世界WorldQWERTYUIOPLKJHGFDSAZXCVBNMabcdefghijklmnopqrstuvwxyz"
    private val spiteArray = ArrayList<String>()
    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
        splitString()
        log("ondraw width", width)
        log("字体大小", paint.textSize )
        log("字体颜色", paint.color )
        val fontMetrics = paint.fontMetrics
        val bottom = fontMetrics.bottom

        canvas?.drawText(longString, 100f, 100f ,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        setMeasuredDimension(measureWidth(), measureHeight())
    }



    private fun measureWidth():Int {
        val measureText = paint.measureText(longString)
        log("字符长度", measureText)
        return measureText.toInt()
    }

    private fun splitString() {
        val measureText = paint.measureText(longString)
        var lines = (measureText / measuredWidth).toInt()
        val yu = measureText % width
        if (yu>0) lines++
        val sizeRect = Rect()
        paint.getTextBounds("1",0,1,sizeRect)
        val height = ((abs(sizeRect.bottom-sizeRect.top)) * lines).toInt()
        log("获取高", height, "rect = " , sizeRect ,"lines = " , lines  , "width=" , measuredWidth)

        var arrayIndex = 0
        var start = 0
        var end = 1
        for (i in end until longString.length) {

            val s = longString.substring(start,end)
            if (paint.measureText(s, start,i) >= measuredWidth) {
                spiteArray.add(arrayIndex, s)
                arrayIndex++
                start = i

            } else {
                end++
            }

        }

    }


    private fun log(vararg info:Any) {
        val outStr = StringBuilder()
        for(str in info) {
            outStr.append(str).append(' ')
        }
        Log.e("----------", outStr.toString())
    }
}