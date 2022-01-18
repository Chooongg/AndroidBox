package com.chooongg.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import com.chooongg.annotation.TopAppBarDefaultNavigation
import com.chooongg.annotation.TopAppBarType
import com.chooongg.core.R
import com.chooongg.manager.WindowPreferencesManager
import com.chooongg.toolbar.BoxToolbar
import com.google.android.material.appbar.AppBarLayout

abstract class BoxTopAppBarActivity : BoxActivity() {

    protected open fun initActionBar(actionBar: ActionBar) = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowPreferencesManager(this).applyEdgeToEdgePreference(window)
        configRootView()
    }

    /**
     * 配置根视图
     */
    protected open fun configRootView() {
        when (getTopAppBarType4Annotation()) {
            TopAppBarType.TYPE_SMALL -> super.setContentView(R.layout.box_activity_root_small)
            TopAppBarType.TYPE_MEDIUM -> super.setContentView(R.layout.box_activity_root_medium)
            TopAppBarType.TYPE_LARGE -> super.setContentView(R.layout.box_activity_root_large)
        }
        if (supportActionBar == null) {
            setSupportActionBar(findViewById<BoxToolbar>(R.id.toolbar))
        }
        if (getTopAppBarDefaultNavigation4Annotation()) {
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_app_bar_back)
        }
        initActionBar(supportActionBar!!)
    }

    override fun setContentView(layoutResID: Int) {
        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        val view = layoutInflater.inflate(layoutResID, coordinatorLayout)
        view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            behavior = AppBarLayout.ScrollingViewBehavior()
        }
    }

    override fun setContentView(view: View?) {
        val coordinatorLayout = findViewById<CoordinatorLayout>(R.id.coordinator_layout)
        if (view == null) return
        coordinatorLayout.addView(view, CoordinatorLayout.LayoutParams(-1, -1).apply {
            behavior = AppBarLayout.ScrollingViewBehavior()
        })
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        super.setContentView(view, params)
    }
}