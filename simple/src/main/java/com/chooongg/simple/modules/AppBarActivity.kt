package com.chooongg.simple.modules

import android.os.Bundle
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.annotation.ActivityTransitions
import com.chooongg.simple.databinding.ActivityAppBarBinding

@ActivityTransitions
class AppBarActivity : BoxBindingActivity<ActivityAppBarBinding>() {
    override fun initConfig(savedInstanceState: Bundle?) = Unit
    override fun initContent() = Unit
}