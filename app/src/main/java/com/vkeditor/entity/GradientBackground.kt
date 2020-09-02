package com.vkeditor.entity

class GradientBackground(
    id: String,
    val colorStart: Int,
    val colorEnd: Int
): Background(id, Type.Gradient)