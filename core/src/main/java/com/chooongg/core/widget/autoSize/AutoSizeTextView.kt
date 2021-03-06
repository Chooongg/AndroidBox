package com.chooongg.core.widget.autoSize

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textview.MaterialTextView

class AutoSizeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialTextView(context, attrs, defStyleAttr) {

    val helper = AutoSizeTextHelper.create(this, attrs, defStyleAttr)

}