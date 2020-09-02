package com.vkeditor.ui.adapters.holders

import android.view.View

class BackgroundAddNewHolder(itemView: View): BackgroundViewHolder(itemView) {

    fun bind(onAddNewClickListener: (() -> Unit)? = null) {
        itemView.setOnClickListener {
            onAddNewClickListener?.invoke()
        }
    }

}