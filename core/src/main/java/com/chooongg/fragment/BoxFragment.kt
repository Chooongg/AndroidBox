package com.chooongg.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.chooongg.activity.BoxActivity
import com.chooongg.annotation.*
import com.chooongg.core.R
import com.chooongg.ext.attrColor
import com.chooongg.ext.hideInputMethodEditor
import com.chooongg.ext.resourcesColor
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

@TopAppBarType(TopAppBarType.TYPE_NONE)
abstract class BoxFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    //<editor-fold desc="子类实现方法">

    protected open fun initActionBar(actionBar: Toolbar) = Unit

    abstract fun initConfig(savedInstanceState: Bundle?)

    open fun initContent() = Unit

    abstract fun initLazyContent()

    //</editor-fold>

    /**
     * 是否已经加载
     */
    var isLoaded = false; private set

    /**
     * 标题
     */
    var title: CharSequence? = null
        get() = field ?: javaClass.getAnnotation(Title::class.java)?.title

    inline val isShowed get() = !isHidden && isResumed

    //<editor-fold desc="开放方法">

    /**
     * 再次选择时
     */
    protected fun onReselected() = Unit

    /**
     * 刷新
     */
    protected fun onRefresh(any: Any? = null) = Unit

    /**
     * 是否可以回退
     * @return 是否内部消费了回退事件
     */
    open fun onBackPressed() = false

    /**
     * 配置 SnackBar 锚点 View
     */
    protected open fun snackBarAnchorView(): View? = null

    //</editor-fold>

    //<editor-fold desc="框架方法">

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = when (getTopAppBarType4Annotation()) {
            TopAppBarType.TYPE_SMALL -> inflater.inflate(
                R.layout.box_activity_root_small, container
            )
            TopAppBarType.TYPE_MEDIUM -> inflater.inflate(
                R.layout.box_activity_root_medium, container
            )
            TopAppBarType.TYPE_LARGE -> inflater.inflate(
                R.layout.box_activity_root_large, container
            )
            else -> null
        } ?: return onCreateContentView(inflater, container, savedInstanceState)

        val coordinatorLayout = view.findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        onCreateContentView(inflater, coordinatorLayout, savedInstanceState)

        val appBarLayout = view.findViewById<AppBarLayout>(R.id.appbar_layout)
        appBarLayout.liftOnScrollTargetViewId = getLiftOnScrollTargetId4Annotation() ?: View.NO_ID

        val collapsingLayout =
            view.findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar_layout)
                ?: return view
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
        initActionBar(view.findViewById(R.id.toolbar))
        return view
    }

    protected open fun onCreateContentView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initConfig(savedInstanceState)
        initContent()
        if (isEnableAutoHideInputMethod4Annotation()) {
            view.setOnClickListener {
                hideInputMethodEditor()
                it.requestFocus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden) {
            isLoaded = true
            initLazyContent()
        }
    }

    fun reselected() = onReselected()

    fun refresh(any: Any? = null) = onRefresh(any)

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!isLoaded && !isHidden) {
            isLoaded = true
            initLazyContent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
    }

    //</editor-fold>

    //<editor-fold desc="注解获取">

    private fun isEnableAutoHideInputMethod4Annotation() =
        javaClass.getAnnotation(AutoHideInputMethod::class.java)?.isEnable ?: true

    private fun getTopAppBarType4Annotation() =
        javaClass.getAnnotation(TopAppBarType::class.java)?.type ?: TopAppBarType.TYPE_NONE

    private fun getLiftOnScrollTargetId4Annotation() =
        javaClass.getAnnotation(LiftOnScrollTargetId::class.java)?.resId

    private fun isShowTopAppBarDefaultNavigation4Annotation() =
        javaClass.getAnnotation(TopAppBarDefaultNavigation::class.java)?.isShow ?: false

    private fun getTopAppBarTextGravity4Annotation() =
        javaClass.getAnnotation(TopAppBarTextGravity::class.java)

    //</editor-fold>

    //<editor-fold desc="SnackBar显示">

    fun showSnackBar(
        message: CharSequence,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(requireView(), message, duration).apply {
        val snackBarAnchorView = snackBarAnchorView()
        if (snackBarAnchorView != null) anchorView = snackBarAnchorView
        else if ((activity as? BoxActivity)?.snackBarAnchorView() != null) {
            anchorView = (activity as BoxActivity).snackBarAnchorView()
        }
        block?.invoke(this)
    }.apply { show() }

    fun showSnackBar(
        message: CharSequence,
        anchor: View,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(requireView(), message, duration).apply {
        anchorView = anchor
        block?.invoke(this)
    }.apply { show() }

    fun showSnackBar(
        @StringRes resId: Int,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(requireView(), resId, duration).apply {
        val snackBarAnchorView = snackBarAnchorView()
        if (snackBarAnchorView != null) anchorView = snackBarAnchorView
        else if ((activity as? BoxActivity)?.snackBarAnchorView() != null) {
            anchorView = (activity as BoxActivity).snackBarAnchorView()
        }
        block?.invoke(this)
    }.apply { show() }

    fun showSnackBar(
        @StringRes resId: Int,
        anchor: View,
        @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT,
        block: (Snackbar.() -> Unit)? = null,
    ) = Snackbar.make(requireView(), resId, duration).apply {
        anchorView = anchor
        block?.invoke(this)
    }.apply { show() }

    //</editor-fold>
}