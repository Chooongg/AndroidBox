package com.chooongg.http.ext

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.RequestBuilder
import com.chooongg.http.BoxGlide
import java.io.File

fun ImageView.load(bitmap: Bitmap?, block: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    val builder = BoxGlide.with(this).load(bitmap)
    block?.invoke(builder)
    builder.into(this)
}

fun ImageView.load(drawable: Drawable?, block: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    val builder = BoxGlide.with(this).load(drawable)
    block?.invoke(builder)
    builder.into(this)
}

fun ImageView.load(url: String?, block: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    val builder = BoxGlide.with(this).load(url)
    block?.invoke(builder)
    builder.into(this)
}

fun ImageView.load(uri: Uri?, block: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    val builder = BoxGlide.with(this).load(uri)
    block?.invoke(builder)
    builder.into(this)
}

fun ImageView.load(file: File?, block: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    val builder = BoxGlide.with(this).load(file)
    block?.invoke(builder)
    builder.into(this)
}

fun ImageView.load(@DrawableRes resId: Int?, block: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    val builder = BoxGlide.with(this).load(resId)
    block?.invoke(builder)
    builder.into(this)
}

fun ImageView.load(byte: ByteArray?, block: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    val builder = BoxGlide.with(this).load(byte)
    block?.invoke(builder)
    builder.into(this)
}

