package com.chooongg.core.widget

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.TextViewCompat
import com.chooongg.core.R
import com.chooongg.core.ext.EmojiExcludeFilter
import com.chooongg.ext.clipboardManager
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.min

/**
 * 基于 XEditText 修改
 */
class BoxTextInputEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_PADDING = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8f, Resources.getSystem().displayMetrics
        ).toInt()
    }

    private var mSeparator // the separator，default is "".
            : String? = null
    private var mClearResId = 0
    private var mShowPwdResId = 0
    private var mHidePwdResId = 0
    private var mClearDrawableTint: ColorStateList? = null
    private var mTogglePwdDrawableTint: ColorStateList? = null
    private var mInteractionPadding // padding of drawables' interactive rect area.
            = 0
    private var disableClear // disable the clear drawable.
            = false
    private var togglePwdDrawableEnable // be able to use togglePwdDrawables.
            = false
    private var disableEmoji // disable emoji and some special symbol input.
            = false

    private var mClearDrawable: Drawable? = null
    private var mTogglePwdDrawable: Drawable? = null
    private var mXTextChangeListener: OnXTextChangeListener? = null
    private var mXFocusChangeListener: OnFocusChangeListener? = null
    private var mOnClearListener: OnClearListener? = null
    private var mTextWatcher = MyTextWatcher()
    private var mOldLength = 0
    private var mNowLength = 0
    private var mSelectionPos = 0
    private var hasFocused = false
    private var pattern: IntArray? = null
    private var intervals: IntArray? = null
    private var hasNoSeparator = false
    private var isPwdInputType = false
    private var isPwdShow = false
    private var mBitmap: Bitmap? = null
    private var mStart = 0
    private var mTop: Int = 0
    private var mHalfPadding = 0

    init {
        initAttrs(context, attrs, defStyleAttr)
        addTextChangedListener(mTextWatcher)
        super.setOnFocusChangeListener { v, hasFocus ->
            hasFocused = hasFocus
            logicOfCompoundDrawables()
            mXFocusChangeListener?.onFocusChange(v, hasFocus)
        }
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.BoxTextInputEditText, defStyleAttr, 0)
        mSeparator = a.getString(R.styleable.BoxTextInputEditText_separator)
        disableClear = a.getBoolean(R.styleable.BoxTextInputEditText_disableClear, false)
        mClearResId = a.getResourceId(
            R.styleable.BoxTextInputEditText_clearDrawable,
            R.drawable.ic_edit_clear
        )
        togglePwdDrawableEnable =
            a.getBoolean(R.styleable.BoxTextInputEditText_togglePwdDrawableEnable, true)
        mShowPwdResId = a.getResourceId(
            R.styleable.BoxTextInputEditText_showPwdDrawable,
            R.drawable.ic_edit_show_password
        )
        mHidePwdResId = a.getResourceId(
            R.styleable.BoxTextInputEditText_hidePwdDrawable,
            R.drawable.ic_edit_hide_password
        )
        mClearDrawableTint =
            if (a.hasValue(R.styleable.BoxTextInputEditText_clearDrawableTint)) {
                a.getColorStateList(R.styleable.BoxTextInputEditText_clearDrawableTint)
            } else {
                ColorStateList.valueOf(currentHintTextColor)
            }
        if (mShowPwdResId == R.drawable.ic_edit_show_password &&
            mHidePwdResId == R.drawable.ic_edit_hide_password
        ) {
            // didn't customize toggle pwd drawables
            mTogglePwdDrawableTint =
                if (a.hasValue(R.styleable.BoxTextInputEditText_togglePwdDrawableTint)) {
                    a.getColorStateList(R.styleable.BoxTextInputEditText_togglePwdDrawableTint)
                } else {
                    ColorStateList.valueOf(currentHintTextColor)
                }
        } else {
            if (a.hasValue(R.styleable.BoxTextInputEditText_togglePwdDrawableTint)) {
                mTogglePwdDrawableTint =
                    a.getColorStateList(R.styleable.BoxTextInputEditText_togglePwdDrawableTint)
            }
        }
        disableEmoji = a.getBoolean(R.styleable.BoxTextInputEditText_disableEmoji, false)
        val pattern = a.getString(R.styleable.BoxTextInputEditText_pattern)
        mInteractionPadding =
            a.getDimensionPixelSize(
                R.styleable.BoxTextInputEditText_interactionPadding,
                DEFAULT_PADDING
            )
        a.recycle()
        if (mSeparator == null) {
            mSeparator = ""
        }
        hasNoSeparator = TextUtils.isEmpty(mSeparator)
        if (mSeparator!!.isNotEmpty()) {
            val inputType = inputType
            if (inputType == 2 || inputType == 8194 || inputType == 4098) {
                // If the inputType is number, the separator can't be inserted, so change to phone type.
                setInputType(InputType.TYPE_CLASS_PHONE)
            }
        }
        if (mInteractionPadding < 0) mInteractionPadding = 0
        mHalfPadding = mInteractionPadding shr 1
        if (!disableClear) {
            val d = AppCompatResources.getDrawable(context, mClearResId)
            if (d != null) {
                mClearDrawable = DrawableCompat.wrap(d)
                mClearDrawable!!.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
                DrawableCompat.setTintList(mClearDrawable!!.mutate(), mClearDrawableTint)
            }
        }
        dealWithInputTypes(true)
        if (mSeparator!!.isNotEmpty() && !isPwdInputType && pattern != null && pattern.isNotEmpty()) {
            var ok = true
            if (pattern.contains(",")) {
                val split = pattern.split(",").toTypedArray()
                val array = IntArray(split.size)
                for (i in array.indices) {
                    try {
                        array[i] = split[i].toInt()
                    } catch (e: Exception) {
                        ok = false
                        break
                    }
                }
                if (ok) {
                    setPattern(array, mSeparator!!)
                }
            } else {
                try {
                    val i = pattern.toInt()
                    setPattern(intArrayOf(i), mSeparator!!)
                } catch (e: Exception) {
                    ok = false
                }
            }
            if (!ok) {
                Log.e("XEditText", "the Pattern format is incorrect!")
            }
        }
        if (disableEmoji) {
            val oldFilters = filters
            val newFilters = arrayOfNulls<InputFilter>(oldFilters.size + 1)
            newFilters[oldFilters.size] = EmojiExcludeFilter()
            System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.size)
            filters = newFilters
        }
    }

    private fun dealWithInputTypes(fromXml: Boolean) {
        var inputType = inputType
        if (!fromXml) {
            inputType++
            if (inputType == 17) inputType++
        }
        isPwdInputType =
            togglePwdDrawableEnable && (inputType == 129 || inputType == 18 || inputType == 145 || inputType == 225)
        if (isPwdInputType) {
            isPwdShow = inputType == 145 // textVisiblePassword
            transformationMethod = if (isPwdShow) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            val d = AppCompatResources.getDrawable(
                context,
                if (isPwdShow) mShowPwdResId else mHidePwdResId
            )
            if (d != null) {
                mTogglePwdDrawable = DrawableCompat.wrap(d)
                mTogglePwdDrawable!!.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
                if (mTogglePwdDrawableTint != null) {
                    DrawableCompat.setTintList(
                        mTogglePwdDrawable!!.mutate(),
                        mTogglePwdDrawableTint
                    )
                }
            }
            if (!disableClear && mClearDrawable != null) {
                mBitmap = Bitmap.createBitmap(
                    mClearDrawable!!.intrinsicWidth,
                    mClearDrawable!!.intrinsicHeight, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(mBitmap!!)
                mClearDrawable!!.draw(canvas)
                compoundDrawablePadding += mBitmap!!.width + (mInteractionPadding shl 1)
            }
        }
        if (!fromXml) {
            setTextEx(getTextEx())
            logicOfCompoundDrawables()
        }
    }

    override fun setInputType(type: Int) {
        super.setInputType(type)
        dealWithInputTypes(false)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        logicOfCompoundDrawables()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hasFocused && !disableClear && mBitmap != null && isPwdInputType && !isTextEmpty()) {
            if (isRtl()) {
                if (mStart * mTop == 0) {
                    mStart = (ViewCompat.getPaddingEnd(this) + mTogglePwdDrawable!!.intrinsicWidth
                            + mInteractionPadding)
                    mTop = height - mBitmap!!.height shr 1
                }
            } else {
                if (mStart * mTop == 0) {
                    mStart =
                        (width - ViewCompat.getPaddingEnd(this) - mTogglePwdDrawable!!.intrinsicWidth
                                - mBitmap!!.width - mInteractionPadding)
                    mTop = height - mBitmap!!.height shr 1
                }
            }
            // When the inputted content is too long, getScrollX() can fix the offset.
            canvas.drawBitmap(mBitmap!!, mStart.toFloat() + scrollX, mTop.toFloat(), null)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return super.onTouchEvent(event)
        }
        if (hasFocused && isPwdInputType && event.action == MotionEvent.ACTION_UP) {
            val dw = mTogglePwdDrawable!!.intrinsicWidth
            val dh = mTogglePwdDrawable!!.intrinsicHeight
            val top = height - dh shr 1
            var end: Int
            var inAreaX: Boolean
            val inAreaY =
                event.y >= top - mInteractionPadding && event.y <= top + dh + mInteractionPadding
            val eventX = event.x
            if (isRtl()) {
                end = ViewCompat.getPaddingEnd(this)
                inAreaX = eventX >= end - mHalfPadding && eventX <= end + dw + mHalfPadding
            } else {
                end = width - ViewCompat.getPaddingEnd(this)
                inAreaX = eventX <= end + mHalfPadding && eventX >= end - dw - mHalfPadding
            }
            if (inAreaX && inAreaY) {
                isPwdShow = !isPwdShow
                transformationMethod = if (isPwdShow) {
                    HideReturnsTransformationMethod.getInstance()
                } else {
                    PasswordTransformationMethod.getInstance()
                }
                setSelection(selectionStart, selectionEnd)
                val d = AppCompatResources.getDrawable(
                    context,
                    if (isPwdShow) mShowPwdResId else mHidePwdResId
                )
                if (d != null) {
                    mTogglePwdDrawable = DrawableCompat.wrap(d)
                    mTogglePwdDrawable!!.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
                    if (mTogglePwdDrawableTint != null) {
                        DrawableCompat.setTintList(
                            mTogglePwdDrawable!!.mutate(),
                            mTogglePwdDrawableTint
                        )
                    }
                    setCompoundDrawablesCompat(mTogglePwdDrawable)
                }
            }
            if (!disableClear) {
                if (isRtl()) {
                    end += dw + mInteractionPadding
                    inAreaX =
                        eventX >= end - mHalfPadding && eventX <= end + mBitmap!!.width + mHalfPadding
                } else {
                    end -= dw + mInteractionPadding
                    inAreaX =
                        eventX <= end + mHalfPadding && eventX >= end - mBitmap!!.width - mHalfPadding
                }
                if (inAreaX && inAreaY) {
                    error = null
                    val editable = text
                    editable?.clear()
                    if (mOnClearListener != null) {
                        mOnClearListener!!.onClear()
                    }
                }
            }
        }
        if (hasFocused && !disableClear && !isPwdInputType && event.action == MotionEvent.ACTION_UP) {
            val dw = mClearDrawable!!.intrinsicWidth
            val dh = mClearDrawable!!.intrinsicHeight
            val top = height - dh shr 1
            val end: Int
            val inAreaX: Boolean
            val inAreaY =
                event.y >= top - mInteractionPadding && event.y <= top + dh + mInteractionPadding
            val eventX = event.x
            if (isRtl()) {
                end = ViewCompat.getPaddingEnd(this)
                inAreaX =
                    eventX >= end - mInteractionPadding && eventX <= end + dw + mInteractionPadding
            } else {
                end = width - ViewCompat.getPaddingEnd(this)
                inAreaX =
                    eventX <= end + mInteractionPadding && eventX >= end - dw - mInteractionPadding
            }
            if (inAreaX && inAreaY) {
                error = null
                val editable = text
                editable?.clear()
                if (mOnClearListener != null) {
                    mOnClearListener!!.onClear()
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        val clipboardManager = context.clipboardManager
        if (id == 16908320 || id == 16908321) { // catch CUT or COPY ops
            super.onTextContextMenuItem(id)
            val clip: ClipData? = clipboardManager.primaryClip
            if (clip != null) {
                val item = clip.getItemAt(0)
                if (item != null && item.text != null) {
                    val s = item.text.toString().replace(mSeparator!!, "")
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(null, s))
                    return true
                }
            }
        } else if (id == 16908322) { // catch PASTE ops
            val clip: ClipData? = clipboardManager.primaryClip
            if (clip != null) {
                val item = clip.getItemAt(0)
                if (item != null && item.text != null) {
                    val content = item.text.toString().replace(mSeparator!!, "")
                    val existedTxt = getTextNoneNull()
                    var txt: String?
                    val start = selectionStart
                    val end = selectionEnd
                    if (start * end >= 0) {
                        val startHalfEx =
                            existedTxt.substring(0, start).replace(mSeparator!!, "")
                        txt = startHalfEx + content
                        val endHalfEx = existedTxt.substring(end).replace(mSeparator!!, "")
                        txt += endHalfEx
                    } else {
                        txt = existedTxt.replace(mSeparator!!, "") + content
                    }
                    setTextEx(txt)
                    return true
                }
            }
        }
        return super.onTextContextMenuItem(id)
    }

    private fun isRtl(): Boolean {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    // =========================== MyTextWatcher ================================
    private inner class MyTextWatcher : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            mOldLength = s.length
            if (mXTextChangeListener != null) {
                mXTextChangeListener!!.beforeTextChanged(s, start, count, after)
            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            mNowLength = s.length
            mSelectionPos = selectionStart
            if (mXTextChangeListener != null) {
                mXTextChangeListener!!.onTextChanged(s, start, before, count)
            }
        }

        override fun afterTextChanged(s: Editable) {
            logicOfCompoundDrawables()
            if (mSeparator.isNullOrEmpty()) {
                if (mXTextChangeListener != null) {
                    mXTextChangeListener!!.afterTextChanged(s)
                }
                return
            }
            removeTextChangedListener(mTextWatcher)
            val trimmedText = if (hasNoSeparator) {
                s.toString().trim { it <= ' ' }
            } else {
                s.toString().replace(mSeparator!!.toRegex(), "").trim { it <= ' ' }
            }
            setTextToSeparate(trimmedText, false)
            if (mXTextChangeListener != null) {
                s.clear()
                s.append(trimmedText)
                mXTextChangeListener!!.afterTextChanged(s)
            }
            addTextChangedListener(mTextWatcher)
        }
    }

    private fun logicOfCompoundDrawables() {
        if (!isEnabled || !hasFocused || isTextEmpty() && !isPwdInputType) {
            setCompoundDrawablesCompat(null)
            if (!isTextEmpty() && isPwdInputType) {
                invalidate()
            }
        } else {
            if (isPwdInputType) {
                if (mTogglePwdDrawableTint != null) DrawableCompat.setTintList(
                    mTogglePwdDrawable!!.mutate(),
                    mTogglePwdDrawableTint
                )
                setCompoundDrawablesCompat(mTogglePwdDrawable)
            } else if (!isTextEmpty() && !disableClear) {
                setCompoundDrawablesCompat(mClearDrawable)
            }
        }
    }

    private fun setCompoundDrawablesCompat(drawableEnd: Drawable?) {
        val drawables = TextViewCompat.getCompoundDrawablesRelative(this)
        TextViewCompat.setCompoundDrawablesRelative(
            this,
            drawables[0], drawables[1], drawableEnd, drawables[3]
        )
    }

    private fun isTextEmpty(): Boolean {
        return getTextNoneNull().trim { it <= ' ' }.isEmpty()
    }


    // =================================== APIs begin ========================================
    fun getSeparator(): String? {
        return mSeparator
    }

    /**
     * Set custom separator.
     */
    fun setSeparator(separator: String) {
        if (mSeparator == separator) return
        mSeparator = separator
        hasNoSeparator = TextUtils.isEmpty(mSeparator)
        if (mSeparator!!.isNotEmpty()) {
            val inputType = inputType
            if (inputType == 2 || inputType == 8194 || inputType == 4098) {
                // If the inputType is number, the separator can't be inserted, so change to phone type.
                setInputType(InputType.TYPE_CLASS_PHONE)
            }
        }
    }

    /**
     * Set custom pattern.
     *
     * @param pattern   e.g. pattern:{4,4,4,4}, separator:"-" to xxxx-xxxx-xxxx-xxxx
     * @param separator separator
     */
    fun setPattern(pattern: IntArray, separator: String) {
        setSeparator(separator)
        setPattern(pattern)
    }

    /**
     * Set custom pattern.
     *
     * @param pattern e.g. pattern:{4,4,4,4}, separator:"-" to xxxx-xxxx-xxxx-xxxx
     */
    fun setPattern(pattern: IntArray) {
        this.pattern = pattern
        intervals = IntArray(pattern.size)
        var sum = 0
        for (i in pattern.indices) {
            sum += pattern[i]
            intervals!![i] = sum
        }
        /* When you set pattern, it will automatically compute the max length of characters and separators,
           so you don't need to set 'maxLength' attr in your xml any more(it won't work).*/
        val maxLength = intervals!![intervals!!.size - 1] + pattern.size - 1
        val oldFilters = filters
        val list: MutableList<InputFilter> = ArrayList()
        for (filter in oldFilters) {
            if (filter !is InputFilter.LengthFilter) list.add(filter)
        }
        list.add(InputFilter.LengthFilter(maxLength))
        filters = list.toTypedArray()
    }

    /**
     * Set CharSequence to separate.
     *
     */
    @Deprecated("Call {@link #setTextEx(CharSequence)} instead.")
    fun setTextToSeparate(c: CharSequence) {
        setTextToSeparate(c, true)
    }

    /**
     * Call [.setText] or set text to separate by the pattern had been set.
     * <br></br>
     * It's especially convenient to call [.setText] in Kotlin.
     */
    fun setTextEx(text: CharSequence) {
        if (TextUtils.isEmpty(text) || hasNoSeparator) {
            setText(text)
            setSelection(getTextNoneNull().length)
        } else {
            setTextToSeparate(text, true)
        }
    }

    private fun setTextToSeparate(c: CharSequence, fromUser: Boolean) {
        if (c.isEmpty() || intervals == null) {
            return
        }
        val builder = StringBuilder()
        var i = 0
        val length1 = c.length
        while (i < length1) {
            builder.append(c.subSequence(i, i + 1))
            var j = 0
            val length2 = intervals!!.size
            while (j < length2) {
                if (i == intervals!![j] && j < length2 - 1) {
                    builder.insert(builder.length - 1, mSeparator)
                    if (mSelectionPos == builder.length - 1 && mSelectionPos > intervals!![j]) {
                        if (mNowLength > mOldLength) { // inputted
                            mSelectionPos += mSeparator!!.length
                        } else { // deleted
                            mSelectionPos -= mSeparator!!.length
                        }
                    }
                }
                j++
            }
            i++
        }
        val text = builder.toString()
        setText(text)
        if (fromUser) {
            val maxLength = intervals!![intervals!!.size - 1] + pattern!!.size - 1
            val index = min(maxLength, text.length)
            try {
                setSelection(index)
            } catch (e: IndexOutOfBoundsException) {
                // Last resort (￣▽￣)
                val message = e.message
                if (!TextUtils.isEmpty(message) && message!!.contains(" ")) {
                    val last = message.lastIndexOf(" ")
                    val lenStr = message.substring(last + 1)
                    if (TextUtils.isDigitsOnly(lenStr)) {
                        setSelection(lenStr.toInt())
                    }
                }
            }
        } else {
            if (mSelectionPos > text.length) {
                mSelectionPos = text.length
            }
            if (mSelectionPos < 0) {
                mSelectionPos = 0
            }
            setSelection(mSelectionPos)
        }
    }

    /**
     * Get text string had been trimmed.
     */
    fun getTextTrimmed(): String {
        return getTextEx().trim { it <= ' ' }
    }

    /**
     * Get text string without separator.
     */
    fun getTextEx(): String {
        return if (hasNoSeparator) {
            getTextNoneNull()
        } else {
            getTextNoneNull().replace(mSeparator!!.toRegex(), "")
        }
    }

    /**
     * Get text String had been trimmed.
     *
     */
    @Deprecated("Call {@link #getTextTrimmed()} instead.")
    fun getTrimmedString(): String {
        return if (hasNoSeparator) {
            getTextNoneNull().trim { it <= ' ' }
        } else {
            getTextNoneNull().replace(mSeparator!!.toRegex(), "").trim { it <= ' ' }
        }
    }

    private fun getTextNoneNull(): String {
        val editable = text
        return editable?.toString() ?: ""
    }

    fun hasNoSeparator(): Boolean {
        return hasNoSeparator
    }

    /**
     * Set no separator, just like a @[android.widget.EditText].
     */
    fun setNoSeparator() {
        hasNoSeparator = true
        mSeparator = ""
        intervals = null
    }

    fun setClearDrawable(@DrawableRes resId: Int) {
        mClearResId = resId
        setClearDrawable(AppCompatResources.getDrawable(context, resId))
    }

    fun setClearDrawable(drawable: Drawable?) {
        if (!disableClear && drawable != null) {
            mClearDrawable = DrawableCompat.wrap(drawable)
            mClearDrawable!!.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            if (mClearDrawableTint != null) DrawableCompat.setTintList(
                mClearDrawable!!.mutate(),
                mClearDrawableTint
            )
        }
    }

    fun setTogglePwdDrawables(@DrawableRes showResId: Int, @DrawableRes hideResId: Int) {
        mShowPwdResId = showResId
        mHidePwdResId = hideResId
        setTogglePwdDrawables(
            AppCompatResources.getDrawable(context, showResId),
            AppCompatResources.getDrawable(context, showResId)
        )
    }

    fun setTogglePwdDrawables(
        showDrawable: Drawable?,
        hideDrawable: Drawable?
    ) {
        if (isPwdShow && showDrawable != null) {
            mTogglePwdDrawable = DrawableCompat.wrap(showDrawable)
            mTogglePwdDrawable!!.setBounds(
                0,
                0,
                showDrawable.intrinsicWidth,
                showDrawable.intrinsicHeight
            )
            if (mTogglePwdDrawableTint != null) DrawableCompat.setTintList(
                mTogglePwdDrawable!!.mutate(),
                mTogglePwdDrawableTint
            )
        }
        if (!isPwdShow && hideDrawable != null) {
            mTogglePwdDrawable = DrawableCompat.wrap(hideDrawable)
            mTogglePwdDrawable!!.setBounds(
                0,
                0,
                hideDrawable.intrinsicWidth,
                hideDrawable.intrinsicHeight
            )
            if (mTogglePwdDrawableTint != null) DrawableCompat.setTintList(
                mTogglePwdDrawable!!.mutate(),
                mTogglePwdDrawableTint
            )
        }
    }

    fun setClearDrawableTint(colorStateList: ColorStateList) {
        mClearDrawableTint = colorStateList
        if (mClearDrawable != null) DrawableCompat.setTintList(
            mClearDrawable!!.mutate(),
            colorStateList
        )
    }

    fun setTogglePwdDrawablesTint(colorStateList: ColorStateList) {
        mTogglePwdDrawableTint = colorStateList
        if (mTogglePwdDrawable != null) DrawableCompat.setTintList(
            mTogglePwdDrawable!!.mutate(),
            colorStateList
        )
    }

    fun setInteractionPadding(paddingInDp: Int) {
        if (paddingInDp >= 0) {
            mInteractionPadding = paddingInDp
            mHalfPadding = paddingInDp shr 1
        }
    }

    fun setDisableClear(disable: Boolean) {
        if (disableClear == disable) return
        disableClear = disable
        if (isPwdInputType && mBitmap != null) {
            var padding = compoundDrawablePadding
            if (disable) {
                padding -= mBitmap!!.width + mInteractionPadding
            } else {
                padding += mBitmap!!.width + mInteractionPadding
            }
            compoundDrawablePadding = padding
        }
    }

    fun setTogglePwdDrawableEnable(enable: Boolean) {
        if (togglePwdDrawableEnable == enable) return
        togglePwdDrawableEnable = enable
        dealWithInputTypes(false)
    }

    fun setDisableEmoji(disableEmoji: Boolean) {
        if (this.disableEmoji == disableEmoji) return
        this.disableEmoji = disableEmoji
        val oldFilters = filters
        val newFilters: Array<InputFilter?>
        if (disableEmoji) {
            newFilters = arrayOfNulls(oldFilters.size + 1)
            newFilters[oldFilters.size] = EmojiExcludeFilter()
            System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.size)
        } else {
            val list: MutableList<InputFilter> = ArrayList()
            for (filter in oldFilters) {
                if (filter !is EmojiExcludeFilter) list.add(filter)
            }
            newFilters = list.toTypedArray()
        }
        filters = newFilters
    }

    override fun setOnFocusChangeListener(l: OnFocusChangeListener?) {
        mXFocusChangeListener = l
    }

    fun setOnClearListener(listener: OnClearListener?) {
        mOnClearListener = listener
    }

    /**
     * OnXTextChangeListener is to XEditText what OnTextChangeListener is to EditText.
     */
    interface OnXTextChangeListener {
        fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)
        fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)
        fun afterTextChanged(s: Editable?)
    }

    /**
     * Interface definition for a callback to be invoked when the clear drawable is clicked.
     */
    interface OnClearListener {
        fun onClear()
    }

    // =================================== APIs end ========================================

    // =================================== APIs end ========================================
    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("save_instance", super.onSaveInstanceState())
        bundle.putString("separator", mSeparator)
        bundle.putIntArray("pattern", pattern)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            mSeparator = state.getString("separator")
            pattern = state.getIntArray("pattern")
            hasNoSeparator = TextUtils.isEmpty(mSeparator)
            if (pattern != null) {
                setPattern(pattern!!)
            }
            super.onRestoreInstanceState(state.getParcelable("save_instance"))
            return
        }
        super.onRestoreInstanceState(state)
    }
}