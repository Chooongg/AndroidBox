package com.chooongg.activity

import android.content.Context
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionSet
import android.view.*
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.FitWindowsLinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import com.chooongg.annotation.Theme
import com.chooongg.autoHideIME.AutoHideInputMethodEditor
import com.chooongg.core.R
import com.chooongg.ext.contentView
import com.chooongg.ext.dp2px
import com.chooongg.ext.resourcesInteger
import com.chooongg.toolbar.BoxToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

abstract class BoxActivity : AppCompatActivity {

    constructor() : super() {
        contentId = ResourcesCompat.ID_NULL
    }

    constructor(@LayoutRes contentLayoutId: Int) : super() {
        contentId = contentLayoutId
    }

    //<editor-fold desc="子类实现方法">

    abstract fun initConfig(savedInstanceState: Bundle?)

    abstract fun initContent()

    //</editor-fold>

    protected inline val context: Context get() = this

    protected inline val activity: BoxActivity get() = this

    private val contentId: Int

    protected var windowToolBar: Toolbar? = null
        private set

    //<editor-fold desc="开放方法">

    /**
     * 初始化主题
     */
    @StyleRes
    protected fun initTheme(): Int = ResourcesCompat.ID_NULL

    /**
     * 是否显示标题栏
     */
    protected open fun isShowActionBar() = true

    /**
     * 是否自动显示导航图标
     */
    protected open fun isShowToolbarNavigationIcon() = true

    /**
     * 是否启用自动隐藏输入法
     */
    protected open fun isEnableAutoHideInputMethod() = true

    /**
     * 获取 ToolBar
     */
    protected open fun getToolbar(parentLayout: ViewGroup): Toolbar = LayoutInflater.from(context)
        .inflate(R.layout.box_activity_toolbar, parentLayout, false) as BoxToolbar

    /**
     * 初始化行动栏
     */
    open fun initActionBar(toolbar: ActionBar) = Unit

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
        getTheme4Box().let { if (it != ResourcesCompat.ID_NULL) setTheme(it) }
        super.onCreate(savedInstanceState)
        if (getActionBar4Box()) configActionBar()
        window.sharedElementEnterTransition = initSharedElementEnterTransition()
        window.sharedElementExitTransition = initSharedElementExitTransition()
        window.sharedElementReturnTransition = initSharedElementReturnTransition()
        window.sharedElementReenterTransition = initSharedElementReenterTransition()
        if (contentId != ResourcesCompat.ID_NULL) setContentView(contentId)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (isEnableAutoHideInputMethod()) {
            AutoHideInputMethodEditor.initialize(this)
        }
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
                super.setSupportActionBar(windowToolBar!!)
            }
            parentLayout.addView(windowToolBar!!, 0)
            if (supportActionBar != null) {
                if (isShowToolbarNavigationIcon()) {
                    supportActionBar!!.setHomeButtonEnabled(true)
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_app_bar_back)
                }
                initActionBar(supportActionBar!!)
            }
        } else {
            if (supportActionBar != null) {
                if (isShowToolbarNavigationIcon()) {
                    supportActionBar!!.setHomeButtonEnabled(true)
                    supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                    supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_app_bar_back)
                }
                initActionBar(supportActionBar!!)
            }
        }
    }

    @Deprecated(
        "BoxActivity has been processed automatically",
        ReplaceWith("", ""), DeprecationLevel.ERROR
    )
    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
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
        javaClass.getAnnotation(com.chooongg.annotation.ActionBar::class.java)?.isShow
            ?: isShowActionBar()

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

    //<editor-fold desc="共享元素">

    /**
     * 共享元素-进入动画
     */
    protected open fun initSharedElementEnterTransition(): Transition {
        return TransitionSet().apply {
            addTransition(Fade().apply {
                duration = resourcesInteger(android.R.integer.config_longAnimTime).toLong()
                excludeChildren(android.R.id.content, true)
            })
            addTransition(Slide(Gravity.END).apply {
                excludeTarget(android.R.id.statusBarBackground, true)
                excludeTarget(android.R.id.navigationBarBackground, true)
                excludeTarget(BottomNavigationView::class.java, true)
                excludeTarget(R.id.box_activity_toolbar, true)
            })
        }
    }

    /**
     * 共享元素-退出动画
     */
    protected open fun initSharedElementExitTransition(): Transition {
        return Fade().apply {
            duration = resourcesInteger(android.R.integer.config_shortAnimTime).toLong()
            startDelay = 200L
            excludeChildren(android.R.id.content, true)
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(R.id.box_activity_toolbar, true)
        }
    }

    /**
     * 共享元素-返回动画
     */
    protected open fun initSharedElementReturnTransition(): Transition {
        return TransitionSet().apply {
            addTransition(Fade().apply {
                excludeChildren(android.R.id.content, true)
            })
            addTransition(Slide(Gravity.END).apply {
                excludeTarget(android.R.id.statusBarBackground, true)
                excludeTarget(android.R.id.navigationBarBackground, true)
                excludeTarget(BottomNavigationView::class.java, true)
                excludeTarget(R.id.box_activity_toolbar, true)
            })
        }
    }

    /**
     * 共享元素-重新进入动画
     */
    protected open fun initSharedElementReenterTransition(): Transition {
        return Fade().apply {
            excludeChildren(android.R.id.content, true)
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
            excludeTarget(R.id.box_activity_toolbar, true)
        }
    }

    //</editor-fold>
}