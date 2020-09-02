package com.vkcanvas.entity

import android.graphics.Point
import android.util.Size

data class TransformState(
    val xy: Point,
    val size: Size,
    val angle: Float
)