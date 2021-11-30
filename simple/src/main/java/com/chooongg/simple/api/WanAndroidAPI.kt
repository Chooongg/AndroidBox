package com.chooongg.simple.api

import com.chooongg.http.HttpFactory
import com.chooongg.http.annotation.BaseUrl
import com.chooongg.simple.model.ArticleEntity
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

fun apiWanAndroid() = HttpFactory.getAPI(WanAndroidAPI::class)

@BaseUrl("https://www.wanandroid.com/")
interface WanAndroidAPI {

    /**
     * 首页文章
     */
    @GET("article/list/{pageNum}/json")
    suspend fun getHomeArticles(
        @Path("pageNum") pageNum: Int,
        @Query("page_size") pageSize: Int = 10
    ): WanAndroidResponse<ArticleEntity>

    /**
     * 获取所有谷歌仓库包名
     */
    @GET("maven_pom/package/json")
    suspend fun allPackage(): WanAndroidResponse<MutableList<String>>
}