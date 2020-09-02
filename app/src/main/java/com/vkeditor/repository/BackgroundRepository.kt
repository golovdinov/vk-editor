package com.vkeditor.repository

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.liveData
import com.vkeditor.entity.Background
import com.vkeditor.entity.BitmapBackground
import com.vkeditor.entity.ColorBackground
import com.vkeditor.entity.GradientBackground

class BackgroundRepository(private val context: Context) {

    fun getBackgrounds() = liveData {
        val list = mutableListOf<Background>()

        list.add(
            ColorBackground(
                Background.ID_WHITE,
                Color.WHITE,
                Color.argb(255,235,235,235)
            )
        )
        list.add(
            GradientBackground(
                Background.ID_GRADIENT_BLUE,
                Color.argb(255,48,242,210),
                Color.argb(255,46,122,230)
            )
        )
        list.add(
            GradientBackground(
                Background.ID_GRADIENT_GREEN,
                Color.argb(255,203,230,69),
                Color.argb(255,71,179,71)
            )
        )
        list.add(
            GradientBackground(
                Background.ID_GRADIENT_ORANGE,
                Color.argb(255,255,204,51),
                Color.argb(255,255,119,51)
            )
        )
        list.add(
            GradientBackground(
                Background.ID_GRADIENT_RED,
                Color.argb(255,255,51,85),
                Color.argb(255,153,15,107)
            )
        )
        list.add(
            GradientBackground(
                Background.ID_GRADIENT_VIOLET,
                Color.argb(255,248,166,255),
                Color.argb(255,108,108,217)
            )
        )

        list.add(
            BitmapBackground(
                Background.ID_BEACH,
                "assets://backgrounds/beach.png",
                "assets://backgrounds/beach_preview.png"
            )
        )

        list.add(
            BitmapBackground(
                Background.ID_STARS_SKY,
                "assets://backgrounds/stars.png",
                "assets://backgrounds/stars_preview.png"
            )
        )

        emit(list)
    }

}