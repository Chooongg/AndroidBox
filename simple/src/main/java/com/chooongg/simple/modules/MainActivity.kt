package com.chooongg.simple.modules

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.adapter.BindingAdapter
import com.chooongg.annotation.ActivityTransitions
import com.chooongg.annotation.TopAppBarDefaultNavigation
import com.chooongg.annotation.TopAppBarTextGravity
import com.chooongg.core.ext.divider
import com.chooongg.core.ext.startActivity
import com.chooongg.core.ext.startActivityTransitionPage
import com.chooongg.ext.dp2px
import com.chooongg.ext.getNightMode
import com.chooongg.ext.setNightMode
import com.chooongg.http.ext.requestBasic
import com.chooongg.simple.R
import com.chooongg.simple.api.apiSeniverse
import com.chooongg.simple.databinding.ActivityMainBinding
import com.chooongg.simple.databinding.ItemSingleBinding
import com.chooongg.simple.model.SingleItem
import kotlinx.coroutines.launch

@ActivityTransitions
@TopAppBarDefaultNavigation(false)
@TopAppBarTextGravity(Gravity.CENTER)
class MainActivity : BoxBindingActivity<ActivityMainBinding>() {

    private val adapter = Adapter()

    override fun initLiftOnScrollTargetId() = R.id.recycler_view

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
                    startActivityTransitionPage(AppBarActivity::class, it)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("状态布局") {
                    startActivity(StatusActivity::class)
                }, SingleItem("网络请求") {
                    startActivity(HttpActivity::class)
                }, SingleItem("分布式光伏功率预测") {
                    lifecycleScope.launch {
                        requestBasic<Any> {
                            api {
                                apiSeniverse().sensePower(
                                    "38.610084:115.047043",
                                    "38",
                                    "20",
                                    "0"
                                )
                            }
                            onResponse {
                                Log.d("weather", "onResponse:$it")
                            }
                            onFailed {
                                Log.e("weather", "onFailed:${it.message}")
                            }
                        }
                    }
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
        return super.onPrepareOptionsMenu(menu)
    }

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