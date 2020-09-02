package com.vkeditor.entity

import com.vkcanvas.entity.TransformState
import com.vkcanvas.entity.VKCanvasGradientObject

// Просто обертка, чтобы не запутаться в названиях
class GradientBackgroundObject(
    id: String,
    state: TransformState,
    colorStart: Int,
    colorEnd: Int
): VKCanvasGradientObject(id, state, colorStart, colorEnd) {
}