package com.chooongg.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BoxBindingFragment<BINDING : ViewBinding> : BoxFragment() {

    private var container: ViewGroup? = null

    @Suppress("UNCHECKED_CAST")
    protected val binding: BINDING by lazy {
        val clazz =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<*>
        val method = clazz.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
        return@lazy method.invoke(
            null,
            LayoutInflater.from(requireContext()),
            container,
            false
        ) as BINDING
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.container = container
        return binding.root
    }
}