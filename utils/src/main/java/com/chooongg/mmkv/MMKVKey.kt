package com.chooongg.mmkv

import androidx.lifecycle.MediatorLiveData
import com.chooongg.ext.getTClass
import com.tencent.mmkv.MMKV

/**
 * MMKV 字典
 * 使用时请使用 object 类继承此类
 */
open class MMKVKey<T> : MediatorLiveData<T> {

    private val mmkv: MMKV?
    val key: String
    val default: T

    constructor(mmkv: MMKV?, key: String, default: T) : super() {
        this.mmkv = mmkv
        this.key = key
        this.default = default
    }

    constructor(key: String, default: T) : super() {
        this.mmkv = MMKV.defaultMMKV()
        this.key = key
        this.default = default
    }

    init {
        super.postValue(getValueFromController())
    }

    private fun getValueFromController() = decode()

//    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
//        value
//        super.observe(owner, observer)
//    }
//
//    override fun observeForever(observer: Observer<in T>) {
//        value
//        super.observeForever(observer)
//    }

    override fun getValue(): T {
        var currentValue = super.getValue() ?: default
        val mmkvValue = decode()
        if (currentValue != mmkvValue) {
            currentValue = mmkvValue
            postValue(currentValue)
        }
        return currentValue
    }

    override fun postValue(value: T?) {
        encode(value)
        super.postValue(value ?: default)
    }

    override fun setValue(value: T?) {
        encode(value)
        super.setValue(value ?: default)
    }

    fun remove() {
        if (mmkv == null) throw NullPointerException("MMKV is null")
        mmkv.removeValueForKey(key)
        postValue(null)
    }

    @Suppress("UNCHECKED_CAST")
    private fun encode(value: T?) {
        if (mmkv == null) return
        if (value == null) mmkv.removeValueForKey(key)
        else when (javaClass.getTClass()) {
            Int::class.javaObjectType -> mmkv.encode(key, value as Int)
            Long::class.javaObjectType -> mmkv.encode(key, value as Long)
            Float::class.javaObjectType -> mmkv.encode(key, value as Float)
            Double::class.javaObjectType -> mmkv.encode(key, value as Double)
            String::class.javaObjectType -> mmkv.encode(key, value as String)
            Boolean::class.javaObjectType -> mmkv.encode(key, value as Boolean)
            ByteArray::class.javaObjectType -> mmkv.encode(key, value as ByteArray)
            Set::class.javaObjectType -> {
                val set = javaClass.getTClass() as Set<*>
                if (set.javaClass.getTClass() == String::class.javaObjectType) {
                    mmkv.encode(key, value as Set<String>)
                } else throw RuntimeException("MMKV can't support the data type(Set<${set.javaClass.getTClass()}>)")
            }
            else -> throw RuntimeException("MMKV can't support the data type(${javaClass.getTClass().name})")
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun decode(): T {
        if (mmkv == null) return default
        return if (mmkv.containsKey(key)) {
            when (javaClass.getTClass()) {
                Int::class.javaObjectType -> mmkv.decodeInt(key) as T
                Long::class.javaObjectType -> mmkv.decodeLong(key) as T
                Float::class.javaObjectType -> mmkv.decodeFloat(key) as T
                Double::class.javaObjectType -> mmkv.decodeDouble(key) as T
                String::class.javaObjectType -> mmkv.decodeString(key) as T
                Boolean::class.javaObjectType -> mmkv.decodeBool(key) as T
                ByteArray::class.javaObjectType -> mmkv.decodeBytes(key) as T
                Set::class.javaObjectType -> {
                    val set = javaClass.getTClass() as Set<*>
                    if (set.javaClass.getTClass() == String::class.javaObjectType) {
                        mmkv.decodeStringSet(key) as T
                    } else throw RuntimeException("MMKV can't support the data type(Set<${set.javaClass.getTClass()}>)")
                }
                else -> throw RuntimeException("MMKV can't support the data type(${javaClass.getTClass().name})")
            }
        } else default
    }
}