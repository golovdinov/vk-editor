package com.vkcanvas.widgets

import android.animation.*
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.children
import com.vkcanvas.R
import com.vkcanvas.entity.TransformState
import com.vkcanvas.VKCanvasAdapter
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt


class VKCanvasView: RelativeLayout, VKCanvasAdapter.DataObserver {

    companion object {
        const val CLICK_THRESHOLD = 5F
        const val TRASH_THRESHOLD = 25F
    }

    private data class ObjectTransaction(
        val view: View,
        var newRotation: Float = 0F,
        var newWidth: Int = 0,
        var newHeight: Int = 0,
        var newLeftMargin: Int = 0,
        var newTopMargin: Int = 0
    )

    var adapter: VKCanvasAdapter? = null
        set(value) {
            if (field != null) {
                field?.unregisterDataObserver(this)
            }
            field = value
            field?.registerDataObserver(this)
        }

    // Этот колбек не в адаптере потому что он вроде как не связан с "данными"
    var onCanvasClickListener: (() -> Unit)? = null

    private var trashView: VKCanvasTrashView? = null
    private var trashViewBounds: Rect? = null
    private var transaction: ObjectTransaction? = null

    private var touchStartX = 0F
    private var touchStartY = 0F
    private var touchLastX = 0F
    private var touchLastY = 0F
    private var touchLastDistance = 0F
    private var touchLastAngle = 0F
    private var touchStartView: View? = null
    private var touchPointerId = 0
    private var trashTouchStartX = 0F // Помогают показывать корзину с зарержкой
    private var trashTouchStartY = 0F

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val scaleIn = AnimatorInflater.loadAnimator(context,
            R.animator.scale_in
        )
        val scaleOut = AnimatorInflater.loadAnimator(context,
            R.animator.scale_out
        )

        layoutTransition = LayoutTransition()
        layoutTransition.setAnimator(LayoutTransition.APPEARING, scaleIn)
        layoutTransition.setAnimator(LayoutTransition.DISAPPEARING, scaleOut)
        disableLayoutAnimation()
    }

    override fun onChange(withAnimation: Boolean) {
        if (adapter == null) {
            throw IllegalStateException("No Adapter!")
        }

        if (withAnimation) {
            enableLayoutAnimation()
        }

        removeAllViews()

        for (i in 0 until adapter!!.getObjectsCount()) {
            addView(adapter!!.getView(this, i) as View)
        }

        onTrashViewUpdated()

        if (withAnimation) {
            disableLayoutAnimation()
        }
    }

    override fun onObjectChanged(position: Int, withAnimation: Boolean) {
        if (adapter == null) {
            throw IllegalStateException("No Adapter!")
        }

        if (withAnimation) {
            enableLayoutAnimation()
        }

        removeViewAt(position)
        addView(adapter!!.getView(this, position) as View, position)

        if (withAnimation) {
            disableLayoutAnimation()
        }
    }

    override fun onObjectInserted(position: Int, withAnimation: Boolean) {
        if (adapter == null) {
            throw IllegalStateException("No Adapter!")
        }

        if (withAnimation) {
            enableLayoutAnimation()
        }

        addView(adapter!!.getView(this, position) as View, position)


        if (withAnimation) {
            disableLayoutAnimation()
        }
    }

    override fun onObjectRemoved(position: Int, withAnimation: Boolean) {
        if (adapter == null) {
            throw IllegalStateException("No Adapter!")
        }

        if (withAnimation) {
            enableLayoutAnimation()
        }

        removeViewAt(position)

        if (withAnimation) {
            disableLayoutAnimation()
        }
    }

    override fun onTrashViewUpdated() {
        trashView?.let {
            removeView(it as View)
        }

        adapter?.getTrashView(this)?.let {
            addView(it)
            it.visibility = View.INVISIBLE
            trashView = it as VKCanvasTrashView
        }

        trashViewBounds = adapter?.getTrashViewBounds()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return super.onTouchEvent(event)
        }

        when (event.action and MotionEvent.ACTION_MASK) {


            MotionEvent.ACTION_DOWN -> {
                val pointerIndex = event.actionIndex
                touchStartX = event.getX(pointerIndex)
                touchStartY = event.getY(pointerIndex)
                touchLastX = touchStartX
                touchLastY = touchStartY
                touchLastDistance = 0F
                touchLastAngle = 0F
                touchPointerId = event.getPointerId(pointerIndex)
                touchStartView = getTouchedObjectView()

                // Транзакцию начнем позже, потому что возможно это клик
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(touchPointerId)
                if (pointerIndex < 0) {
                    return false
                }

                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)
                val distance = getDistance(event)
                val angle = getAngle(event)

                // Сначала отметаем микро-движения, чтобы отделить клик от драга
                val touchDiffX = abs(x - touchStartX)
                val touchDiffY = abs(y - touchStartX)

                if (touchDiffX < CLICK_THRESHOLD && touchDiffY < CLICK_THRESHOLD) {
                    touchLastX = x
                    touchLastY = y
                    return true
                }

                if (touchLastDistance == 0F) {
                    touchLastDistance = distance
                }
                if (touchLastAngle == 0F) {
                    touchLastAngle = angle
                }

                // Это точно не клик
                // Стартуем транзакцию, если еще не сделали это
                if (touchStartView != null && transaction == null) {
                    beginTransaction(touchStartView!!)
                }

                // Двигаем объект
                transaction?.let {
                    doDragScaleRotate(event, it, x, y, distance, angle)

                    if (event.pointerCount == 1) {
                        // Для того чтобы показать корзину с задержкой
                        if (trashTouchStartX == 0F || trashTouchStartY == 0F) {
                            trashTouchStartX = x
                            trashTouchStartY = y
                        }

                        // Показываем корзину, которую скрыли при появлении 2-го пальца
                        showTrashIfCan(x, y)

                        // Активируем корзину, если навели на неё
                        activateTrashIfNeeded(x, y)
                    }
                }

                touchLastX = x
                touchLastY = y
                touchLastDistance = distance
                touchLastAngle = angle
            }

            MotionEvent.ACTION_UP -> {
                transaction?.let {
                    commitTransaction(it)
                }

                // Это был клик
                val touchDiffX = abs(touchLastX - touchStartX)
                val touchDiffY = abs(touchLastY - touchStartY)

                if (touchDiffX < CLICK_THRESHOLD && touchDiffY < CLICK_THRESHOLD) {
                    onCanvasClickListener?.invoke()
                }

                transaction = null
                touchStartView = null
                touchStartX = 0F
                touchStartY = 0F
                touchLastX = 0F
                touchLastY = 0F
                touchLastDistance = 0F
                touchLastAngle = 0F
                touchPointerId = 0
            }
            MotionEvent.ACTION_CANCEL -> {
                transaction?.let {
                    commitTransaction(it)
                }
                transaction = null
                touchStartView = null
                touchStartX = 0F
                touchStartY = 0F
                touchLastX = 0F
                touchLastY = 0F
                touchLastDistance = 0F
                touchLastAngle = 0F
                touchPointerId = 0
            }

            MotionEvent.ACTION_POINTER_DOWN -> {

                // Скрываем корзину, когда пальцев становится больше 1
                // Покажем карзину только на ACTION_MOVE
                if (event.pointerCount > 1) {
                    hideTrash()
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.findPointerIndex(pointerIndex)

                if (pointerId == touchPointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    touchLastX = event.getX(newPointerIndex)
                    touchLastY = event.getY(newPointerIndex)
                    touchPointerId = event.findPointerIndex(newPointerIndex)
                }

                touchLastDistance = 0F
                touchLastAngle = 0F
            }
        }
        return true
    }

    private fun doDragScaleRotate(
        event: MotionEvent,
        transaction: ObjectTransaction,
        x: Float,
        y: Float,
        distance: Float,
        angle: Float
    ) {
        val dx = x - touchLastX
        val dy = y - touchLastY
        val dDistance = distance - touchLastDistance
        val dAngle = angle - touchLastAngle

        val lp = transaction.view.layoutParams as RelativeLayout.LayoutParams

        // Drag
        lp.leftMargin += dx.toInt()
        lp.topMargin += dy.toInt()

        // Scale
        lp.width += dDistance.toInt()
        lp.height += dDistance.toInt()

        lp.width = min(width*2, lp.width)
        lp.height = min(height*2, lp.height)

        // Rotate
        transaction.view.rotation += dAngle

        // При необходимости добавляем отрицательные отступы справа/снизу,
        // чтобы вьюха не кукожилась в нижнем правом углу
        lp.apply {
            rightMargin = when {
                leftMargin + width > this@VKCanvasView.width -> {
                    this@VKCanvasView.width - leftMargin - width
                }
                else -> 0
            }
            bottomMargin = when {
                topMargin + height > this@VKCanvasView.height -> {
                    this@VKCanvasView.height - topMargin - height
                }
                else -> 0
            }
        }

        transaction.apply {
            newWidth = lp.width
            newHeight = lp.height
            newLeftMargin = lp.leftMargin
            newTopMargin = lp.topMargin
            newRotation = view.rotation

            view.requestLayout()
        }
    }

    private fun beginTransaction(view: View) {
        (view.layoutParams as RelativeLayout.LayoutParams).let {
            transaction = ObjectTransaction(view)
        }
    }

    private fun commitTransaction(transaction: ObjectTransaction) {
        (transaction.view as VKCanvasObjectView).isTouchedForTransform = false
        for (i in 0 until childCount) {
            (getChildAt(i) as? VKCanvasObjectView)?.touchId = Int.MAX_VALUE
        }

        val viewIndex = children.indexOf(transaction.view)

        // Случается, если мы успели сделать несколько транзакций до апдейта вьюх
        if (viewIndex < 0) {
            return
        }

        val shouldBeRemoved = trashView?.isTrashActivated == true

        when {
            shouldBeRemoved -> {
                adapter?.onObjectRemoved(viewIndex)
            }
            else -> {
                val newState = TransformState(
                    Point(transaction.newLeftMargin, transaction.newTopMargin),
                    Size(transaction.newWidth, transaction.newHeight),
                    transaction.newRotation
                )

                adapter?.onObjectStateChanged(viewIndex, newState)
            }
        }

        hideTrash()
    }

    private fun getTouchedObjectView(): View? {
        // Находим объект, на который нажал пользователь.
        // Если под пальцем было несколько объектов,
        // то определить верхний, помогает touchId

        var minTouchId = Int.MAX_VALUE
        var topViewDraggable: VKCanvasObjectView? = null

        for (i in 0 until childCount) {
            val child = getChildAt(i) as? VKCanvasObjectView
            if (child != null
                && child.isTouchedForTransform
                && child.touchId < minTouchId) {

                topViewDraggable = child
                minTouchId = child.touchId
            }
        }

        return topViewDraggable as? View
    }

    private fun showTrashIfCan(x: Float, y: Float) {
        val diffX = abs(x - trashTouchStartX)
        val diffY = abs(y - trashTouchStartY)
        if (diffX > TRASH_THRESHOLD && diffY > TRASH_THRESHOLD) {
            (trashView as? View)?.apply {
                enableLayoutAnimation()
                visibility = View.VISIBLE
                disableLayoutAnimation()
            }
        }
    }

    private fun hideTrash() {
        trashTouchStartX = 0F
        trashTouchStartY = 0F

        trashView?.isTrashActivated = false
        (trashView as? View)?.apply {
            if (visibility == View.INVISIBLE) {
                return
            }
            enableLayoutAnimation()
            visibility = View.INVISIBLE
            disableLayoutAnimation()
        }
    }

    private fun activateTrashIfNeeded(x: Float, y: Float) {
        trashViewBounds?.let {
            trashView?.isTrashActivated = isInsideTrashBounds(x.toInt(), y.toInt(), it)
        }
    }

    private fun isInsideTrashBounds(pointerX: Int, pointerY: Int, trashViewBounds: Rect): Boolean {
        return pointerX >= trashViewBounds.left && pointerX <= trashViewBounds.right &&
                pointerY >= trashViewBounds.top && pointerY <= trashViewBounds.bottom
    }

    private fun enableLayoutAnimation() {
        layoutTransition.enableTransitionType(LayoutTransition.APPEARING)
        layoutTransition.enableTransitionType(LayoutTransition.DISAPPEARING)
    }

    private fun disableLayoutAnimation() {
        layoutTransition.disableTransitionType(LayoutTransition.APPEARING)
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING)
    }

    private fun getDistance(event: MotionEvent): Float {
        if (event.pointerCount < 2) {
            return 0F
        }
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        val s = x * x + y * y
        return sqrt(s.toDouble()).toFloat()
    }

    private fun getAngle(event: MotionEvent): Float {
        if (event.pointerCount < 2) {
            return 0F
        }
        val dX = (event.getX(0) - event.getX(1)).toDouble()
        val dY = (event.getY(0) - event.getY(1)).toDouble()
        val radians = atan2(dY, dX)
        return Math.toDegrees(radians).toFloat()
    }

    /*private fun getTouchCenter(event: MotionEvent): Point {
        if (event.pointerCount < 2) {
            return Point(event.getRawX(0).toInt(), event.getRawY(0).toInt())
        }

        val x1 = event.getRawX(0)
        val y1 = event.getRawX(0)

        val x2 = event.getRawX(1)
        val y2 = event.getRawX(1)

        val minX = min(x1, x2)
        val maxX = max(x1, x2)

        val minY = min(y1, y2)
        val maxY = max(y1, y2)

        val centerX = minX + (maxX - minX) / 2
        val centerY = minY + (maxY - minY) / 2

        return Point(centerX.toInt(), centerY.toInt())
    }*/

}