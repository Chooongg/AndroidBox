package com.chooongg.annotation

import android.view.Gravity
import androidx.annotation.GravityInt
import java.lang.annotation.Inherited

@Inherited
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TopAppBarTextGravity(
    @GravityInt val expandedTitleGravity: Int,
    @GravityInt val collapsedTitleGravity: Int= Gravity.START
)
