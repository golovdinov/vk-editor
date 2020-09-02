package com.vkeditor.ui.adapters

import android.graphics.Rect
import android.util.Size
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.vkcanvas.*
import com.vkcanvas.entity.TransformState
import com.vkcanvas.entity.VKCanvasObject
import com.vkcanvas.entity.VKCanvasTextObject
import com.vkcanvas.widgets.VKCanvasBitmapObjectView
import com.vkcanvas.widgets.VKCanvasObjectView
import com.vkcanvas.widgets.VKCanvasTextObjectView
import com.vkeditor.entity.BackgroundObject
import com.vkeditor.entity.StickerObject
import com.vkeditor.ui.widgets.TrashView

open class CanvasAdapter : VKCanvasAdapter() {

    var onTextChanged: ((String) -> Unit)? = null
    var onStickerStateChanged: ((StickerObject, TransformState) -> Unit)? = null
    var onStickerRemoved: ((StickerObject) -> Unit)? = null

    lateinit var canvasSize: Size

    private var objects: List<VKCanvasObject> = listOf()
    private var trashViewBounds: Rect? = null

    private var trashViewStyle: TrashView.Style = TrashView.Style.WithBorder

    private val listUpdateCallback = object : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {
            for (i in 0 until count) {
                notifyObjectChanged(position+i, false)
            }
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyObjectRemoved(fromPosition, false)
            notifyObjectInserted(toPosition, false)
        }

        override fun onInserted(position: Int, count: Int) {
            for (i in 0 until count) {
                notifyObjectInserted(position+i, true)
            }
        }

        override fun onRemoved(position: Int, count: Int) {
            for (i in 0 until count) {
                notifyObjectRemoved(position+i, true)
            }
        }

    }

    override fun getObjectsCount() = objects.size

    override fun getView(parent: ViewGroup, index: Int): VKCanvasObjectView {
        return when (objects[index].subtype) {
            BackgroundObject.SUBTYPE_BACKGROUND -> {
                VKCanvasBitmapObjectView(
                    parent.context,
                    objects[index] as BackgroundObject,
                    canvasSize
                )
            }
            StickerObject.SUBTYPE_STICKER -> {
                VKCanvasBitmapObjectView(
                    parent.context,
                    objects[index] as StickerObject,
                    canvasSize
                ).apply {
                    isDraggable = true
                }
            }
            VKCanvasTextObject.SUBTYPE_TEXT -> {
                val textObjectView = VKCanvasTextObjectView(
                    parent.context,
                    objects[index] as VKCanvasTextObject
                )
                textObjectView.onTextChangedListener = onTextChanged
                textObjectView
            }
            else -> {
                throw IllegalArgumentException("Unknown object type")
            }
        }
    }

    override fun getTrashView(parent: ViewGroup): View? {
        return when {
            trashViewBounds != null -> TrashView(parent.context, trashViewStyle)
            else -> null
        }
    }

    override fun getTrashViewBounds(): Rect? = trashViewBounds

    override fun onObjectStateChanged(index: Int, state: TransformState) {
        (objects[index] as? StickerObject)?.let { stickerObject ->
            onStickerStateChanged?.invoke(stickerObject, state)
        }
    }

    override fun onObjectRemoved(index: Int) {
        (objects[index] as? StickerObject)?.let { stickerObject ->
            onStickerRemoved?.invoke(stickerObject)
        }
    }

    fun updateObjects(newObjects: List<VKCanvasObject>) {
        val diffResult = DiffUtil.calculateDiff(
            ObjectsDiffCallback(
                objects,
                newObjects
            )
        )
        objects = newObjects
        diffResult.dispatchUpdatesTo(listUpdateCallback)
    }

    fun setTrashViewBounds(rect: Rect?) {
        trashViewBounds = rect
        notifyTrashViewUpdated()
    }

    fun setTrashViewStyle(style: TrashView.Style) {
        if (style != trashViewStyle) {
            trashViewStyle = style
            notifyTrashViewUpdated()
        }
    }

}