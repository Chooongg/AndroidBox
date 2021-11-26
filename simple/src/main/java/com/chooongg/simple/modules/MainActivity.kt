package com.chooongg.simple.modules

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.adapter.BindingAdapter
import com.chooongg.core.ext.divider
import com.chooongg.core.ext.hideLoading
import com.chooongg.core.ext.showLoading
import com.chooongg.core.ext.startActivity
import com.chooongg.ext.dp2px
import com.chooongg.ext.getNightMode
import com.chooongg.ext.setNightMode
import com.chooongg.ext.withMain
import com.chooongg.simple.R
import com.chooongg.simple.databinding.ActivityMainBinding
import com.chooongg.simple.databinding.ItemSingleBinding
import com.chooongg.simple.model.SingleItem
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BoxBindingActivity<ActivityMainBinding>() {


    private val adapter = Adapter()

    override fun isShowToolbarNavigationIcon() = false

    override fun initConfig(savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.divider {
            asSpace().size(dp2px(16f))
            showFirstDivider().showLastDivider().showSideDividers()
        }
        adapter.setOnItemClickListener { _, view, position ->
            adapter.data[position].block.invoke(view)
        }
    }

    override fun initContent() {
        adapter.setNewInstance(
            arrayListOf(
                SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("AppBar") {
                    startActivity(AppBarActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.choose_night, menu)
        return true
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        when (getNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> menu?.findItem(R.id.light)?.isChecked = true
            AppCompatDelegate.MODE_NIGHT_YES -> menu?.findItem(R.id.night)?.isChecked = true
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> menu?.findItem(R.id.system)?.isChecked =
                true
        }
        val create = BadgeDrawable.create(context)
        create.number = 1
        windowToolBar?.let {
            BadgeUtils.attachBadgeDrawable(create, it, R.id.day_night_mode)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private var isLong = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.light -> setNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            R.id.night -> setNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            R.id.system -> setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private var firstTime: Long = 0
    override fun onBackPressed() {
        val secondTime = System.currentTimeMillis()
        if (secondTime - firstTime > 2000) {
            showSnackBar("再按一次退出程序")
            firstTime = secondTime
        } else {
            super.onBackPressed()
        }
    }

    private class Adapter : BindingAdapter<SingleItem, ItemSingleBinding>() {
        override fun convert(holder: BaseViewHolder, binding: ItemSingleBinding, item: SingleItem) {
            binding.tvTitle.text = item.title
        }
    }
}