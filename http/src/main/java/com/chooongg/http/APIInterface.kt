package com.chooongg.http

import com.chooongg.BoxException
import com.chooongg.http.annotation.BaseUrl

interface APIInterface {
    companion object {
        fun getBaseUrl() = this::class.java.getAnnotation(BaseUrl::class.java)?.value
            ?: throw BoxException("please add @BaseUrl on interface class")
    }
}