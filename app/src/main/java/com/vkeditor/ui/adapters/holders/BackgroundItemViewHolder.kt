package com.vkeditor.ui.adapters.holders

import android.view.View
import android.widget.ImageView
import com.nostra13.universalimageloader.core.ImageLoader
import com.vkeditor.R
import com.vkeditor.entity.Background

class BackgroundItemViewHolder(
    itemView: View,
    private val clickListener: ((Int) -> Unit)?
): BackgroundViewHolder(itemView) {

    fun bind(item: Background, position: Int, isSelected: Boolean) = with(itemView) {
        itemView.findViewById<ImageView>(R.id.ivPreview).apply {
            ImageLoader.getInstance().displayImage(item.uriPreview, this)
            clipToOutline = true
        }

        findViewById<ImageView>(R.id.ivSelected).apply {
            visibility = if (isSelected) View.VISIBLE else View.GONE
        }

        setOnClickListener {
            clickListener?.invoke(position)
        }
    }

}