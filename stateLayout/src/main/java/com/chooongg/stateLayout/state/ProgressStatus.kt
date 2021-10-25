package com.chooongg.stateLayout.state

import android.content.Context
import android.graphics.Color
import android.view.View
import com.chooongg.ext.attrColor
import com.chooongg.ext.dp2px
import com.chooongg.stateLayout.R
import com.google.android.material.progressindicator.CircularProgressIndicator

class ProgressStatus : AbstractStatus() {

    override fun onBuildView(context: Context): View {
        return CircularProgressIndicator(context).apply {
            isIndeterminate = true
            setIndicatorColor(context.attrColor(R.attr.colorPrimary, Color.GRAY))
            indicatorSize = dp2px(56f)
            trackCornerRadius = dp2px(16f)
        }
    }

    override fun onAttach(context: Context, view: View) {
    }

    override fun onDetach(context: Context, view: View) {
//        (view as CircularProgressIndicator).progressDrawable?.addSpringAnimationEndListener { _, canceled, _, _ ->
//
//        }
    }
}