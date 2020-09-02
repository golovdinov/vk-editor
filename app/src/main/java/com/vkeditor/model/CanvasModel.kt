package com.vkeditor.model

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Size
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.ImageSize
import com.vkcanvas.VKCanvasRenderer
import com.vkcanvas.entity.TextStyle
import com.vkcanvas.entity.TransformState
import com.vkcanvas.entity.VKCanvasObject
import com.vkcanvas.entity.VKCanvasTextObject
import com.vkeditor.App
import com.vkeditor.entity.Background
import com.vkeditor.entity.Sticker
import com.vkeditor.entity.BackgroundObject
import com.vkeditor.entity.StickerObject
import com.vkeditor.utils.scaleCenterCrop
import com.vkeditor.utils.scaleFit

class CanvasModel(
    var canvasSize: Size,
    private val textPlaceHolder: String
) {

    var backgroundObject: BackgroundObject? = null
        private set

    private val _stickerObjects: MutableList<StickerObject> = mutableListOf()
    val stickerObjects: List<StickerObject>
        get() = _stickerObjects

    var text: String = ""
        set(value) {
            field = value

            textStyle?.let {
                textObject = VKCanvasTextObject(
                    "text",
                    textPlaceHolder,
                    text,
                    it
                )
            }
        }

    var textStyle: TextStyle? = null
        set(value) {
            field = value

            value?.let {
                textObject = VKCanvasTextObject(
                    "text",
                    textPlaceHolder,
                    text,
                    it
                )
            }
        }

    var textObject: VKCanvasTextObject? = null
        private set

    fun setBackground(background: Background) {
        val imageSize = ImageSize(canvasSize.width, canvasSize.height)

        var bitmap = ImageLoader.getInstance().loadImageSync(background.uri, imageSize)

        bitmap = bitmap.scaleCenterCrop(canvasSize)

        val state =
            TransformState(Point(0, 0), canvasSize, 0F)

        backgroundObject = BackgroundObject(
            "background", // пока может быть только один фон
            bitmap,
            state,
            background
        )
    }

    fun addSticker(sticker: Sticker) {
        val size = Size(320, 320)
        val imageSize = ImageSize(320, 320)
        val bitmap = ImageLoader.getInstance().loadImageSync(sticker.uri, imageSize)
        val state = TransformState(Point(100, 100), size, 0F)
        val stickerObject = StickerObject(
            System.currentTimeMillis().toString(),
            bitmap,
            state,
            sticker
        )

        _stickerObjects.add(stickerObject)
    }

    fun updateStickerObject(stickerObject: StickerObject, newState: TransformState) {
        val sticker = stickerObject.sticker

        val oldBitmap = stickerObject.bitmap
        var newBitmap = oldBitmap

        if (stickerObject.state.size != newState.size) {
            val imageSize = ImageSize(320, 320)
            newBitmap = ImageLoader.getInstance().loadImageSync(sticker.uri, imageSize)
            newBitmap = newBitmap.scaleFit(newState.size)
        }

        val newStickerObject = StickerObject(
            stickerObject.id,
            newBitmap,
            newState,
            sticker
        )

        val index = stickerObjects.indexOf(stickerObject)

        if (index < 0) {
            return
        }

        _stickerObjects[index] = newStickerObject
    }

    fun removeSticker(stickerObject: StickerObject) {
        _stickerObjects.remove(stickerObject)
    }

    fun renderToBitmap(objects: List<VKCanvasObject>): Bitmap {
        val bitmap = Bitmap.createBitmap(canvasSize.width, canvasSize.height, Bitmap.Config.ARGB_8888)

        val canvas = android.graphics.Canvas(bitmap)

        for (obj in objects) {
            App.serviceLocator!!.canvasRenderer.drawObject(obj, canvas)
        }

        return bitmap
    }

}