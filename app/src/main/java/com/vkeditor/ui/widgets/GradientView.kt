package com.vkeditor.ui.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.vkcanvas.entity.VKCanvasGradientObject
import kotlin.math.pow
import kotlin.math.sqrt

class GradientView: View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paintFill = Paint().apply {
        style = Paint.Style.FILL
    }
    private val paintGradient = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius: Float = 0F

    fun setColors(colorStart: Int, colorEnd: Int) {
        paintFill.color = colorEnd

        radius = sqrt(
            90.0.pow(2.0) + 90.0.pow(2.0)
        ).toFloat()

        paintGradient.shader = RadialGradient(
            0F,
            0F,
            radius,
            colorStart,
            colorEnd,
            Shader.TileMode.CLAMP
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPaint(paintFill)
        canvas?.drawCircle(0F, 0F, radius, paintGradient)
        super.onDraw(canvas)
    }

}