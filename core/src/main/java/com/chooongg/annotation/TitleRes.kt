package com.chooongg.annotation

import androidx.annotation.StringRes

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TitleRes(@StringRes val resId: Int)
