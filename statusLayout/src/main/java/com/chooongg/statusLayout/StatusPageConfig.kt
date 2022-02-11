package com.chooongg.statusLayout

import com.chooongg.statusLayout.status.AbstractStatus
import com.chooongg.statusLayout.status.SuccessStatus
import com.google.android.material.transition.MaterialSharedAxis
import kotlin.reflect.KClass

class StatusPageConfig {

    var enableAnimation = true

    @MaterialSharedAxis.Axis
    var animationAxis: Int = MaterialSharedAxis.Y

    var defaultState: KClass<out AbstractStatus> = SuccessStatus::class
}