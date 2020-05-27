package com.demo.drawtext.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import com.demo.drawtext.R
import com.demo.drawtext.TLog

class CameraUseView : View {

    constructor(context: Context?) : super(context) {init()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){init()}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){init()}

    private lateinit var bm:Bitmap
    private lateinit var camera:Camera
    private lateinit var mMatrix: Matrix
    private lateinit var topBlockRect: Rect
    private lateinit var bottomBlockRect: Rect

    private var rotateX = 0f
    private val centerX = 0f
    private val centerY = 143f

    private fun init() {
        bm = BitmapFactory.decodeResource(resources, R.mipmap.go)
        TLog.info("bm width = ${bm.width}")
        camera = Camera()
        mMatrix = Matrix()
        topBlockRect = Rect(0, 0,286,143)
        bottomBlockRect = Rect(0, 143,286,286)
    }

    fun setRotateX(rotateX:Float) {
        this.rotateX = rotateX
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {
//            val centerX = width.toFloat()/2
//            val centerY = height.toFloat()/2

            /*canvas.drawColor(Color.YELLOW)
            canvas.save()

            camera.save()
//            camera.setLocation(0f,0f,1898f)
            camera.rotateX(80f)
            canvas.translate(centerX,centerY)
            camera.applyToCanvas(canvas)
            canvas.translate(-centerX,-centerY)
            camera.restore()

            canvas.drawBitmap(bm,0f,0f,null)

            canvas.restore()*/

            // 模仿图标下半截翻页

            canvas.save()
            canvas.clipRect(topBlockRect)
            canvas.drawBitmap(bm,0f,0f,null)
            canvas.restore()



            canvas.save()

            canvas.clipRect(bottomBlockRect)


            camera.save()
            camera.rotateX(rotateX)
            canvas.translate(centerX,centerY)
            camera.applyToCanvas(canvas)
            canvas.translate(-centerX,-centerY)
            camera.restore()

            canvas.drawBitmap(bm,0f, 0f,null)


            canvas.restore() 
        }
    }

    fun start() {
        val anim = ObjectAnimator.ofFloat(this, "rotateX", 0f, 45f)
        anim.duration = 222
        anim.repeatMode = REVERSE
        anim.repeatCount = ValueAnimator.INFINITE
        anim.start()
    }
}