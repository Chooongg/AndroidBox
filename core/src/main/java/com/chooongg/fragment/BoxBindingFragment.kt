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
        val type = javaClass.genericSuperclass
        if (type is ParameterizedType) {
            try {
                val clazz = type.actualTypeArguments[0] as Class<*>
                val method = clazz.getMethod("inflate",
                    LayoutInflater::class.java,
                    ViewGroup::class.javaObjectType,
                    Boolean::class.java)
                return@lazy method.invoke(null,
                    LayoutInflater.from(requireContext()),
                    container,
                    false) as BINDING
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        throw RuntimeException("BINDING not find.")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        this.container = container
        return binding.root
    }
}