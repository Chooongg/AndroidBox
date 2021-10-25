package com.chooongg.simple.modules.main.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.chooongg.http.throws.HttpException
import com.chooongg.simple.api.apiWanAndroid
import com.chooongg.simple.model.ArticleItem

class MainViewModel : ViewModel() {

    fun getArticlePager() = Pager(PagingConfig(10, initialLoadSize = 10)) {
        object : PagingSource<Int, ArticleItem>() {
            override fun getRefreshKey(state: PagingState<Int, ArticleItem>): Int? = null
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleItem> {
                return try {
                    Log.e("PagingAdapter", "load: key: ${params.key}")
                    val homeArticles =
                        apiWanAndroid().getHomeArticles(params.key ?: 0, params.loadSize)
                    if (homeArticles.data == null) throw HttpException(HttpException.Type.EMPTY)
                    if (homeArticles.data.datas == null) throw HttpException(HttpException.Type.EMPTY)
                    LoadResult.Page(
                        homeArticles.data.datas,
                        null,
                        (params.key ?: 0) + 1
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    LoadResult.Error(e)
                }
            }
        }
    }.flow.cachedIn(viewModelScope)
}