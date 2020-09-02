package com.vkeditor.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vkeditor.R
import com.vkeditor.entity.Sticker
import com.vkeditor.ui.adapters.holders.StickerItemViewHolder

class StickersListAdapter: RecyclerView.Adapter<StickerItemViewHolder>() {

    var onItemClick: ((Int) -> Unit)? = null

    private var items: List<Sticker> = listOf()

    fun updateList(newItems: List<Sticker>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_sticker, parent, false)
        return StickerItemViewHolder(view, onItemClick)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: StickerItemViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

}