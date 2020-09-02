package com.vkeditor.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vkeditor.R
import com.vkeditor.entity.Background
import com.vkeditor.entity.BitmapBackground
import com.vkeditor.entity.ColorBackground
import com.vkeditor.entity.GradientBackground
import com.vkeditor.ui.adapters.holders.*
import java.lang.IllegalStateException

class BackgroundsListAdapter: RecyclerView.Adapter<BackgroundViewHolder>() {

    companion object {
        const val VIEW_TYPE_COLOR = 1
        const val VIEW_TYPE_GRADIENT = 2
        const val VIEW_TYPE_BITMAP = 3
        const val VIEW_TYPE_BUTTON = 4
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
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position >= items.size -> VIEW_TYPE_BUTTON
            else -> {
                when (items[position].type) {
                    Background.Type.Color -> VIEW_TYPE_COLOR
                    Background.Type.Gradient -> VIEW_TYPE_GRADIENT
                    Background.Type.Bitmap -> VIEW_TYPE_BITMAP
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundViewHolder {
        return when(viewType) {
            VIEW_TYPE_BUTTON -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_background_new, parent, false)
                BackgroundAddNewHolder(view)
            }
            VIEW_TYPE_COLOR -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_background_color, parent, false)
                BackgroundColorViewHolder(view, onItemClickListener)
            }
            VIEW_TYPE_GRADIENT -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_background_gradient, parent, false)
                BackgroundGradientViewHolder(view, onItemClickListener)
            }
            VIEW_TYPE_BITMAP -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_background_bitmap, parent, false)
                BackgroundBitmapViewHolder(view, onItemClickListener)
            }
            else -> {
                throw IllegalStateException("Undefined background view type $viewType")
            }
        }
    }

    override fun getItemCount() = items.size + 1

    override fun onBindViewHolder(holder: BackgroundViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            VIEW_TYPE_BUTTON -> {
                (holder as BackgroundAddNewHolder).bind(onAddNewClickListener)
            }
            VIEW_TYPE_COLOR -> {
                (holder as BackgroundColorViewHolder).bind(
                    items[position] as ColorBackground,
                    position,
                    selectedPosition == position
                )
            }
            VIEW_TYPE_GRADIENT -> {
                (holder as BackgroundGradientViewHolder).bind(
                    items[position] as GradientBackground,
                    position,
                    selectedPosition == position
                )
            }
            VIEW_TYPE_BITMAP -> {
                (holder as BackgroundBitmapViewHolder).bind(
                    items[position] as BitmapBackground,
                    position,
                    selectedPosition == position
                )
            }
            else -> {
                throw IllegalStateException("Undefined background view type $viewType")
            }
        }
    }

}