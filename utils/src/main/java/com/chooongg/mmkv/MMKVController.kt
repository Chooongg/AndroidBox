package com.chooongg.mmkv

import android.os.Parcelable
import com.chooongg.ext.getTClass
import com.tencent.mmkv.MMKV

open class MMKVController {

    private val mmkv: MMKV

    constructor() {
        mmkv = MMKV.defaultMMKV()
    }

    constructor(mmkv: MMKV) {
        this.mmkv = mmkv
    }

    constructor(mode: Int, cryptKey: String) {
        mmkv = MMKV.defaultMMKV(mode, cryptKey)
    }

    constructor(mmapID: String) {
        mmkv = MMKV.mmkvWithID(mmapID)
    }

    constructor(mmapID: String, mode: Int) {
        mmkv = MMKV.mmkvWithID(mmapID, mode)
    }

    constructor(mmapID: String, mode: Int, cryptKey: String?) {
        mmkv = MMKV.mmkvWithID(mmapID, mode, cryptKey)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> encode(mmkvKey: MMKVKey<T>, value: T?) {
        if (value == null) mmkv.removeValueForKey(mmkvKey.key)
        else when (mmkvKey.javaClass.getTClass()) {
            Int::class.javaObjectType -> mmkv.encode(mmkvKey.key, value as Int)
            Long::class.javaObjectType -> mmkv.encode(mmkvKey.key, value as Long)
            Float::class.javaObjectType -> mmkv.encode(mmkvKey.key, value as Float)
            Double::class.javaObjectType -> mmkv.encode(mmkvKey.key, value as Double)
            String::class.javaObjectType -> mmkv.encode(mmkvKey.key, value as String)
            Boolean::class.javaObjectType -> mmkv.encode(mmkvKey.key, value as Boolean)
            ByteArray::class.javaObjectType -> mmkv.encode(mmkvKey.key, value as ByteArray)
            Set::class.javaObjectType -> {
                val set = mmkvKey.javaClass.getTClass() as Set<*>
                if (set.javaClass.getTClass() == String::class.javaObjectType) {
                    mmkv.encode(mmkvKey.key, value as Set<String>)
                } else throw RuntimeException("MMKV can't support the data type(Set<${set.javaClass.getTClass()}>)")
            }
            else -> throw RuntimeException("MMKV can't support the data type(${mmkvKey.javaClass.getTClass().name})")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> decode(mmkvKey: MMKVKey<T>, default: T = mmkvKey.default): T {
        return if (mmkv.containsKey(mmkvKey.key)) {
            when (mmkvKey.javaClass.getTClass()) {
                Int::class.javaObjectType -> mmkv.decodeInt(mmkvKey.key) as T
                Long::class.javaObjectType -> mmkv.decodeLong(mmkvKey.key) as T
                Float::class.javaObjectType -> mmkv.decodeFloat(mmkvKey.key) as T
                Double::class.javaObjectType -> mmkv.decodeDouble(mmkvKey.key) as T
                String::class.javaObjectType -> mmkv.decodeString(mmkvKey.key) as T
                Boolean::class.javaObjectType -> mmkv.decodeBool(mmkvKey.key) as T
                ByteArray::class.javaObjectType -> mmkv.decodeBytes(mmkvKey.key) as T
                Set::class.javaObjectType -> {
                    val set = mmkvKey.javaClass.getTClass() as Set<*>
                    if (set.javaClass.getTClass() == String::class.javaObjectType) {
                        mmkv.decodeStringSet(mmkvKey.key) as T
                    } else throw RuntimeException("MMKV can't support the data type(Set<${set.javaClass.getTClass()}>)")
                }
                else -> throw RuntimeException("MMKV can't support the data type(${mmkvKey.javaClass.getTClass().name})")
            }
        } else default
    }

    fun <T : Parcelable?> encode(mmkvKey: MMKVParcelableKey<T>, value: T?) {
        if (value == null) mmkv.removeValueForKey(mmkvKey.key)
        else mmkv.encode(mmkvKey.key, value)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Parcelable?> decode(mmkvKey: MMKVParcelableKey<T>, default: T = mmkvKey.default): T {
        return if (mmkv.containsKey(mmkvKey.key)) mmkv.decodeParcelable(
            mmkvKey.key,
            mmkvKey.javaClass.getTClass() as Class<Parcelable>
        ) as T else default
    }

    fun remove(vararg mmkvKey: MMKVKey<*>) {
        if (mmkvKey.isNotEmpty()) mmkv.removeValuesForKeys(Array(mmkvKey.size) { mmkvKey[it].key })
    }
}