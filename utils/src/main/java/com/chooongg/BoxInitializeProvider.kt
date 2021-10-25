package com.chooongg

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.chooongg.ext.isAppDebug
import com.chooongg.manager.ApplicationManager
import com.chooongg.manager.BoxSimpleDataManager
import com.chooongg.utils.PermissionInterceptor
import com.facebook.stetho.Stetho
import com.hjq.permissions.XXPermissions
import com.tencent.mmkv.MMKV

class BoxInitializeProvider : ContentProvider() {

    companion object {
        init {
            // 兼容矢量图片
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate(): Boolean {
        if (context is Application) {
            ApplicationManager.initialize(context as Application)
            MMKV.initialize(context as Application)
            AppCompatDelegate.setDefaultNightMode(BoxSimpleDataManager.DayNightMode.value)
            Stetho.initializeWithDefaults(context)
            XXPermissions.setScopedStorage(true)
            XXPermissions.setDebugMode(isAppDebug())
            XXPermissions.setInterceptor(PermissionInterceptor())
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