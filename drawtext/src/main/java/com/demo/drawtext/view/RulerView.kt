package com.demo.drawtext.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

class RulerView : View {
    constructor(context: Context?) : super(context) {init()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){init()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init()}

    private lateinit var textWeightPaint:TextPaint
    private lateinit var greenLinePaint :Paint
    private lateinit var grayLinePaint  :Paint
    private lateinit var textScalePaint :TextPaint
    private val scaleWidth = 10f // 一小格宽度
    private val minScale = 5    //   5公斤
    private val maxScale = 100  // 100公斤
    private val scaleStrokeWidth  =  2f
    private val scaleStrokeHeight = 10f
    private val scale1Width = scaleWidth + scaleStrokeWidth // 一大格 ， 10个小格宽度+10个线宽度
    private val scale10Width = scale1Width * 10 // 一大格 ， 10个小格宽度+10个线宽度

    private var textWeight = "- 公斤"

    private fun init() {
        textWeightPaint = TextPaint()
        greenLinePaint  = Paint()
        grayLinePaint   = Paint()
        textScalePaint  = TextPaint()

        textWeightPaint.color = Color.GREEN
        textScalePaint.color = Color.DKGRAY
        textWeightPaint.textSize = 50f
        textScalePaint.textSize = 30f
        textWeightPaint.textAlign = Paint.Align.CENTER

        greenLinePaint.color = Color.GREEN
        grayLinePaint.color = Color.GRAY
        grayLinePaint.strokeWidth = scaleStrokeWidth

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 5-100   19个大个  190个小个
        val width = ((maxScale-minScale)/5 * 10 * scaleWidth             // 所有格子宽度
                            + (maxScale-minScale)/5 * scaleStrokeWidth * 10    // 所有格子线的宽度
                            + scaleStrokeWidth)                                // 最后一条的宽度

                        
        setMeasuredDimension(width.toInt(),measuredHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {

            val weightWidth = textWeightPaint.measureText(textWeight)
            val fontMetrics = textWeightPaint.fontMetrics
            val centerToBaseLine = (fontMetrics.bottom-fontMetrics.top)/2 - fontMetrics.bottom
            val x = (width-weightWidth)/2
            val y = height/4+centerToBaseLine
            canvas.drawText(textWeight, x, y, textWeightPaint)


            // 横向
            val lineY = height.toFloat()/2
            canvas.drawLine(0f, lineY, width.toFloat(), lineY, grayLinePaint)




            for (i in 0 until 10) {
                // 竖向
                val lineVY1 = lineY
                val lineVY2 = lineVY1 + scaleStrokeHeight
                canvas.drawLine(i * (scale1Width), lineVY1, i * (scale1Width), lineVY2, grayLinePaint)
            }




        }
    }


}