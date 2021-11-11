package com.chooongg.autoHideIME

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.FrameLayout
import com.chooongg.ext.inputMethodManager

class AutoHideInputMethodEditorLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (context == null || context !is Activity) {
                return super.dispatchTouchEvent(ev)
            }
            val activity = context as Activity
            val focusView = activity.currentFocus
            if (shouldHideInputMethod(focusView, ev)) {
                hideInputMethod(focusView)
            } else return super.dispatchTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun shouldHideInputMethod(focusView: View?, ev: MotionEvent): Boolean {
        if (focusView == null) return false
        val location = IntArray(2)
        focusView.getLocationOnScreen(location)
        val rect =
            Rect(
                location[0],
                location[1],
                location[0] + focusView.width,
                location[1] + focusView.height
            )
        return !rect.contains(ev.rawX.toInt(), ev.rawY.toInt())
    }

    private fun hideInputMethod(currentFocus: View?) {
        currentFocus?.let {
            context.inputMethodManager.hideSoftInputFromWindow(it.windowToken, HIDE_NOT_ALWAYS)
            it.clearFocus()
        }
    }
}