package com.demo.drawtext.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.BounceInterpolator
import android.view.animation.CycleInterpolator
import androidx.core.view.ViewPropertyAnimatorCompat
import kotlin.math.abs

class MyTextView : View {
    private val TAG = "MyTextView";

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    override fun onDraw(canvas: Canvas?) {
        //super.onDraw(canvas)


        if (canvas != null) {
            val linePaint = Paint()
            linePaint.color = Color.BLACK
            linePaint.strokeWidth = 2f
            linePaint.style = Paint.Style.FILL
            canvas.drawLine(0f, 400f, 2000f, 400f, linePaint)
            canvas.drawLine(400f, 0f, 400f, 2000f, linePaint)

            linePaint.color = Color.RED
            linePaint.textSize = 50f

            val fontMetrics = linePaint.fontMetrics
            val bottom = fontMetrics.bottom
            val ascent = fontMetrics.ascent
            val textHeight = abs(bottom - ascent)


            canvas.drawLine(0f, 400f + bottom, 2000f, 400f + bottom, linePaint)
            canvas.drawLine(0f, 400f + ascent, 2000f, 400f + ascent, linePaint)

            linePaint.color = Color.BLUE
            canvas.drawText("哈哈jYGg``哈哈1234", 400f, 400f, linePaint)

            linePaint.color = Color.parseColor("#098766")
            canvas.drawText("第二行哈哈jYGg``哈哈1234", 400f, 400f + textHeight, linePaint)


            val textPaint = TextPaint()
            textPaint.color = Color.BLACK
            textPaint.textSize = 40f


            val text = "13点44分88888888888888888888888888888888888123456888888882020年5月18日" +
                    "13点44分2020年5月18日13点44分2020年5月18日13点44分2020年5月18日13点44分2020年5月18日13点44分" +
                    "2020年5月18日13点44分2020年5月18日13点44分2020年5月18日13点44分2020年5月18日13点44分" +
                    "2020年5月18日13点44分2020年5月18日13点44分2020年5月18日13点44分2020年5月18日13点44分" +
                    "2020年5月18日13点44分2020年5月18日13点44分2020年5月18日13点44分2020年5月18日" +
                    "请问日托IP立刻就会和公共方法的撒子门口i今年保护一个v成分天然到现在色温2020年5月18日" +
                    "13点44分2020年5月18日13点44分2020年5月18日13点44分2020年5月18日"
            val measureLineWidth = FloatArray(2)
            val lineWordCount = linePaint.breakText(text, true, 1080f, measureLineWidth)
            Log.e(TAG, lineWordCount.toString() + "-" + measureLineWidth[0])
            val staticLayout = StaticLayout.Builder.obtain(text, 0, lineWordCount, textPaint, 1080)
                    .setBreakStrategy(StaticLayout.BREAK_STRATEGY_BALANCED)
                    .build()
            staticLayout.draw(canvas)

            var lines = text.length / lineWordCount
            val lineYu = text.length % lineWordCount
            if (lineYu > 0)
                lines++

            for (i in 0 until lines) {
                val start = i * 35
                var end = (i + 1) * 35
                if (end > text.length) {
                    end = text.length
                }

                val y = textPaint.fontSpacing * i
                val subStr = text.substring(start, end)
//                canvas.drawText(subStr, 000f, 800f + y, textPaint )
            }

            animate().translationX(500f)
                    .translationY(40f)
                    .setDuration(2000)
                    .setInterpolator(CycleInterpolator(1f))


        }


    }
}