package com.chooongg

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.chooongg.manager.ApplicationManager
import com.chooongg.manager.BoxMMKV
import com.chooongg.utils.PermissionInterceptor
import com.facebook.stetho.Stetho
import com.google.android.material.color.DynamicColors
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV

class BoxInitializeProvider : ContentProvider() {

    companion object {
        init {
            // 黑夜模式支持
            AppCompatDelegate.setDefaultNightMode(BoxMMKV.DayNightMode.get())
            // 兼容矢量图片
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate(): Boolean {
        if (context is Application) {
            ApplicationManager.initialize(context as Application)
            MMKV.initialize(context as Application)
            Stetho.initializeWithDefaults(context)
            XXPermissions.setInterceptor(PermissionInterceptor())
            DynamicColors.applyToActivitiesIfAvailable(context as Application)
        } else {
            Log.e("AndroidBox", "BoxInitializeProvider initialize failure")
        }
        return false
    }

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ) = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ) = 0
}