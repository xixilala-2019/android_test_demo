package com.demo.drawtext.view

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.demo.drawtext.R
import com.demo.drawtext.TLog


class AnimImageView: View {
    constructor(context: Context?) : super(context) {init()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){init()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init()}

    private lateinit var circlePaint:Paint
    private var color = Color.parseColor("#ffff0000")
    private fun init() {
        circlePaint = Paint()
        circlePaint.color = color
    }

    fun setColor(color:Int) {
        this.color = color

        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        if (canvas != null) {
            TLog.info("", "color1 = $color")
            circlePaint.color = color
            canvas.drawCircle(width.toFloat()/2,height.toFloat()/2,width.toFloat()/2,circlePaint)

            val bm = (resources.getDrawable(R.mipmap.go) as BitmapDrawable).bitmap
//            canvas.drawBitmap(bm, 40f,40f, Paint())
        }
    }

    fun start() {

        TLog.info("", "color2 = $color")
        val animate = animate()
        animate.rotationY(45f)
        animate.x(0f)
        animate.y(0f)
        animate.duration = 2000
        animate.start()
        //并没有模仿成功 1-6
    }

    fun changeColor() {
        val anim = ObjectAnimator.ofInt(this, "color", Color.parseColor("#ffff0000"), Color.parseColor("#00ffff00"))
        anim.duration = 2222
        anim.setEvaluator(ArgbEvaluator())
        anim.start()
    }
}