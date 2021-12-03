package com.chooongg.http

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.chooongg.ext.isAppDebug

@GlideModule(glideName = "BoxGlide")
class BoxGlideModule : AppGlideModule() {
    @SuppressLint("CheckResult")
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogRequestOrigins(isAppDebug())
        builder.setLogLevel(Log.INFO)
        builder.setDefaultRequestOptions {
            return@setDefaultRequestOptions RequestOptions().apply {
                placeholder(R.color.divider)
                fallback(R.color.divider)
                error(R.color.divider)
                useAnimationPool(true)
            }
        }
    }
}