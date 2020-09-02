package com.vkcanvas.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.vkcanvas.entity.VKCanvasColorObject

class VKCanvasColorObjectView : View, VKCanvasObjectView {

    private val colorObject: VKCanvasColorObject

    override var isTouchedForTransform = false // Не используем в этом классе
    override var touchId = Int.MAX_VALUE // Не используем в этом классе
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    constructor(context: Context, colorObject: VKCanvasColorObject) : this(context, colorObject, null)
    constructor(context: Context, colorObject: VKCanvasColorObject, attrs: AttributeSet?) : this(context, colorObject, attrs, 0)
    constructor(context: Context, colorObject: VKCanvasColorObject, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)  {
        this.colorObject = colorObject
        paint.color = colorObject.color
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPaint(paint)
        super.onDraw(canvas)
    }

}