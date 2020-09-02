package com.vkcanvas.entity

open class VKCanvasGradientObject(
    id: String,
    val state: TransformState,
    val colorStart: Int,
    val colorEnd: Int
): VKCanvasObject(
    id,
    TYPE_GRADIENT,
    SUBTYPE_GRADIENT
) {
    companion object {
        const val SUBTYPE_GRADIENT = "gradient"
    }
}