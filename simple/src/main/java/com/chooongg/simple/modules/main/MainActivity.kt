package com.chooongg.simple.modules.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.adapter.BindingAdapter
import com.chooongg.adapter.BindingHolder
import com.chooongg.core.ext.hideLoading
import com.chooongg.core.ext.showLoading
import com.chooongg.ext.getNightMode
import com.chooongg.ext.setNightMode
import com.chooongg.ext.withMain
import com.chooongg.simple.R
import com.chooongg.simple.databinding.ActivityMainBinding
import com.chooongg.simple.databinding.ItemMainBinding
import com.chooongg.simple.model.ArticleItem
import com.chooongg.simple.modules.main.viewModel.MainViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : BoxBindingActivity<ActivityMainBinding>() {

    private val model: MainViewModel by viewModels()

    private val adapter = Adapter()
    private val concatAdapter = ConcatAdapter(adapter)

    override fun isShowToolbarNavigationIcon() = false

    override fun initConfig(savedInstanceState: Bundle?) {
//        binding.recyclerView.adapter = concatAdapter
//        binding.statusLayout.showSuccess()
    }

    override fun initContent() {
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
        BadgeUtils.attachBadgeDrawable(create, windowToolBar!!, R.id.day_night_mode)
        return super.onPrepareOptionsMenu(menu)
    }

    private var isLong = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.light -> setNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            R.id.night -> setNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            R.id.system -> setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            R.id.loading -> {
                lifecycleScope.launch {
                    withMain {
                        if (isLong) {
                            showLoading("测试加载${System.currentTimeMillis()}")
                        } else {
                            showLoading("${System.currentTimeMillis()}")
                        }
                        isLong = !isLong
                    }
                    delay(5000)
                    withMain { hideLoading() }
                }
            }
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

    private class Adapter : BindingAdapter<String, ItemMainBinding>() {
        override fun convert(holder: BaseViewHolder, binding: ItemMainBinding, item: String) {

        }
    }

    private class Adapter2 : PagingDataAdapter<ArticleItem, BindingHolder<ItemMainBinding>>(object :
        DiffUtil.ItemCallback<ArticleItem>() {
        override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
            if (oldItem.id == null && newItem.id == null) return false
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
            return oldItem == newItem
        }
    }) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BindingHolder<ItemMainBinding> = BindingHolder(
            ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

        override fun onBindViewHolder(holder: BindingHolder<ItemMainBinding>, position: Int) {
            val item = getItem(position)
            holder.binding.tvTitle.text = item?.title
        }
    }
}