package com.chooongg.simple.modules

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chooongg.activity.BoxBindingActivity
import com.chooongg.adapter.BindingHolder
import com.chooongg.http.exception.HttpException
import com.chooongg.simple.api.apiWanAndroid
import com.chooongg.simple.databinding.ActivityHttpBinding
import com.chooongg.simple.databinding.ItemSingleBinding
import com.chooongg.simple.model.ArticleItem

class HttpActivity : BoxBindingActivity<ActivityHttpBinding>() {

    private val adapter = Adapter()

    override fun initConfig(savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter
    }

    override fun initContent() {
    }

    private class WanAndroidPagingSource : PagingSource<Int, ArticleItem>() {
        override fun getRefreshKey(state: PagingState<Int, ArticleItem>) = 0
        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleItem> {
            return try {
                val response = apiWanAndroid().getHomeArticles(params.key ?: 0)
                val checkData = response.checkData()
                if (checkData == null || checkData.datas.isNullOrEmpty()) {
                    throw HttpException(HttpException.Type.EMPTY)
                }
                LoadResult.Page(checkData.datas, null, (params.key ?: 0) + 1)
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }

    private class Adapter :
        PagingDataAdapter<ArticleItem, BindingHolder<ItemSingleBinding>>(object :
            DiffUtil.ItemCallback<ArticleItem>() {
            override fun areItemsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ArticleItem, newItem: ArticleItem): Boolean {
                return oldItem.id == newItem.id
            }
        }) {

        private lateinit var context: Context


        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            context = recyclerView.context
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BindingHolder<ItemSingleBinding> {
            return BindingHolder(
                ItemSingleBinding.inflate(LayoutInflater.from(context), parent, false)
            )
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemSingleBinding>, position: Int) {
            holder.binding.tvTitle.text = getItem(position)?.title
        }
    }
}