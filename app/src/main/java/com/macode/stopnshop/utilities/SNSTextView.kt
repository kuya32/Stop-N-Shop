package com.macode.stopnshop.utilities

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class SNSTextView(context: Context, attributeSet: AttributeSet): AppCompatTextView(context, attributeSet) {
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "OrelegaOne-Regular.ttf")
        setTypeface(typeface)
    }
}