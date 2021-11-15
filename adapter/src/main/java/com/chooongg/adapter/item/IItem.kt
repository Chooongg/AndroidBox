package com.chooongg.adapter.item

import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

interface IItem<HOLDER : RecyclerView.ViewHolder> : IAdapterIdentifiable {

    var tag: Any?

    var isEnabled: Boolean

    var isSelectable: Boolean

    @get:IdRes
    val type: Int

    val factory: IItemVHFactory<HOLDER>?

    fun bindView(holder: HOLDER, payloads: List<Any>)

    fun unbindView(holder: HOLDER)

    fun attachToWindow(holder: HOLDER)

    fun detachFromWindow(holder: HOLDER)

    fun failedToRecycle(holder: HOLDER): Boolean

    fun equals(id: Int): Boolean
}