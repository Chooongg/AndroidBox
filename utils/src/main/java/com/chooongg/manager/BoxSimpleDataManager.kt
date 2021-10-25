package com.chooongg.manager

import androidx.appcompat.app.AppCompatDelegate
import com.chooongg.mmkv.MMKVKey
import com.tencent.mmkv.MMKV

internal object BoxSimpleDataManager {

    private val mmkv = MMKV.mmkvWithID("AndroidBox")

    internal object DayNightMode :
        MMKVKey<Int>(mmkv, "day_night_mode", AppCompatDelegate.MODE_NIGHT_NO)
}