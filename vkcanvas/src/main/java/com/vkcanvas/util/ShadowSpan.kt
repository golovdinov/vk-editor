package com.vkcanvas.util

import android.text.TextPaint
import android.text.style.CharacterStyle

class ShadowSpan(
    private val dx: Float,
    private val dy: Float,
    private val radius: Float,
    private val color: Int
) : CharacterStyle() {

    override fun updateDrawState(tp: TextPaint?) {
        tp?.setShadowLayer(radius, dx, dy, color)
    }

}