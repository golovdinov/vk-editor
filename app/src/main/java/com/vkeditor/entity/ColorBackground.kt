package com.vkeditor.entity

class ColorBackground(
    id: String,
    val color: Int,
    val colorPreview: Int
): Background(id, Type.Color)