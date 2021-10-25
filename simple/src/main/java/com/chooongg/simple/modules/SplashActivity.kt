package com.chooongg.simple.modules

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.ext.withMain
import com.chooongg.simple.BuildConfig
import com.chooongg.simple.R
import com.chooongg.simple.databinding.ActivitySplashBinding
import com.chooongg.simple.modules.main.MainActivity
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : BoxBindingActivity<ActivitySplashBinding>() {

    override fun isShowActionBar() = false

    override fun initConfig(savedInstanceState: Bundle?) {
        window.setBackgroundDrawableResource(R.color.surface)
        binding.tvVersion.text = BuildConfig.VERSION_NAME
    }

    override fun initContent() {
        lifecycleScope.launchWhenResumed {
            delay(200)
            withMain {
                binding.motionLayout.transitionToEnd()
            }
            delay(1800)
            withMain {
                startActivity(
                    Intent(context, MainActivity::class.java),
                    ActivityOptionsCompat.makeCustomAnimation(
                        context,
                        R.anim.fade_in,
                        R.anim.fade_out
                    ).toBundle()
                )
                finish()
            }
        }
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
}