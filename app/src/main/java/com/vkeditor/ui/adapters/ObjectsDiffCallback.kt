package com.vkeditor.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vkcanvas.entity.VKCanvasObject
import com.vkcanvas.entity.VKCanvasTextObject
import com.vkeditor.entity.BackgroundObject
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

        return oldObject.id == newObject.id
                && oldObject.type == newObject.type
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldList[oldItemPosition].type != newList[newItemPosition].type) {
            return false;
        }

        return when (oldList[oldItemPosition].subtype) {
            BackgroundObject.SUBTYPE_BACKGROUND -> {
                val oldObject = oldList[oldItemPosition] as BackgroundObject
                val newObject = newList[newItemPosition] as BackgroundObject

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