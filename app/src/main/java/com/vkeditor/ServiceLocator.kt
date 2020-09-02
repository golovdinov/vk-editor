package com.vkeditor

import android.content.Context
import com.vkcanvas.VKCanvasRenderer
import com.vkeditor.repository.*

class ServiceLocator(val context: Context) {

    val stickerRepository: StickerRepository by lazy {
        StickerRepository(context)
    }

    val backgroundRepository: BackgroundRepository by lazy {
        BackgroundRepository(context)
    }

    val textStyleRepository: TextStyleRepository by lazy {
        TextStyleRepository()
    }

    val userBackgroundRepository: UserBackgroundRepository by lazy {
        UserBackgroundRepository(context)
    }

    val canvasRenderer: VKCanvasRenderer by lazy {
        VKCanvasRenderer(context)
    }



}