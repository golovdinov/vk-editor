package com.vkeditor.utils

import android.graphics.Bitmap
import android.util.Size

fun Bitmap.scaleFit(size: Size): Bitmap {
    val scaledWidth: Int
    val scaledHeight: Int

    if (this.width >= this.height) {
        scaledWidth = size.width
        scaledHeight = (size.height * (this.height.toFloat() / this.width.toFloat())).toInt()

    } else {
        scaledHeight = size.height
        scaledWidth = (size.width * (this.width.toFloat() / this.height.toFloat())).toInt()
    }

    return Bitmap.createScaledBitmap(
        this,
        scaledWidth,
        scaledHeight,
        true
    )
}

fun Bitmap.scaleCenterCrop(size: Size): Bitmap {
    val scaledWidth: Int
    val scaledHeight: Int

    if (this.width >= this.height) {
        scaledHeight = size.height
        scaledWidth = (size.width * (this.width.toFloat() / this.height.toFloat())).toInt()
    } else {
        scaledWidth = size.width
        scaledHeight = (size.height * (this.height.toFloat() / this.width.toFloat())).toInt()
    }

    val bitmap = Bitmap.createScaledBitmap(
        this,
        scaledWidth,
        scaledHeight,
        true
    )

    return when {
        (scaledWidth > scaledHeight) -> {
            Bitmap.createBitmap(
                bitmap,
                (scaledWidth - scaledHeight)/2,
                0,
                size.width,
                size.height
            )
        }
        else -> {
            Bitmap.createBitmap(
                bitmap,
                0,
                (scaledHeight - scaledWidth)/2,
                size.width,
                size.height
            )
        }
    }
}