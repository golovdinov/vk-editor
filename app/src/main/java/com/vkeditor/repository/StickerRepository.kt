package com.vkeditor.repository

import android.content.Context
import androidx.lifecycle.liveData
import com.vkeditor.entity.Sticker

class StickerRepository(private val context: Context) {

    fun getStickers() = liveData {
        val list = mutableListOf<Sticker>()

        for (i in 1..24) {
            list.add(
                Sticker(
                    "sticker_$i",
                    "assets://stickers/sticker_${i}.png"
                )
            )
        }

        emit(list)
    }

}