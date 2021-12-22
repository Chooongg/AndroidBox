package com.chooongg.simple.api

import com.chooongg.http.HttpFactory
import com.chooongg.http.annotation.BaseUrl
import okhttp3.Interceptor
import retrofit2.http.GET
import retrofit2.http.Query

fun apiSeniverse() = HttpFactory.getAPI(SeniverseAPI::class) {
    interceptors.add(Interceptor {
        val seniverseUtils = SeniverseUtils()
        val request = it.request()
        val url = request.url
        for (i in 0 until url.querySize) {
            seniverseUtils.addParameter(url.queryParameterName(i), url.queryParameterValue(i))
        }
        seniverseUtils.removeParameter("sig")
        it.proceed(
            request.newBuilder().get()
                .url(seniverseUtils.getUrl("https://api.seniverse.com/v4", "SQh2ARu2R3pd-KpuZ"))
                .build()
        )
    })
}

@BaseUrl("https://api.seniverse.com/")
interface SeniverseAPI {

    @GET("v4")
    suspend fun sensePower(
        @Query("locations") location: String, // 经纬度 纬度:经度
        @Query("cap") cap: String, // 装机容量
        @Query("tilt") tilt: String, // 阵列倾角
        @Query("azimuth") azimuth: String, // 阵列朝向 0为正南
        @Query(/*"public_key"*/"key") publicKey: String = /*"P3TTBDfVYhxw8mSIN"*/"SQh2ARu2R3pd-KpuZ", // 公钥
        @Query("fields") fields: String = "pv_forecast_basic",
        @Query("ts") ts: Long = System.currentTimeMillis() / 1000,
        @Query("ttl") ttl: Long = 3000
    ): Any

}