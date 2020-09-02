package com.vkeditor.ui.adapters.holders

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.nostra13.universalimageloader.core.ImageLoader
import com.vkeditor.R
import com.vkeditor.entity.Background
import com.vkeditor.entity.ColorBackground
import com.vkeditor.ui.widgets.GradientView

class BackgroundColorViewHolder(
    itemView: View,
    private val clickListener: ((Int) -> Unit)?
): BackgroundViewHolder(itemView) {

    fun bind(item: ColorBackground, position: Int, isSelected: Boolean) = with(itemView) {
        itemView.findViewById<LinearLayout>(R.id.llPreview).apply {
            clipToOutline = true
        }
        itemView.findViewById<ImageView>(R.id.colorView).apply {
            setBackgroundColor(item.colorPreview)
        }

        findViewById<ImageView>(R.id.ivSelected).apply {
            visibility = if (isSelected) View.VISIBLE else View.GONE
        }

        setOnClickListener {
            clickListener?.invoke(position)
        }
    }

}