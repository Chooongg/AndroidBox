package com.chooongg.http.file

interface DownloadProgressCallback {
    fun update(total: Long, progress: Float)
}