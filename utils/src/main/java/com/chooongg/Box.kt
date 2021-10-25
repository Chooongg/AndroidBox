package com.chooongg

import androidx.annotation.IntDef

/**
 * 全局参数配置类
 */
object Box {

    const val COLOR_MODE_PRIMARY = 0
    const val COLOR_MODE_SECONDARY = 1
    const val COLOR_MODE_ON_SURFACE = 2

    @IntDef(COLOR_MODE_PRIMARY, COLOR_MODE_SECONDARY,COLOR_MODE_ON_SURFACE)
    annotation class ColorMode

    // 页面加载框是否阻止点击事件
    var activityLoadingPreventClick = true

    @ColorMode
    var activityLoadingProgressColor = COLOR_MODE_PRIMARY



}