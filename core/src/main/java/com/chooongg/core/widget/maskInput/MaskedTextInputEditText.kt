package com.chooongg.core.widget.maskInput

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.chooongg.core.R

class MaskedTextInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    private var mMaskedFormatter: MaskedFormatter? = null
    private var mMaskedWatcher: MaskedWatcher? = null

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    val maskString: String?
        get() = mMaskedFormatter?.maskString

    val unMaskedText: String?
        get() {
            val currentText = text?.toString()
            val formattedString = currentText?.let { mMaskedFormatter?.formatString(it) }
            return formattedString?.unMaskedString
        }

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.MaskedTextInputEditText,
            defStyleAttr,
            0
        )

        if (typedArray.hasValue(R.styleable.MaskedTextInputEditText_mask)) {
            val maskStr = typedArray.getString(R.styleable.MaskedTextInputEditText_mask)

            if (maskStr != null && maskStr.isNotEmpty()) {
                setMask(maskStr)
            }
        }

        typedArray.recycle()
    }

    fun setMask(mMaskStr: String) {
        mMaskedFormatter = MaskedFormatter(mMaskStr)

        if (mMaskedWatcher != null) {
            removeTextChangedListener(mMaskedWatcher)
        }

        mMaskedFormatter?.let { mMaskedWatcher = MaskedWatcher(it, this) }
        addTextChangedListener(mMaskedWatcher)
    }

}