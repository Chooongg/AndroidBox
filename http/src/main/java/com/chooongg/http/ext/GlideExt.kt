package com.chooongg.http.ext

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.chooongg.http.BoxGlide
import java.io.File

fun ImageView.load(bitmap: Bitmap?) = let { BoxGlide.with(this).load(bitmap) }

fun ImageView.load(drawable: Drawable?) = let { BoxGlide.with(this).load(drawable) }

fun ImageView.load(url: String?) = let { BoxGlide.with(this).load(url) }

fun ImageView.load(uri: Uri?) = let { BoxGlide.with(this).load(uri) }

fun ImageView.load(file: File?) = let { BoxGlide.with(this).load(file) }

fun ImageView.load(@DrawableRes resId: Int?) = let { BoxGlide.with(this).load(resId) }

fun ImageView.load(byte: ByteArray?) = let { BoxGlide.with(this).load(byte) }


fun ImageView.loadGif(bitmap: Bitmap?) = let { BoxGlide.with(this).asGif().load(bitmap) }

fun ImageView.loadGif(drawable: Drawable?) = let { BoxGlide.with(this).asGif().load(drawable) }

fun ImageView.loadGif(url: String?) = let { BoxGlide.with(this).asGif().load(url) }

fun ImageView.loadGif(uri: Uri?) = let { BoxGlide.with(this).asGif().load(uri) }

fun ImageView.loadGif(file: File?) = let { BoxGlide.with(this).asGif().load(file) }

fun ImageView.loadGif(@DrawableRes resId: Int?) = let { BoxGlide.with(this).asGif().load(resId) }

fun ImageView.loadGif(byte: ByteArray?) = let { BoxGlide.with(this).asGif().load(byte) }