package com.vkeditor.entity

import android.graphics.Bitmap
import com.vkcanvas.entity.TransformState
import com.vkcanvas.entity.VKCanvasBitmapObject

class StickerObject(
    id: String,
    bitmap: Bitmap,
    state: TransformState,
    val sticker: Sticker
) : VKCanvasBitmapObject(
    id,
    SUBTYPE_STICKER,
    bitmap,
    state
) {
    companion object {
        const val SUBTYPE_STICKER = "sticker"
    }
}