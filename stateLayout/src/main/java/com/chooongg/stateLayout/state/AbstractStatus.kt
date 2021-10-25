package com.chooongg.stateLayout.state

import android.content.Context
import android.view.View
import android.widget.TextView
import java.io.Serializable

/**
 * 继承此类必须保留空参构造方法
 */
abstract class AbstractStatus : Serializable {

    internal lateinit var targetView: View

    constructor()

    internal constructor(view: View) {
        this.targetView = view
    }

    internal fun obtainTargetView(context: Context) {
        this.targetView = onBuildView(context)
    }

    protected abstract fun onBuildView(context: Context): View

    abstract fun onAttach(context: Context, view: View)

    abstract fun onDetach(context: Context, view: View)

    open fun reloadEventView(rootView: View): View? = null

    open fun messageView(rootView: View): TextView? = null

    open fun showSuccess(): Boolean = false

}