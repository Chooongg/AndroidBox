package com.chooongg.autoHideIME

import android.app.Activity
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.chooongg.ext.contentView
import com.chooongg.ext.decorView

object AutoHideInputMethodEditor {

    fun initialize(activity: Activity) {
        val contentParent = activity.decorView
        initialize(contentParent)
    }

    fun initialize(fragment: Fragment) {
        val contentParent = fragment.requireView().parent as ViewGroup
        initialize(contentParent)
    }

    fun initialize(contentParent: ViewGroup) {
        val content = contentParent[0]
        contentParent.removeView(content)
        val params = content.layoutParams
        val layout = AutoHideInputMethodEditorLayout(content.context)
        layout.addView(content)
        contentParent.addView(layout, ViewGroup.LayoutParams(params.width, params.height))
    }
}