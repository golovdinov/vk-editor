package com.vkeditor.entity

import android.graphics.Bitmap
import com.vkcanvas.entity.TransformState
import com.vkcanvas.entity.VKCanvasBitmapObject

class BackgroundObject(
    id: String,
    bitmap: Bitmap,
    state: TransformState,
    val background: Background
): VKCanvasBitmapObject(
    id,
    SUBTYPE_BACKGROUND,
    bitmap,
    state
) {
    companion object {
        const val SUBTYPE_BACKGROUND = "background"
    }
}
