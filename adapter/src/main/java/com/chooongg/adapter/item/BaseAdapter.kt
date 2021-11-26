package com.chooongg.adapter.item

import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<DATA> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData: MutableList<DATA> = ArrayList()

    public val data: List<DATA> get() = mData

}