package com.chooongg.ext

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment

fun Context.attrText(@AttrRes id: Int): CharSequence {
    val a = obtainStyledAttributes(intArrayOf(id))
    val text = a.getText(0)
    a.recycle()
    return text
}

fun Context.attrString(@AttrRes id: Int): String? {
    val a = obtainStyledAttributes(intArrayOf(id))
    val string = a.getString(0)
    a.recycle()
    return string
}

fun Context.attrBoolean(@AttrRes id: Int, defValue: Boolean): Boolean {
    val a = obtainStyledAttributes(intArrayOf(id))
    val boolean = a.getBoolean(0, defValue)
    a.recycle()
    return boolean
}

fun Context.attrInt(@AttrRes id: Int, defValue: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(id))
    val int = a.getInt(0, defValue)
    a.recycle()
    return int
}

fun Context.attrFloat(@AttrRes id: Int, defValue: Float): Float {
    val a = obtainStyledAttributes(intArrayOf(id))
    val float = a.getFloat(0, defValue)
    a.recycle()
    return float
}

fun Context.attrColor(@AttrRes id: Int, @ColorInt defValue: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(id))
    val color = a.getColor(0, defValue)
    a.recycle()
    return color
}

fun Context.attrColorStateList(@AttrRes id: Int): ColorStateList? {
    val a = obtainStyledAttributes(intArrayOf(id))
    val colorStateList = a.getColorStateList(0)
    a.recycle()
    return colorStateList
}

fun Context.attrInteger(@AttrRes id: Int, defValue: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(id))
    val integer = a.getInteger(0, defValue)
    a.recycle()
    return integer
}

fun Context.attrDimension(@AttrRes id: Int, defValue: Float): Float {
    val a = obtainStyledAttributes(intArrayOf(id))
    val dimension = a.getDimension(0, defValue)
    a.recycle()
    return dimension
}

fun Context.attrDimensionPixelOffset(@AttrRes id: Int, defValue: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(id))
    val dimension = a.getDimensionPixelOffset(0, defValue)
    a.recycle()
    return dimension
}


fun Context.attrDimensionPixelSize(@AttrRes id: Int, defValue: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(id))
    val dimension = a.getDimensionPixelSize(0, defValue)
    a.recycle()
    return dimension
}

fun Context.attrResourcesId(@AttrRes id: Int, defValue: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(id))
    val resourcesId = a.getResourceId(0, defValue)
    a.recycle()
    return resourcesId
}

fun Context.attrDrawable(@AttrRes id: Int): Drawable? {
    val a = obtainStyledAttributes(intArrayOf(id))
    val drawable = a.getDrawable(0)
    a.recycle()
    return drawable
}


fun Fragment.attrText(@AttrRes id: Int) = requireContext().attrText(id)
fun Fragment.attrString(@AttrRes id: Int) = requireContext().attrString(id)
fun Fragment.attrBoolean(@AttrRes id: Int, defValue: Boolean) =
    requireContext().attrBoolean(id, defValue)

fun Fragment.attrInt(@AttrRes id: Int, defValue: Int) = requireContext().attrInt(id, defValue)
fun Fragment.attrFloat(@AttrRes id: Int, defValue: Float) = requireContext().attrFloat(id, defValue)
fun Fragment.attrColor(@AttrRes id: Int, @ColorInt defValue: Int) =
    requireContext().attrColor(id, defValue)

fun Fragment.attrColorStateList(@AttrRes id: Int) = requireContext().attrColorStateList(id)
fun Fragment.attrInteger(@AttrRes id: Int, defValue: Int) =
    requireContext().attrInteger(id, defValue)

fun Fragment.attrDimension(@AttrRes id: Int, defValue: Float) =
    requireContext().attrDimension(id, defValue)

fun Fragment.attrDimensionPixelOffset(@AttrRes id: Int, defValue: Int) =
    requireContext().attrDimensionPixelOffset(id, defValue)

fun Fragment.attrDimensionPixelSize(@AttrRes id: Int, defValue: Int) =
    requireContext().attrDimensionPixelSize(id, defValue)

fun Fragment.attrResourcesId(@AttrRes id: Int, defValue: Int) =
    requireContext().attrResourcesId(id, defValue)

fun Fragment.attrDrawable(@AttrRes id: Int) = requireContext().attrDrawable(id)

fun View.attrText(@AttrRes id: Int) = context.attrText(id)
fun View.attrString(@AttrRes id: Int) = context.attrString(id)
fun View.attrBoolean(@AttrRes id: Int, defValue: Boolean) = context.attrBoolean(id, defValue)
fun View.attrInt(@AttrRes id: Int, defValue: Int) = context.attrInt(id, defValue)
fun View.attrFloat(@AttrRes id: Int, defValue: Float) = context.attrFloat(id, defValue)
fun View.attrColor(@AttrRes id: Int, @ColorInt defValue: Int) = context.attrColor(id, defValue)
fun View.attrColorStateList(@AttrRes id: Int) = context.attrColorStateList(id)
fun View.attrInteger(@AttrRes id: Int, defValue: Int) = context.attrInteger(id, defValue)
fun View.attrDimension(@AttrRes id: Int, defValue: Float) = context.attrDimension(id, defValue)
fun View.attrDimensionPixelOffset(@AttrRes id: Int, defValue: Int) =
    context.attrDimensionPixelOffset(id, defValue)

fun View.attrDimensionPixelSize(@AttrRes id: Int, defValue: Int) =
    context.attrDimensionPixelSize(id, defValue)

fun View.attrResourcesId(@AttrRes id: Int, defValue: Int) = context.attrResourcesId(id, defValue)
fun View.attrDrawable(@AttrRes id: Int) = context.attrDrawable(id)