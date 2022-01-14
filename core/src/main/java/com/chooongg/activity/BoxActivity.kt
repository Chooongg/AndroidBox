package com.chooongg.activity

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.chooongg.annotation.ActivityTransitions
import com.chooongg.annotation.AutoHideInputMethod
import com.chooongg.annotation.ContentTransitions
import com.chooongg.ext.contentView
import com.chooongg.ext.dp2px
import com.chooongg.ext.hideInputMethodEditor
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

abstract class BoxActivity : AppCompatActivity() {

    //<editor-fold desc="子类实现方法">

    abstract fun initConfig(savedInstanceState: Bundle?)

    abstract fun initContent()

    //</editor-fold>

    protected inline val context: Context get() = this

    protected inline val activity: BoxActivity get() = this

    //<editor-fold desc="开放方法">

    /**
     * 配置 SnackBar 锚点 View
     */
    open fun snackBarAnchorView(): View? = null

    /**
     * 刷新
     */
    open fun refresh(any: Any? = null) = Unit

    //</editor-fold>

    //<editor-fold desc="框架方法">

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 是否启用页面过渡效果
        if (isEnableActivityTransitions4Annotation()) {
            window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        }
        // 是否启用内容过渡效果
        if (isEnableContentTransitions4Annotation()) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // 是否启用点击空白隐藏输入法
        if (isEnableAutoHideInputMethod4Annotation()) {
            contentView.setOnClickListener { hideInputMethodEditor() }
        }
        initConfig(savedInstanceState)
        initContent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //</editor-fold>

    //<editor-fold desc="注解获取">

    private fun isEnableAutoHideInputMethod4Annotation() =
        javaClass.getAnnotation(AutoHideInputMethod::class.java)?.isEnable ?: true

    private fun isEnableActivityTransitions4Annotation() =
        javaClass.getAnnotation(ActivityTransitions::class.java)?.isEnable ?: false

    private fun isEnableContentTransitions4Annotation() =
        javaClass.getAnnotation(ContentTransitions::class.java)?.isEnable ?: false

    //</editor-fold>

    //<editor-fold desc="SnackBar显示">

    open fun showSnackBar(
        message: CharSequence,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(contentView, message, duration).apply {
        val snackBarAnchorView = snackBarAnchorView()
        if (snackBarAnchorView != null) anchorView = snackBarAnchorView
        else view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = dp2px(20f)
        }
        block?.invoke(this)
        show()
    }

    open fun showSnackBar(
        message: CharSequence,
        anchor: View,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(contentView, message, duration).apply {
        anchorView = anchor
        block?.invoke(this)
        show()
    }

    open fun showSnackBar(
        @StringRes resId: Int,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(contentView, resId, duration).apply {
        val snackBarAnchorView = snackBarAnchorView()
        if (snackBarAnchorView != null) anchorView = snackBarAnchorView
        else view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            bottomMargin = dp2px(20f)
        }
        block?.invoke(this)
        show()
    }

    open fun showSnackBar(
        @StringRes resId: Int,
        anchor: View,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(contentView, resId, duration).apply {
        anchorView = anchor
        block?.invoke(this)
        show()
    }

    //</editor-fold>
}