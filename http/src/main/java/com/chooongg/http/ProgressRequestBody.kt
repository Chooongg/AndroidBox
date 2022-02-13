package com.chooongg.http

import android.app.Notification
import okhttp3.RequestBody
import okio.*


class ProgressRequestBody(val requestBody: RequestBody, val block: () -> Unit) : RequestBody() {

    override fun contentType() = requestBody.contentType()

    override fun contentLength() = requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        //写入
        requestBody.writeTo(sink(sink).buffer());
        //刷新
        bufferedSink.flush();
    }

    private fun sink(sink: BufferedSink): Sink {
        return object : ForwardingSink(sink) {
            var bytesWritten = 0L
            var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (contentLength == 0L) {
                    contentLength = contentLength()
                }
                bytesWritten += byteCount
                //回调
                msg.obj = ProgressModel(bytesWritten, contentLength, bytesWritten == contentLength)
            }
        }
    }
}