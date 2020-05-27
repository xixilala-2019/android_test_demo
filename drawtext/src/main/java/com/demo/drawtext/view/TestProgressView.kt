package com.demo.drawtext.view

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import com.demo.drawtext.TLog

class TestProgressView : View {

    private var progress = 0
    private lateinit var progressPaint:Paint
    private lateinit var buffLinePaint:Paint
    private lateinit var backgroundPaint:Paint
    private lateinit var progressTextPaint:TextPaint

    constructor(context: Context?) : super(context) {init()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){init()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init()}


    private fun init() {
        backgroundPaint = Paint()
        backgroundPaint.colorFilter = LightingColorFilter(0xffffff, 0x003000)

        progressPaint = Paint()
        progressPaint.strokeWidth = 30f
//        progressPaint.color = Color.RED
        val linearShader = LinearGradient(100f,100f,500f,500f,Color.parseColor("#E91E63"),
                Color.parseColor("#21ffF3"), Shader.TileMode.CLAMP)
        progressPaint.shader = linearShader
//        val colorFilter = LightingColorFilter(0x00ffff, 0x000000)
//        progressPaint.colorFilter = colorFilter
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeCap = Paint.Cap.ROUND

        progressTextPaint = TextPaint()
        progressTextPaint.textSize = 50f
        progressTextPaint.color = Color.WHITE

        buffLinePaint = Paint()
        buffLinePaint.color = Color.BLUE
        buffLinePaint.strokeWidth = 1f


    }

    private fun setProgress(progress:Int) {
        this.progress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas != null) {


            val rectBackground = Rect(0,0,width,height)
            canvas.drawRect(rectBackground,backgroundPaint)

            val left = (width/4).toFloat()
            val top  = (height/4).toFloat()
            val right  = left*3.toFloat()
            val bottom = top*3.toFloat()
            val rect = RectF(left,top,right,bottom)
            canvas.drawArc(rect, 135f, progress * 2.7f, false, progressPaint)

            TLog.info("$progress = progress")
            val text = "$progress Gg%"
            val textWidth = progressTextPaint.measureText(text)
            val textHeight = progressTextPaint.fontSpacing
            val x = (width-textWidth)/2
            val y = (height-textHeight)/2 + textHeight
            canvas.drawText(text,x,y,progressTextPaint)

            val ascentLineY = (height-textHeight)/2
            val bottomLineY = y
            canvas.drawLine(0f,ascentLineY,2000f, ascentLineY, buffLinePaint)
            canvas.drawLine(0f, bottomLineY, 2000f, bottomLineY, buffLinePaint)


        }
    }

    fun setTProgress(progress:Int) {
        val anim = ObjectAnimator.ofInt(this, "progress", 0, progress)
        anim.duration = 2000
        anim.interpolator = LinearInterpolator()
        anim.start()
    }

    fun startNewProgress(progress:Int) {
        val keyframe1 = Keyframe.ofInt(0f, 0)
        val keyframe2 = Keyframe.ofInt(0.5f, 100)
        val keyframe3 = Keyframe.ofInt(1f, 80)
        keyframe3.interpolator = BounceInterpolator()
        val holder = PropertyValuesHolder.ofKeyframe("progress", keyframe1, keyframe2, keyframe3)
        val animator = ObjectAnimator.ofPropertyValuesHolder(this, holder)
        animator.duration = 2222
        animator.start()
    }
}