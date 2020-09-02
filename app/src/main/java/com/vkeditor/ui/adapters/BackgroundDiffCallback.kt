package com.vkeditor.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import com.vkcanvas.entity.VKCanvasObject
import com.vkeditor.entity.Background

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
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldObject = oldList[oldItemPosition]
        val newObject = newList[newItemPosition]

        return oldObject.uri == newObject.uri
    }

}