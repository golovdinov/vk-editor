package com.vkcanvas.entity

abstract class VKCanvasObject(
    val id: String,
    val type: String,
    val subtype: String // нужен, когда мы хотим разделять TYPE_BITMAP
) {
    companion object {
        const val TYPE_COLOR = "color"
        const val TYPE_GRADIENT = "gradient"
        const val TYPE_BITMAP = "bitmap"
        const val TYPE_TEXT = "text"
    }
}