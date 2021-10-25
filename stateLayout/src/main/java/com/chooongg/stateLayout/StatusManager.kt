package com.chooongg.stateLayout

import com.chooongg.stateLayout.state.AbstractStatus
import com.chooongg.stateLayout.state.ProgressStatus
import kotlin.reflect.KClass

object StatusManager {
    var initialStatus: KClass<out AbstractStatus> = ProgressStatus::class
    var enableAnimated: Boolean = true
}