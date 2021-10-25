package com.chooongg.simple.api

data class WanAndroidResponse<T>(val data: T?, val errorCode: Int?, val errorMsg: String?)
