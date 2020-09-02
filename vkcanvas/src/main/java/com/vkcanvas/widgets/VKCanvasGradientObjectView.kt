package com.vkcanvas.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.vkcanvas.entity.VKCanvasGradientObject
import kotlin.math.pow
import kotlin.math.sqrt

class VKCanvasGradientObjectView : View, VKCanvasObjectView {

    private val gradientObject: VKCanvasGradientObject

    override var isTouchedForTransform = false // Не используем в этом классе
    override var touchId = Int.MAX_VALUE // Не используем в этом классе

    private val paintFill = Paint().apply {
        style = Paint.Style.FILL
    }
    private val paintGradient = Paint(Paint.ANTI_ALIAS_FLAG)
    private var radius: Double = 0.0

    constructor(context: Context, gradientObject: VKCanvasGradientObject) : this(context, gradientObject, null)
    constructor(context: Context, gradientObject: VKCanvasGradientObject, attrs: AttributeSet?) : this(context, gradientObject, attrs, 0)
    constructor(context: Context, gradientObject: VKCanvasGradientObject, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)  {
        this.gradientObject = gradientObject

        paintFill.color = gradientObject.colorEnd

        radius = sqrt(
            gradientObject.state.size.width.toDouble().pow(2.0)
                    + gradientObject.state.size.height.toDouble().pow(2.0)
        )

        paintGradient.shader = RadialGradient(
            0F,
            0F,
            radius.toFloat(),
            gradientObject.colorStart,
            gradientObject.colorEnd,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPaint(paintFill)
        canvas?.drawCircle(0F, 0F, radius.toFloat(), paintGradient)
        super.onDraw(canvas)
    }

}