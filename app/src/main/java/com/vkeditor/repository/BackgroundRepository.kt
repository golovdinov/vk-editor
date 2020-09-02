package com.vkeditor.repository

import android.content.Context
import androidx.lifecycle.liveData
import com.vkeditor.entity.Background

class BackgroundRepository(private val context: Context) {

    fun getBackgrounds() = liveData {
        val list = mutableListOf<Background>()

        list.add(
            Background(
                Background.ID_WHITE,
                "assets://backgrounds/white.png",
                "assets://backgrounds/gray.png"
            )
        )
        list.add(
            Background(
                Background.ID_GRADIENT_BLUE,
                "assets://backgrounds/gradient_1.png",
                "assets://backgrounds/gradient_1_preview.png"
            )
        )
        list.add(
            Background(
                Background.ID_GRADIENT_GREEN,
                "assets://backgrounds/gradient_2.png",
                "assets://backgrounds/gradient_2_preview.png"
            )
        )
        list.add(
            Background(
                Background.ID_GRADIENT_ORANGE,
                "assets://backgrounds/gradient_3.png",
                "assets://backgrounds/gradient_3_preview.png"
            )
        )
        list.add(
            Background(
                Background.ID_GRADIENT_RED,
                "assets://backgrounds/gradient_4.png",
                "assets://backgrounds/gradient_4_preview.png"
            )
        )
        list.add(
            Background(
                Background.ID_GRADIENT_VIOLET,
                "assets://backgrounds/gradient_5.png",
                "assets://backgrounds/gradient_5_preview.png"
            )
        )

        list.add(Background(
            Background.ID_BEACH,
            "assets://backgrounds/beach.png",
            "assets://backgrounds/beach_preview.png"
        ))

        list.add(
            Background(
                Background.ID_STARS_SKY,
                "assets://backgrounds/stars.png",
                "assets://backgrounds/stars_preview.png"
            )
        )

        emit(list)
    }

}