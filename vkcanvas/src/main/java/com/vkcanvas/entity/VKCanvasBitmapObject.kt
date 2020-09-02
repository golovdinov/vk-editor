package com.vkcanvas.entity

import android.graphics.Bitmap
import com.vkcanvas.entity.TransformState
import com.vkcanvas.entity.VKCanvasObject

open class VKCanvasBitmapObject(
    id: String,
    subtype: String,
    val bitmap: Bitmap,
    val state: TransformState
) : VKCanvasObject(id, TYPE_BITMAP, subtype)