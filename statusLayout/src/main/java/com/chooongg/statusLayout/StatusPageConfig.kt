package com.chooongg.statusLayout

import com.chooongg.statusLayout.status.AbstractStatus
import com.chooongg.statusLayout.status.SuccessStatus
import kotlin.reflect.KClass

class StatusPageConfig {

    var enableAnimation = true

    var defaultState: KClass<out AbstractStatus> = SuccessStatus::class
}