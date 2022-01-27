package com.chooongg.activity

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.updateLayoutParams
import com.chooongg.annotation.*
import com.chooongg.core.R
import com.chooongg.ext.contentView
import com.chooongg.ext.dp2px
import com.chooongg.ext.hideInputMethodEditor
import com.chooongg.manager.WindowPreferencesManager
import com.chooongg.toolbar.BoxToolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

abstract class BoxActivity : AppCompatActivity() {

    //<editor-fold desc="子类实现方法">

    protected open fun initActionBar(actionBar: ActionBar) = Unit

    @IdRes
    protected open fun initLiftOnScrollTargetId(): Int = ResourcesCompat.ID_NULL

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
        WindowPreferencesManager(this).applyEdgeToEdgePreference(window)
        // 是否启用页面过渡效果
        if (isEnableActivityTransitions4Annotation()) {
            window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        }
        // 是否启用内容过渡效果
        if (isEnableContentTransitions4Annotation()) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        configRootView()
    }

    private fun configRootView() {
        when (getTopAppBarType4Annotation()) {
            TopAppBarType.TYPE_SMALL -> super.setContentView(R.layout.box_activity_root_small)
            TopAppBarType.TYPE_MEDIUM -> {
                super.setContentView(R.layout.box_activity_root_medium)
            }
            TopAppBarType.TYPE_LARGE -> {
                super.setContentView(R.layout.box_activity_root_large)
            }
            else -> return
        }
        if (supportActionBar == null) {
            setSupportActionBar(findViewById<BoxToolbar>(R.id.toolbar))
        }
        if (initLiftOnScrollTargetId() != ResourcesCompat.ID_NULL) {
            val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout)
            appBarLayout.liftOnScrollTargetViewId = initLiftOnScrollTargetId()
        }
        initActionBar(supportActionBar!!)
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

    override fun setContentView(layoutResID: Int) {
        when (getTopAppBarType4Annotation()) {
            TopAppBarType.TYPE_SMALL, TopAppBarType.TYPE_MEDIUM, TopAppBarType.TYPE_LARGE -> {
                val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
                val view = layoutInflater.inflate(layoutResID, coordinatorLayout)
                view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                }
            }
            else -> super.setContentView(layoutResID)
        }
    }

    override fun setContentView(view: View?) {
        when (getTopAppBarType4Annotation()) {
            TopAppBarType.TYPE_SMALL, TopAppBarType.TYPE_MEDIUM, TopAppBarType.TYPE_LARGE -> {
                val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
                if (view == null) return
                coordinatorLayout.addView(view, CoordinatorLayout.LayoutParams(-1, -1).apply {
                    behavior = AppBarLayout.ScrollingViewBehavior()
                })
            }
            else -> super.setContentView(view)
        }
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        when (getTopAppBarType4Annotation()) {
            TopAppBarType.TYPE_SMALL, TopAppBarType.TYPE_MEDIUM, TopAppBarType.TYPE_LARGE -> {
                val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
                if (view == null) return
                if (params is CoordinatorLayout.LayoutParams && params.behavior == null) {
                    params.behavior = AppBarLayout.ScrollingViewBehavior()
                }
                coordinatorLayout.addView(view, params)
            }
            else -> super.setContentView(view, params)
        }
    }

    override fun setSupportActionBar(toolbar: Toolbar?) {
        super.setSupportActionBar(toolbar)
        if (isShowTopAppBarDefaultNavigation4Annotation()) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_app_bar_back)
        }
    }

    //</editor-fold>

    //<editor-fold desc="注解获取">

    private fun isEnableAutoHideInputMethod4Annotation() =
        javaClass.getAnnotation(AutoHideInputMethod::class.java)?.isEnable ?: true

    private fun isEnableActivityTransitions4Annotation() =
        javaClass.getAnnotation(ActivityTransitions::class.java)?.isEnable ?: false

    private fun isEnableContentTransitions4Annotation() =
        javaClass.getAnnotation(ContentTransitions::class.java)?.isEnable ?: false

    private fun isShowTopAppBarDefaultNavigation4Annotation() =
        javaClass.getAnnotation(TopAppBarDefaultNavigation::class.java)?.isShow ?: true

    private fun getTopAppBarType4Annotation() =
        javaClass.getAnnotation(TopAppBarType::class.java)?.type ?: TopAppBarType.TYPE_SMALL

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