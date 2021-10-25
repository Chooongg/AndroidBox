package com.chooongg.mmkv

import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 * MMKV 字典
 * 使用时请使用 object 类继承此类
 */
open class MMKVParcelableKey<T : Parcelable?> : MMKVKey<T> {
    constructor(mmkv: MMKV?, key: String, default: T) : super(mmkv, key, default)
    constructor(key: String, default: T) : super(key, default)
}