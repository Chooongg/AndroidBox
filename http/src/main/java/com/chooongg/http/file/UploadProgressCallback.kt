package com.chooongg.http.file

interface UploadProgressCallback {
    fun update(total: Long, progress: Float)
}