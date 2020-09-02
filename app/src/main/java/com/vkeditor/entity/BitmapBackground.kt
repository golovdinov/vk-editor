package com.vkeditor.entity

class BitmapBackground(
    id: String,
    val uri: String,
    val uriPreview: String
): Background(id, Type.Bitmap)