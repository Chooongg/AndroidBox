package com.chooongg.statusLayout.status

import android.view.View
import com.chooongg.ext.dp2px
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProgressStatus : AbstractStatus() {

    override fun onBuildView() = CircularProgressIndicator(context).apply {
        isIndeterminate = true
        indicatorSize = dp2px(56f)
        trackCornerRadius = dp2px(16f)
        hide()
    }

    override fun onAttach(view: View, message: CharSequence?) {
        (view as CircularProgressIndicator).show()
    }

    override fun onChangeMessage(view: View, message: CharSequence?) = Unit

    override fun getReloadEventView(view: View): View? = null

    override fun onDetach(view: View) {
        (view as CircularProgressIndicator).hide()
    }
}