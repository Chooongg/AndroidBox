package com.chooongg.stateLayout.state

import android.content.Context
import android.view.View

class SuccessStatus(contentView: View) : AbstractStatus(contentView) {

    override fun onBuildView(context: Context) = View(context)

    override fun onAttach(context: Context, view: View) = Unit

    override fun onDetach(context: Context, view: View) = Unit
}