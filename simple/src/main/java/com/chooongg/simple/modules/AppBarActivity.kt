package com.chooongg.simple.modules

import android.os.Bundle
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.annotation.ActivityTransitions
import com.chooongg.ext.contentView
import com.chooongg.simple.databinding.ActivityAppBarBinding

@ActivityTransitions
class AppBarActivity : BoxBindingActivity<ActivityAppBarBinding>() {
    override fun initTransitions() {
        contentView.transitionName = "content_layout"
    }
    override fun initConfig(savedInstanceState: Bundle?) = Unit
    override fun initContent() = Unit
}