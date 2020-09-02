package com.vkcanvas.widgets

import android.content.Context
import android.graphics.Typeface
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import com.vkcanvas.entity.VKCanvasTextObject


class VKCanvasTextObjectView: AppCompatEditText,
    VKCanvasObjectView {

    var onTextChangedListener: ((String) -> Unit)? = null

    private val textObject: VKCanvasTextObject

    override var isTouchedForTransform = false // Не используем в этом классе
    override var touchId = Int.MAX_VALUE // Не используем в этом классе

    constructor(context: Context, textObject: VKCanvasTextObject) : this(context, textObject, null)
    constructor(context: Context, textObject: VKCanvasTextObject, attrs: AttributeSet?) : this(context, textObject, attrs, 0)
    constructor(context: Context, textObject: VKCanvasTextObject, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, android.R.attr.editTextStyle)  {
        this.textObject = textObject
        setup()
    }

    private fun setup() {
        setText(textObject.toSpannableString(context))
        setTextColor(textObject.style.textColor)
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, textObject.style.textSizeDp)
        typeface = Typeface.defaultFromStyle(textObject.style.typeface)
        gravity = Gravity.CENTER_HORIZONTAL
        background = null
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        hint = textObject.placeholder

        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        }

        layoutParams = lp
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        text?.let {
            if (it.length > 256) {
                setText(it.substring(0, 256))
            } else {
                onTextChangedListener?.invoke(it.toString())
            }
        }
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

    fun requestFocusAndOpenKeyboard() {
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

}