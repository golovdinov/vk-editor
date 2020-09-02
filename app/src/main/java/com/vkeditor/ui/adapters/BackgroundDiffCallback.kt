package com.vkeditor.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vkcanvas.entity.VKCanvasObject
import com.vkeditor.entity.Background
import com.vkeditor.entity.BitmapBackground
import com.vkeditor.entity.ColorBackground
import com.vkeditor.entity.GradientBackground

class BackgroundDiffCallback(
    private val oldList: List<Background>,
    private val newList: List<Background>
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
        return when (oldList[oldItemPosition].type) {
            Background.Type.Color -> {
                val oldObject = oldList[oldItemPosition] as ColorBackground
                val newObject = newList[newItemPosition] as ColorBackground

                oldObject.color == newObject.color
            }
            Background.Type.Gradient -> {
                val oldObject = oldList[oldItemPosition] as GradientBackground
                val newObject = newList[newItemPosition] as GradientBackground

                oldObject.colorStart == newObject.colorStart
                        && oldObject.colorEnd == newObject.colorEnd
            }
            Background.Type.Bitmap -> {
                val oldObject = oldList[oldItemPosition] as BitmapBackground
                val newObject = newList[newItemPosition] as BitmapBackground

                oldObject.uri == newObject.uri
            }
        }
    }

}