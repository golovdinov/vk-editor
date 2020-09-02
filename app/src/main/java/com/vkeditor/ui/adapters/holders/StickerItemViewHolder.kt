package com.vkeditor.ui.adapters.holders

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nostra13.universalimageloader.core.ImageLoader
import com.vkeditor.R
import com.vkeditor.entity.Sticker

class StickerItemViewHolder(
    itemView: View,
    private val onItemClick: ((Int) -> Unit)?
): RecyclerView.ViewHolder(itemView) {

    fun bind(item: Sticker, position: Int) = with(itemView) {
        findViewById<ImageView>(R.id.ivSticker).apply {
            ImageLoader.getInstance().displayImage(
                item.uri,
                this
            )

            setOnClickListener {
                val animTime = context.resources.getInteger(R.integer.short_animation_duration).toLong()
                postDelayed({
                    onItemClick?.invoke(position)
                }, animTime)
            }
        }
    }

}