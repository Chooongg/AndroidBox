package com.chooongg.core.widget.square

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import com.chooongg.core.R
import com.google.android.material.card.MaterialCardView

@SuppressLint("CustomViewStyleable")
class SquareCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private var basisAxis: Int = 0

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.SquareLayout,
                defStyleAttr,
                R.style.Widget_MaterialComponents_CardView
            )
            basisAxis = a.getInteger(R.styleable.SquareLayout_basisAxis, 0)
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize(0, widthMeasureSpec),
            getDefaultSize(0, heightMeasureSpec)
        )
        val size = MeasureSpec.makeMeasureSpec(
            if (basisAxis == 0) measuredWidth else measuredHeight, MeasureSpec.EXACTLY
        )
        super.onMeasure(size, size)
    }
}