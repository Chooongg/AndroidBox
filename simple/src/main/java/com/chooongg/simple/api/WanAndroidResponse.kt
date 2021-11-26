package com.chooongg.simple.api

import com.chooongg.http.ext.ResponseData
import com.chooongg.http.throws.HttpException

data class WanAndroidResponse<T>(val data: T?, val errorCode: Int?, val errorMsg: String?) :
    ResponseData<T> {
    override suspend fun checkData(): T? {
        if (errorCode != 0) {
            throw HttpException(errorMsg ?: "未知错误")
        }
        return data
    }
}
