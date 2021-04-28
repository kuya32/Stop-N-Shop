package com.macode.stopnshop.utilities

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton

class SNSButton(context: Context, attributeSet: AttributeSet): AppCompatButton(context, attributeSet) {
    init {
        applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface = Typeface.createFromAsset(context.assets, "OrelegaOne-Regular.ttf")
        setTypeface(typeface)
    }
}