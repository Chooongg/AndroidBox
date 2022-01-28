package com.chooongg.annotation

import java.lang.annotation.Inherited

@Inherited
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AutoHideInputMethod(val isEnable: Boolean = true)
