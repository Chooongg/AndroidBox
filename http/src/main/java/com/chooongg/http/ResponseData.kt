package com.chooongg.http

interface ResponseData<DATA> {
    fun getCode(): String?
    fun getMessage(): String?
    suspend fun checkData(): DATA?
}