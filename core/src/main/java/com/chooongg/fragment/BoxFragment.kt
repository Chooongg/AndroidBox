package com.chooongg.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.chooongg.activity.BoxActivity
import com.chooongg.annotation.Title
import com.chooongg.ext.hideInputMethodEditor
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

abstract class BoxFragment : Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    //<editor-fold desc="子类实现方法">

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
     * 是否启用自动隐藏输入法
     */
    protected open fun isEnableAutoHideInputMethod() = true

    /**
     * 再次选择时
     */
    fun onReselected() = Unit

    /**
     * 刷新
     */
    fun refresh(any: Any? = null) = Unit

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initConfig(savedInstanceState)
        initContent()
        if (isEnableAutoHideInputMethod()) {
            view.setOnClickListener { hideInputMethodEditor() }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isLoaded && !isHidden) {
            isLoaded = true
            initLazyContent()
        }
    }

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