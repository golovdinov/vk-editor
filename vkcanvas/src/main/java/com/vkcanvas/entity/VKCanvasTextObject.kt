package com.vkcanvas.entity

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import com.vkcanvas.util.RoundedBackgroundColorSpan
import com.vkcanvas.util.ShadowSpan
import com.vkcanvas.util.dpToPx

class VKCanvasTextObject(
    id: String,
    val placeholder: String,
    val text: String,
    val style: TextStyle
): VKCanvasObject(id, TYPE_TEXT,
    SUBTYPE_TEXT
) {

    companion object {
        const val SUBTYPE_TEXT = "text"
    }

    fun toSpannableString(context: Context): SpannableString {
        val spannableString = SpannableString(text.trim())

        style.backgroundColor?.let {
            val bgSpan = RoundedBackgroundColorSpan(
                it,
                dpToPx(context, 4F).toInt(),
                dpToPx(context, 4F).toInt(),
                style.alignment
            )
            spannableString.setSpan(bgSpan, 0, spannableString.length, 0)
        }

        style.shadowColor?.let {
            spannableString.setSpan(
                ShadowSpan(
                    0F,
                    dpToPx(context, 1F),
                    0.5F,
                    it
                ),
                0,
                spannableString.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        }

        return spannableString
    }

}