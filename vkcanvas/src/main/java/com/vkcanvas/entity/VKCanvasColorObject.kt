package com.vkcanvas.entity

open class VKCanvasColorObject(
    id: String,
    val color: Int,
    val colorPreview: Int
): VKCanvasObject(
    id,
    TYPE_COLOR,
    SUBTYPE_COLOR
) {
    companion object {
        const val SUBTYPE_COLOR = "color"
    }
}