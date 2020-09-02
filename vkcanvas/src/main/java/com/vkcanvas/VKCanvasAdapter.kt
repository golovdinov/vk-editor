package com.vkcanvas

import android.database.Observable
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import com.vkcanvas.entity.TransformState
import com.vkcanvas.widgets.VKCanvasObjectView

abstract class VKCanvasAdapter {

    interface DataObserver {
        fun onChange(withAnimation: Boolean)
        fun onObjectChanged(position: Int, withAnimation: Boolean)
        fun onObjectInserted(position: Int, withAnimation: Boolean)
        fun onObjectRemoved(position: Int, withAnimation: Boolean)
        fun onTrashViewUpdated()
    }

    private class AdapterDataObservable: Observable<DataObserver>() {
        fun notifyChanged(withAnimation: Boolean) {
            for (observer in mObservers) {
                observer.onChange(withAnimation)
            }
        }

        fun notifyObjectChanged(index: Int, withAnimation: Boolean) {
            for (observer in mObservers) {
                observer.onObjectChanged(index, withAnimation)
            }
        }

        fun notifyObjectInserted(index: Int, withAnimation: Boolean) {
            for (observer in mObservers) {
                observer.onObjectInserted(index, withAnimation)
            }
        }

        fun notifyObjectRemoved(index: Int, withAnimation: Boolean) {
            for (observer in mObservers) {
                observer.onObjectRemoved(index, withAnimation)
            }
        }

        fun notifyTrashViewUpdated() {
            for (observer in mObservers) {
                observer.onTrashViewUpdated()
            }
        }
    }

    private val dataObservers =
        AdapterDataObservable()

    abstract fun getObjectsCount(): Int
    abstract fun getView(parent: ViewGroup, index: Int): VKCanvasObjectView
    abstract fun getTrashView(parent: ViewGroup): View?
    abstract fun getTrashViewBounds(): Rect?
    abstract fun onObjectStateChanged(index: Int, state: TransformState)
    abstract fun onObjectRemoved(index: Int)

    fun registerDataObserver(observer: DataObserver) {
        dataObservers.registerObserver(observer)
    }

    fun unregisterDataObserver(observer: DataObserver) {
        dataObservers.unregisterObserver(observer)
    }

    fun notifyDataSetChanged(withAnimation: Boolean) {
        dataObservers.notifyChanged(withAnimation)
    }

    fun notifyObjectChanged(index: Int, withAnimation: Boolean) {
        dataObservers.notifyObjectChanged(index, withAnimation)
    }

    fun notifyObjectInserted(index: Int, withAnimation: Boolean) {
        dataObservers.notifyObjectInserted(index, withAnimation)
    }

    fun notifyObjectRemoved(index: Int, withAnimation: Boolean) {
        dataObservers.notifyObjectRemoved(index, withAnimation)
    }

    fun notifyTrashViewUpdated() {
        dataObservers.notifyTrashViewUpdated()
    }

}