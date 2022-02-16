package com.chooongg.activity

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.chooongg.annotation.*
import com.chooongg.core.R
import com.chooongg.ext.*
import com.chooongg.manager.WindowPreferencesManager
import com.chooongg.toolbar.BoxToolbar
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

abstract class BoxActivity : AppCompatActivity() {

    //<editor-fold desc="子类实现方法">

    protected open fun initActionBar(actionBar: ActionBar) = Unit

    protected open fun initTransitions() = Unit

    @IdRes
    protected open fun getLiftOnScrollTargetId(): Int = View.NO_ID

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
        // 是否启用页面过渡效果
        if (isEnableActivityTransitions4Annotation()) {
            window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            val contentView = contentView
            contentView.transitionName = "box_transitions_content"
            setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
            setExitSharedElementCallback(object :
                MaterialContainerTransformSharedElementCallback() {
                override fun onCaptureSharedElementSnapshot(
                    sharedElement: View,
                    viewToGlobalMatrix: Matrix,
                    screenBounds: RectF
                ): Parcelable? {
                    sharedElement.alpha = 1f
                    return super.onCaptureSharedElementSnapshot(
                        sharedElement,
                        viewToGlobalMatrix,
                        screenBounds
                    )
                }
            })
            window.sharedElementsUseOverlay = false
            window.sharedElementEnterTransition = buildContainerTransform(contentView, true)
            window.sharedElementReturnTransition = buildContainerTransform(contentView, false)

            initTransitions()
        }

        super.onCreate(savedInstanceState)
        WindowPreferencesManager(this).applyEdgeToEdgePreference(window)

        configRootView()
    }

    private fun buildContainerTransform(contentView: View, entering: Boolean) =
        MaterialContainerTransform(this, entering).apply {
            setAllContainerColors(MaterialColors.getColor(contentView, R.attr.colorSurface))
            addTarget(contentView.id)
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            interpolator = FastOutSlowInInterpolator()
            isDrawDebugEnabled = false
        }

    private fun configRootView() {
        when (getTopAppBarType4Annotation()) {
            TopAppBarType.TYPE_SMALL -> super.setContentView(R.layout.box_activity_root_small)
            TopAppBarType.TYPE_MEDIUM -> super.setContentView(R.layout.box_activity_root_medium)
            TopAppBarType.TYPE_LARGE -> super.setContentView(R.layout.box_activity_root_large)
            else -> return
        }
        if (supportActionBar == null) {
            setSupportActionBar(findViewById<BoxToolbar>(R.id.toolbar))
        }
        val appBarLayout = findViewById<AppBarLayout>(R.id.appbar_layout)
        if (getLiftOnScrollTargetId() != View.NO_ID) {
            appBarLayout.liftOnScrollTargetViewId = getLiftOnScrollTargetId()
        }
        val collapsingLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar_layout)
            ?: return
        when (val collapsingBackground = appBarLayout.background) {
            is MaterialShapeDrawable -> {
                val defaultColor = collapsingBackground.fillColor?.defaultColor
                val attrColor = attrColor(R.attr.colorSurface, resourcesColor(R.color.surface))
                if (defaultColor != null && defaultColor != attrColor) {
                    val onPrimary =
                        attrColor(R.attr.colorOnPrimary, resourcesColor(R.color.onPrimary))
                    collapsingLayout.setExpandedTitleColor(onPrimary)
                    collapsingLayout.setCollapsedTitleTextColor(onPrimary)
                    collapsingLayout.setContentScrimColor(
                        attrColor(
                            R.attr.colorPrimary,
                            resourcesColor(R.color.primary)
                        )
                    )
                }
            }
        }
        val topAppBarTextGravity = getTopAppBarTextGravity4Annotation()
        if (topAppBarTextGravity != null) {
            if (topAppBarTextGravity.collapsedTitleGravity != Gravity.NO_GRAVITY) {
                collapsingLayout.collapsedTitleGravity = topAppBarTextGravity.collapsedTitleGravity
            }
            if (topAppBarTextGravity.expandedTitleGravity != Gravity.NO_GRAVITY) {
                collapsingLayout.expandedTitleGravity = topAppBarTextGravity.expandedTitleGravity
            }
        }
        initActionBar(supportActionBar!!)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // 是否启用点击空白隐藏输入法
        contentView.isFocusable = true
        contentView.isFocusableInTouchMode = true
        if (isEnableAutoHideInputMethod4Annotation()) {
            contentView.setOnClickListener {
                hideInputMethodEditor()
                it.clearFocus()
            }
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

    protected fun getCoordinatorLayout(): CoordinatorLayout? =
        findViewById(R.id.coordinator_layout)

    protected fun getAppBarLayout(): AppBarLayout? =
        findViewById(R.id.appbar_layout)

    protected fun getCollapsingToolbarLayout(): CollapsingToolbarLayout? =
        findViewById(R.id.collapsing_toolbar_layout)

    //</editor-fold>

    //<editor-fold desc="注解获取">

    private fun isEnableAutoHideInputMethod4Annotation() =
        javaClass.getAnnotation(AutoHideInputMethod::class.java)?.isEnable ?: true

    private fun isEnableActivityTransitions4Annotation() =
        javaClass.getAnnotation(ActivityTransitions::class.java)?.isEnable ?: false

    private fun getTopAppBarType4Annotation() =
        javaClass.getAnnotation(TopAppBarType::class.java)?.type ?: TopAppBarType.TYPE_SMALL

    private fun isShowTopAppBarDefaultNavigation4Annotation() =
        javaClass.getAnnotation(TopAppBarDefaultNavigation::class.java)?.isShow ?: true

    private fun getTopAppBarTextGravity4Annotation() =
        javaClass.getAnnotation(TopAppBarTextGravity::class.java)

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