package com.chooongg.simple.modules

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.annotation.ActivityScreenOrientation
import com.chooongg.annotation.ActivityTransitions
import com.chooongg.simple.databinding.ActivityAppBarBinding

@ActivityTransitions
@ActivityScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
class AppBarActivity : BoxBindingActivity<ActivityAppBarBinding>() {

    override fun initConfig(savedInstanceState: Bundle?) = Unit
    override fun initContent() = Unit
}