package com.chooongg.core.ext

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.chooongg.Box
import com.chooongg.core.R
import com.chooongg.ext.*
import com.google.android.material.progressindicator.CircularProgressIndicator

/**
 * 显示加载框
 */
fun Activity.showLoading(
    message: CharSequence?,
    preventClick: Boolean = Box.activityLoadingPreventClick
) {
    if (this is ComponentActivity) {
        lifecycleScope.launchMain { showLoadingForActivity(message, preventClick) }
    } else showLoadingForActivity(message, preventClick)
}

/**
 * 隐藏加载框
 */
fun Activity.hideLoading() {
    if (this is ComponentActivity) {
        lifecycleScope.launchMain { hideLoadingForActivity() }
    } else hideLoadingForActivity()
}

/**
 * 显示加载框
 */
fun Fragment.showLoading(
    message: CharSequence?,
    preventClick: Boolean = Box.activityLoadingPreventClick
) {
    activity?.showLoading(message, preventClick)
}

/**
 * 隐藏加载框
 */
fun Fragment.hideLoading() {
    activity?.hideLoading()
}

@SuppressLint("InflateParams")
private fun Activity.showLoadingForActivity(message: CharSequence?, preventClick: Boolean) {
    var loadingView = decorView.findViewById<View>(R.id.box_activity_loading)
    if (loadingView != null) {
        loadingView.animate().setListener(null).cancel()
    } else {
        loadingView =
            LayoutInflater.from(this).inflate(R.layout.box_activity_loading, null)
        decorView.addView(loadingView)
    }
    val progress = loadingView!!.findViewById<CircularProgressIndicator>(R.id.progress_view)
    val color = when (Box.activityLoadingProgressColor) {
        Box.COLOR_MODE_SECONDARY -> attrColor(R.attr.colorSecondary, Color.GRAY)
        Box.COLOR_MODE_ON_SURFACE -> attrColor(R.attr.colorOnSurface, Color.GRAY)
        else -> attrColor(R.attr.colorPrimary, Color.GRAY)
    }
    progress.setIndicatorColor(color)
    val textView = loadingView.findViewById<TextView>(R.id.tv_message)
    if (message.isNullOrEmpty()) {
        textView.gone()
    } else {
        textView.text = message
        textView.visible()
    }
    loadingView.isClickable = preventClick
    loadingView.animate().setInterpolator(AccelerateDecelerateInterpolator())
        .alpha(1f).scaleX(1f).scaleY(1f)
}

private fun Activity.hideLoadingForActivity() {
    decorView.findViewById<View>(R.id.box_activity_loading)?.apply {
        animate().setInterpolator(AccelerateDecelerateInterpolator())
            .alpha(0f).scaleX(0.9f).scaleY(0.9f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) = Unit
                override fun onAnimationCancel(animation: Animator?) = Unit
                override fun onAnimationRepeat(animation: Animator?) = Unit
                override fun onAnimationEnd(animation: Animator?) {
                    decorView.removeView(this@apply)
                }
            })
    }
}