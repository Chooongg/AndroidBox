package com.chooongg.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BoxBindingActivity<BINDING : ViewBinding> : BoxActivity() {

    @Suppress("UNCHECKED_CAST")
    protected val binding: BINDING by lazy {
        val type = javaClass.genericSuperclass
        val clazz = (type as ParameterizedType).actualTypeArguments[0] as Class<*>
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        return@lazy method.invoke(null, LayoutInflater.from(this)) as BINDING
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}