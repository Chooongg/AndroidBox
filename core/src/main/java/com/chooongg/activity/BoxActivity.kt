package com.chooongg.activity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updateLayoutParams
import com.chooongg.annotation.ActionBar
import com.chooongg.annotation.Theme
import com.chooongg.autoHideIME.AutoHideInputMethodEditor
import com.chooongg.core.R
import com.chooongg.ext.contentView
import com.chooongg.ext.dp2px
import com.chooongg.ext.loadActivityLabel
import com.chooongg.toolbar.BoxToolbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

abstract class BoxActivity : AppCompatActivity {

    internal constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    //<editor-fold desc="子类实现方法">

    abstract fun initConfig(savedInstanceState: Bundle?)

    abstract fun initContent()

    //</editor-fold>

    protected inline val context: Context get() = this

    protected inline val activity: BoxActivity get() = this

    protected var windowToolBar: Toolbar? = null
        private set

    //<editor-fold desc="开放方法">

    /**
     * 初始化主题
     */
    @StyleRes
    protected fun initTheme(): Int = 0

    /**
     * 是否显示标题栏
     */
    protected open fun isShowActionBar() = true

    /**
     * 是否自动显示导航图标
     */
    protected open fun isShowToolbarNavigationIcon() = true

    /**
     * 获取 ToolBar
     */
    protected open fun getToolbar(parentLayout: ViewGroup): Toolbar = LayoutInflater.from(context)
        .inflate(R.layout.box_activity_toolbar, parentLayout, false) as BoxToolbar

    /**
     * 初始化行动栏
     */
    open fun initActionBar(toolbar: Toolbar) = Unit

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
        getTheme4Box().let { if (it != 0) setTheme(it) }
        super.onCreate(savedInstanceState)
        if (getActionBar4Box()) configActionBar()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        AutoHideInputMethodEditor.initialize(this)
        initConfig(savedInstanceState)
        initContent()
    }

    private fun configActionBar() {
        val parentLayout = contentView.parent as ViewGroup
        // 在 Theme 中使用 NoActionBar 才生效
        if (parentLayout is FitWindowsLinearLayout) {
            windowToolBar = getToolbar(parentLayout).apply {
                id = R.id.box_activity_toolbar
            }
            if (windowToolBar !is BoxToolbar) {
                setSupportActionBar(windowToolBar!!)
                initActionBar(windowToolBar!!)
            }
            parentLayout.addView(windowToolBar!!, 0)
            title = loadActivityLabel()
        }
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        if (isShowToolbarNavigationIcon()) {
            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_app_bar_back)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getTheme4Box() = javaClass.getAnnotation(Theme::class.java)?.resId ?: initTheme()

    private fun getActionBar4Box() =
        javaClass.getAnnotation(ActionBar::class.java)?.isShow ?: isShowActionBar()

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