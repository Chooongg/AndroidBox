package com.chooongg.http.ext

import com.chooongg.ext.debug
import com.chooongg.ext.withIO
import com.chooongg.ext.withMain
import com.chooongg.http.throws.HttpException

interface ResponseData<DATA> {
    suspend fun checkData(): DATA?
}

/**
 * 常规请求 DSL
 */
suspend fun <DATA> request(block: RetrofitCoroutinesDsl<ResponseData<DATA>, DATA>.() -> Unit) {
    val dsl = RetrofitCoroutinesDsl<ResponseData<DATA>, DATA>()
    block.invoke(dsl)
    dsl.executeRequest()
}

/**
 * 完整的请求 DSL
 */
suspend fun <RESPONSE : ResponseData<DATA>, DATA> requestIntact(block: RetrofitCoroutinesDsl<RESPONSE, DATA>.() -> Unit) {
    val dsl = RetrofitCoroutinesDsl<RESPONSE, DATA>()
    block.invoke(dsl)
    dsl.executeRequest()
}

/**
 * 基础的请求 DSL
 */
suspend fun <RESPONSE> requestBasic(block: RetrofitCoroutinesBaseDsl<RESPONSE>.() -> Unit) {
    val dsl = RetrofitCoroutinesBaseDsl<RESPONSE>()
    block.invoke(dsl)
    dsl.executeRequest()
}

/**
 * 处理返回信息的网络请求封装
 */
open class RetrofitCoroutinesDsl<RESPONSE : ResponseData<DATA>, DATA> :
    RetrofitCoroutinesBaseDsl<RESPONSE>() {

    protected var onSuccess: (suspend (DATA?) -> Unit)? = null

    fun onSuccess(block: suspend (data: DATA?) -> Unit) {
        this.onSuccess = block
    }

    override suspend fun processData(response: RESPONSE) {
        val data = response.checkData()
        if (onSuccess != null) {
            withMain { onSuccess!!.invoke(data) }
        }
    }
}

/**
 * 默认网络请求封装
 */
open class RetrofitCoroutinesBaseDsl<RESPONSE> {

    private var api: (suspend () -> RESPONSE)? = null

    private var onStart: (suspend () -> Unit)? = null

    private var onResponse: (suspend (RESPONSE) -> Unit)? = null

    private var onFailed: (suspend (HttpException) -> Unit)? = null

    private var onEnd: (suspend (Boolean) -> Unit)? = null

    fun api(block: suspend () -> RESPONSE) {
        this.api = block
    }

    fun onStart(block: suspend () -> Unit) {
        this.onStart = block
    }

    fun onResponse(block: suspend (RESPONSE) -> Unit) {
        this.onResponse = block
    }

    fun onFailed(block: suspend (error: HttpException) -> Unit) {
        this.onFailed = block
    }

    fun onEnd(block: suspend (isSuccess: Boolean) -> Unit) {
        this.onEnd = block
    }

    /**
     * 处理数据信息 不通过时抛出异常即可
     */
    protected open suspend fun processData(response: RESPONSE) = Unit

    /**
     * 执行网络请求
     */
    internal suspend fun executeRequest() {
        if (api == null) return
        if (onStart != null) {
            withMain { onStart!!.invoke() }
        }
        withIO {
            try {
                val response = api!!.invoke()
                if (onResponse != null) {
                    withMain { onResponse!!.invoke(response) }
                }
                processData(response)
                if (onEnd != null) {
                    withMain { onEnd!!.invoke(true) }
                }
            } catch (e: Exception) {
                if (onFailed != null) {
                    val httpException = HttpException(e)
                    debug { httpException.printStackTrace() }
                    withMain { onFailed!!.invoke(httpException) }
                }
                if (onEnd != null) {
                    withMain { onEnd!!.invoke(false) }
                }
            }
        }
    }
}