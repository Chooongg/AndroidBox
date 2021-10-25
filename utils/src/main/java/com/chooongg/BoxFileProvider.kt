package com.chooongg

import android.app.Activity
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

class BoxFileProvider : FileProvider() {
    companion object {
        fun getUriForFile(context: Activity, file: File): Uri {
            return if (Build.VERSION.SDK_INT >= 24) {
                getUriForFile(context, "${context.application.packageName}.box.provider", file)
            } else Uri.fromFile(file)
        }
    }
}