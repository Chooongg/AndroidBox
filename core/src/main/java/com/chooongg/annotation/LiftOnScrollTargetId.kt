package com.chooongg.annotation

import androidx.annotation.IdRes

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LiftOnScrollTargetId(@IdRes val resId: Int)