package com.vkcanvas.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import com.vkcanvas.entity.VKCanvasBitmapObject

class VKCanvasBitmapObjectView: AppCompatImageView,
    VKCanvasObjectView {

    companion object {
        var touchSharedId = 1
    }

    open var isDraggable = false

    private val stickerObject: VKCanvasBitmapObject
    private val canvasSize: Size

    override var isTouchedForTransform = false

    override var touchId = Int.MAX_VALUE

    constructor(
        context: Context,
        stickerObject: VKCanvasBitmapObject,
        canvasSize: Size
    ) : this(
        context,
        stickerObject,
        canvasSize,
        null
    )
    constructor(
        context: Context,
        stickerObject: VKCanvasBitmapObject,
        canvasSize: Size,
        attrs: AttributeSet?
    ) : this(
        context,
        stickerObject,
        canvasSize,
        attrs,
        0
    )
    constructor(
        context: Context,
        stickerObject: VKCanvasBitmapObject,
        canvasSize: Size,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(
        context,
        attrs,
        defStyleAttr
    )  {
        this.stickerObject = stickerObject
        this.canvasSize = canvasSize
        setup()
    }

    private fun setup() {
        setImageBitmap(stickerObject.bitmap)

        rotation = stickerObject.state.angle
        scaleType = ImageView.ScaleType.CENTER_CROP

        val lp = RelativeLayout.LayoutParams(
            stickerObject.state.size.width,
            stickerObject.state.size.height
        ).apply {
            topMargin = stickerObject.state.xy.y
            leftMargin = stickerObject.state.xy.x

            rightMargin = when {
                leftMargin + stickerObject.state.size.width > canvasSize.width -> {
                    canvasSize.width - leftMargin - stickerObject.state.size.width
                }
                else -> 0
            }

            bottomMargin = when {
                topMargin + stickerObject.state.size.height > canvasSize.height -> {
                    canvasSize.height - topMargin - stickerObject.state.size.height
                }
                else -> 0
            }
        }

        layoutParams = lp
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isDraggable || event == null) {
            return super.onTouchEvent(event)
        }

        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                isTouchedForTransform = true
                touchId = touchSharedId++
            }
        }

        return false
    }

}