package com.vkeditor.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.vkcanvas.widgets.VKCanvasTrashView
import com.vkcanvas.util.dpToPx
import com.vkeditor.R

class TrashView: LinearLayout, VKCanvasTrashView {

    enum class Style {
        WithBorder, WithShadow
    }

    constructor(context: Context, style: Style) : this(context, style,null)
    constructor(context: Context, style: Style, attrs: AttributeSet?) : this(context, style, attrs, 0)
    constructor(context: Context, style: Style, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.CENTER_HORIZONTAL)

            bottomMargin = dpToPx(context, 16F).toInt()
        }

        val layoutId = when(style) {
            Style.WithBorder -> R.layout.view_trash_bordered
            Style.WithShadow -> R.layout.view_trash
        }

        isHapticFeedbackEnabled = true

        LayoutInflater.from(context).inflate(layoutId, this, true)
    }

    override var isTrashActivated: Boolean = false
        set(flag) {
            findViewById<ImageView>(R.id.ivTrash).visibility = if (flag) View.INVISIBLE else View.VISIBLE
            findViewById<ImageView>(R.id.ivTrashActivated).visibility = if (flag) View.VISIBLE else View.INVISIBLE

            if (field != flag) {
                performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }

            field = flag
        }

}