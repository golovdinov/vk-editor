package com.vkcanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.text.StaticLayout
import android.text.TextPaint
import com.vkcanvas.entity.VKCanvasBitmapObject
import com.vkcanvas.entity.VKCanvasObject
import com.vkcanvas.entity.VKCanvasTextObject
import com.vkcanvas.util.dpToPx

open class VKCanvasRenderer(val context: Context) {

    open fun drawObject(obj: VKCanvasObject, canvas: Canvas) {
        when (obj.type) {
            VKCanvasObject.TYPE_BITMAP -> drawBitmap(obj as VKCanvasBitmapObject, canvas)
            VKCanvasObject.TYPE_TEXT -> drawText(obj as VKCanvasTextObject, canvas)
            else -> {
                throw IllegalArgumentException("Unknown object type")
            }
        }
    }

    protected fun drawBitmap(bitmapObject: VKCanvasBitmapObject, canvas: Canvas) {
        val matrix = Matrix()

        matrix.postRotate(
            bitmapObject.state.angle,
            bitmapObject.state.size.width.toFloat() / 2,
            bitmapObject.state.size.width.toFloat() / 2
        )

        matrix.postTranslate(
            bitmapObject.state.xy.x.toFloat(),
            bitmapObject.state.xy.y.toFloat()
        )

        val paint = Paint()
        paint.isFilterBitmap = true

        canvas.drawBitmap(bitmapObject.bitmap, matrix, paint)
    }

    protected fun drawText(textObject: VKCanvasTextObject, canvas: Canvas) {
        val textPaint = TextPaint()
        textPaint.textSize =
            dpToPx(context, textObject.style.textSizeDp)
        textPaint.color = textObject.style.textColor
        textPaint.typeface = Typeface.defaultFromStyle(textObject.style.typeface)
        textPaint.isAntiAlias = true

        val staticLayout = StaticLayout(
            textObject.toSpannableString(context),
            textPaint,
            canvas.width,
            textObject.style.alignment,
            1F,
            0F,
            false
        )

        val textWidth = canvas.width
        val textHeight = staticLayout.height

        val toX = canvas.width / 2 - textWidth / 2
        val toY = canvas.height / 2 - textHeight / 2

        canvas.save()
        canvas.translate(toX.toFloat(), toY.toFloat())
        staticLayout.draw(canvas)
        canvas.restore()
    }

}