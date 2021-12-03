package com.chooongg.simple.api

import com.chooongg.http.ResponseData
import com.chooongg.http.exception.HttpException

data class WanAndroidResponse<T>(val data: T?, val errorCode: Int?, val errorMsg: String?) :
    ResponseData<T> {

    override fun getCode() = errorCode?.toString()

    override fun getMessage() = errorMsg

    override suspend fun checkData(): T? {
        if (errorCode != 0) {
            throw HttpException(errorMsg ?: "未知错误")
        }
        return data
    }
}
