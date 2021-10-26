package com.chooongg.simple.modules

import android.os.Bundle
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.simple.databinding.ActivityStatusBinding

class StatusActivity : BoxBindingActivity<ActivityStatusBinding>() {

    override fun initConfig(savedInstanceState: Bundle?) {
        binding.statusLayout.setOnRetryListener {

        }
    }

    override fun initContent() {
    }
}