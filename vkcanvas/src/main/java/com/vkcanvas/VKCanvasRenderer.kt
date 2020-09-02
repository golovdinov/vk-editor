package com.vkcanvas

import android.content.Context
import android.graphics.*
import android.text.StaticLayout
import android.text.TextPaint
import com.vkcanvas.entity.*
import com.vkcanvas.util.dpToPx
import kotlin.math.pow
import kotlin.math.sqrt

open class VKCanvasRenderer(val context: Context) {

    open fun drawObject(obj: VKCanvasObject, canvas: Canvas) {
        when (obj.type) {
            VKCanvasObject.TYPE_COLOR -> drawColor(obj as VKCanvasColorObject, canvas)
            VKCanvasObject.TYPE_GRADIENT -> drawGradient(obj as VKCanvasGradientObject, canvas)
            VKCanvasObject.TYPE_BITMAP -> drawBitmap(obj as VKCanvasBitmapObject, canvas)
            VKCanvasObject.TYPE_TEXT -> drawText(obj as VKCanvasTextObject, canvas)
            else -> {
                throw IllegalArgumentException("Unknown object type")
            }
        }
    }

    protected fun drawColor(colorObject: VKCanvasColorObject, canvas: Canvas) {
        Paint().apply {
            style = Paint.Style.FILL
            color = colorObject.color
            canvas.drawPaint(this)
        }
    }

    protected fun drawGradient(gradientObject: VKCanvasGradientObject, canvas: Canvas) {
        val radius = sqrt(
            gradientObject.state.size.width.toDouble().pow(2.0)
                    + gradientObject.state.size.height.toDouble().pow(2.0)
        )

        Paint().apply {
            color = gradientObject.colorEnd
            style = Paint.Style.FILL
            canvas.drawPaint(this)
        }

        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                0F,
                0F,
                radius.toFloat(),
                gradientObject.colorStart,
                gradientObject.colorEnd,
                Shader.TileMode.CLAMP
            )

            canvas.drawCircle(0F, 0F, radius.toFloat(), this)
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