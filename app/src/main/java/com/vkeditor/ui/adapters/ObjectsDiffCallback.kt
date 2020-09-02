package com.vkeditor.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vkcanvas.entity.VKCanvasColorObject
import com.vkcanvas.entity.VKCanvasGradientObject
import com.vkcanvas.entity.VKCanvasObject
import com.vkcanvas.entity.VKCanvasTextObject
import com.vkeditor.entity.BitmapBackgroundObject
import com.vkeditor.entity.ColorBackgroundObject
import com.vkeditor.entity.GradientBackgroundObject
import com.vkeditor.entity.StickerObject

class ObjectsDiffCallback(
    private val oldList: List<VKCanvasObject>,
    private val newList: List<VKCanvasObject>
): DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldObject = oldList[oldItemPosition]
        val newObject = newList[newItemPosition]

        // Фон только обновляем (без удаления/добавления)
        if (oldObject.id == newObject.id && newObject.id == "background") {
            return true
        }

        return oldObject.id == newObject.id
                && oldObject.type == newObject.type
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition].type != newList[newItemPosition].type) {
            return false;
        }

        return when (oldList[oldItemPosition].subtype) {
            VKCanvasColorObject.SUBTYPE_COLOR -> {
                val oldObject = oldList[oldItemPosition] as ColorBackgroundObject
                val newObject = newList[newItemPosition] as ColorBackgroundObject

                oldObject.color == newObject.color
            }
            VKCanvasGradientObject.SUBTYPE_GRADIENT -> {
                val oldObject = oldList[oldItemPosition] as GradientBackgroundObject
                val newObject = newList[newItemPosition] as GradientBackgroundObject

                oldObject.colorStart == newObject.colorStart
                        && oldObject.colorEnd == newObject.colorEnd
            }
            BitmapBackgroundObject.SUBTYPE_BACKGROUND -> {
                val oldObject = oldList[oldItemPosition] as BitmapBackgroundObject
                val newObject = newList[newItemPosition] as BitmapBackgroundObject

                oldObject.background == newObject.background
            }
            StickerObject.SUBTYPE_STICKER -> {
                val oldObject = oldList[oldItemPosition] as StickerObject
                val newObject = newList[newItemPosition] as StickerObject

                oldObject.sticker == newObject.sticker
                        && oldObject.state == newObject.state
            }
            VKCanvasTextObject.SUBTYPE_TEXT -> {
                val oldObject = oldList[oldItemPosition] as VKCanvasTextObject
                val newObject = newList[newItemPosition] as VKCanvasTextObject

                // Не сравниваем по тексту, чтобы не пересоздавать EditText
                // oldObject.text == newObject.text
                //        && oldObject.style == newObject.style

                oldObject.style == newObject.style
            }
            else -> {
                throw IllegalArgumentException("Unknown object type")
            }
        }
    }
}