package com.vkeditor.entity

data class Background(
    val id: String,
    val uri: String,
    val uriPreview: String
) {

    companion object {
        const val ID_WHITE = "white"
        const val ID_GRADIENT_BLUE = "gradient-blue"
        const val ID_GRADIENT_GREEN = "gradient-green"
        const val ID_GRADIENT_ORANGE = "gradient-orange"
        const val ID_GRADIENT_RED = "gradient-red"
        const val ID_GRADIENT_VIOLET = "gradient-violet"
        const val ID_BEACH = "beach"
        const val ID_STARS_SKY = "stars-sky"
    }

}