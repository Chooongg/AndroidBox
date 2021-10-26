package com.chooongg.statusLayout.status

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

/**
 * 继承此类必须保留空参构造方法
 */
abstract class AbstractBindingStatus<BINDING : ViewBinding> : AbstractStatus() {

    private lateinit var binding: BINDING

    @Suppress("UNCHECKED_CAST")
    override fun onBuildView(context: Context): View {
        val type = javaClass.genericSuperclass
        val clazz = (type as ParameterizedType).actualTypeArguments[0] as Class<*>
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        binding = method.invoke(null, LayoutInflater.from(context)) as BINDING
        return binding.root
    }

    abstract fun onAttach(context: Context, binding: BINDING)

    abstract fun onDetach(context: Context, binding: BINDING)

    open fun reloadEventView(binding: BINDING): View? = null

    override fun onAttach(context: Context, view: View) {
        onAttach(context, binding)
    }

    override fun onDetach(context: Context, view: View) {
        onDetach(context, binding)
    }

    override fun reloadEventView(rootView: View): View? {
        return reloadEventView(binding)
    }
}