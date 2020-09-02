package com.vkcanvas.entity

import android.text.Layout

data class TextStyle(
    val textSizeDp: Float,
    val textColor: Int,
    val backgroundColor: Int? = null,
    val shadowColor: Int? = null,
    val alignment: Layout.Alignment,
    val typeface: Int
)