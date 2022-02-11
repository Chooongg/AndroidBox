package com.chooongg.simple.modules

import android.os.Bundle
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.adapter.BindingAdapter
import com.chooongg.annotation.ActivityTransitions
import com.chooongg.core.ext.divider
import com.chooongg.ext.dp2px
import com.chooongg.simple.databinding.ActivityStatusBinding
import com.chooongg.simple.databinding.ItemSingleBinding
import com.chooongg.statusLayout.status.ProgressStatus

@ActivityTransitions
class StatusActivity : BoxBindingActivity<ActivityStatusBinding>() {

    private val adapter = Adapter()

    override fun initConfig(savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter
        binding.statusLayout.setOnRetryListener {
            binding.statusLayout.showSuccess()
        }
        binding.recyclerView.divider {
            asSpace().size(dp2px(16f))
            showFirstDivider().showLastDivider().showSideDividers()
        }
        adapter.setOnItemClickListener { _, _, _ ->
            binding.statusLayout.show(ProgressStatus::class)
        }
    }

    override fun initContent() {
        adapter.setNewInstance(
            arrayListOf(
                "测试",
                "测试",
                "测试",
                "测试",
                "测试",
                "测试",
                "测试",
                "测试",
                "测试",
                "测试",
                "测试"
            )
        )
    }

    private class Adapter : BindingAdapter<String, ItemSingleBinding>() {
        override fun convert(holder: BaseViewHolder, binding: ItemSingleBinding, item: String) {
            binding.tvTitle.text = item
        }
    }
}