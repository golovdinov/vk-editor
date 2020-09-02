package com.vkeditor.repository

import android.graphics.Color
import android.graphics.Typeface
import android.text.Layout
import androidx.lifecycle.liveData
import com.vkcanvas.entity.TextStyle

class TextStyleRepository {

    fun getStyles() = liveData<List<TextStyle>> {
        val list = mutableListOf<TextStyle>()

        // Black Transparent
        list.add(
            TextStyle(
                24F,
                Color.BLACK,
                null,
                null,
                Layout.Alignment.ALIGN_CENTER,
                Typeface.NORMAL
            )
        )

        // White Transparent With Shadow
        list.add(
            TextStyle(
                24F,
                Color.WHITE,
                null,
                Color.argb(50, 0, 0, 0),
                Layout.Alignment.ALIGN_CENTER,
                Typeface.NORMAL
            )
        )

        // White With Light Bg
        list.add(
            TextStyle(
                24F,
                Color.WHITE,
                Color.argb(90, 255, 255, 255),
                Color.argb(90, 0, 0, 0),
                Layout.Alignment.ALIGN_CENTER,
                Typeface.BOLD
            )
        )

        // Black With Qpaque White Bg
        list.add(
            TextStyle(
                24F,
                Color.BLACK,
                Color.WHITE,
                null,
                Layout.Alignment.ALIGN_CENTER,
                Typeface.BOLD
            )
        )

        emit(list)
    }

}