package com.vkeditor.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vkeditor.R
import com.vkeditor.entity.Background
import com.vkeditor.ui.adapters.holders.BackgroundAddNewHolder
import com.vkeditor.ui.adapters.holders.BackgroundItemViewHolder
import com.vkeditor.ui.adapters.holders.BackgroundViewHolder

class BackgroundsListAdapter: RecyclerView.Adapter<BackgroundViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 1
        const val VIEW_TYPE_BUTTON = 2
    }

    var onItemClickListener: ((Int) -> Unit)? = null
    var onAddNewClickListener: (() -> Unit)? = null
    var selectedPosition = -1
        set(new) {
            val old = field
            field = new
            if (old != new) {
                notifyItemChanged(old)
                notifyItemChanged(new)
            }
        }

    private var items: List<Background> = listOf()

    fun updateList(newItems: List<Background>) {
        val diffResult = DiffUtil.calculateDiff(
            BackgroundDiffCallback(
                items,
                newItems
            )
        )
        items = newItems
        diffResult.dispatchUpdatesTo(this)
        //notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position >= items.size -> VIEW_TYPE_BUTTON
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundViewHolder {
        return when(viewType) {
            VIEW_TYPE_BUTTON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_background_new, parent, false)
                BackgroundAddNewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_background, parent, false)
                BackgroundItemViewHolder(view, onItemClickListener)
            }
        }
    }

    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(holder: BackgroundViewHolder, position: Int) {
        if (position >= items.size) {
            (holder as BackgroundAddNewHolder).let { holder ->
                holder.bind(onAddNewClickListener)
            }
        } else {
            (holder as BackgroundItemViewHolder).let { holder ->
                holder.bind(items[position], position, selectedPosition == position)
            }
        }
    }

}