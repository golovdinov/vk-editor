package com.vkeditor.entity

import com.vkcanvas.entity.VKCanvasColorObject

// Просто обертка, чтобы не запутаться в названиях
class ColorBackgroundObject(
    id: String,
    color: Int,
    colorPreview: Int
): VKCanvasColorObject(id, color, colorPreview)