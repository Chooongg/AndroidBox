package com.chooongg.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.IntRange

object ColorUtils {

    /**
     * 是否是深色
     *
     * @param color 颜色
     */
    fun isColorDark(@ColorInt color: Int) =
        0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color) >= 127.5

    /**
     * 修改透明度
     */
    fun changeAlpha(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) alpha: Int) =
        color and 0x00ffffff or (alpha shl 24)

    /**
     * 修改透明度
     */
    fun setAlpha(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) alpha: Float) =
        color and 0x00ffffff or ((alpha * 255.0f + 0.5f).toInt() shl 24)

    /**
     * 修改红色值
     */
    fun changeRed(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) red: Int) =
        color and -0xff0001 or (red shl 16)

    /**
     * 修改红色值
     */
    fun changeRed(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) red: Float) =
        color and -0xff0001 or ((red * 255.0f + 0.5f).toInt() shl 16)

    /**
     * 修改绿色值
     */
    fun changeGreen(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) green: Int) =
        color and -0xff01 or (green shl 8)

    /**
     * 修改绿色值
     */
    fun changeGreen(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) green: Float) =
        color and -0xff01 or ((green * 255.0f + 0.5f).toInt() shl 8)

    /**
     * 修改蓝色值
     */
    fun changeBlue(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF) blue: Int) =
        color and -0x100 or blue

    /**
     * 修改蓝色值
     */
    fun changeBlue(@ColorInt color: Int, @FloatRange(from = 0.0, to = 1.0) blue: Float) =
        color and -0x100 or (blue * 255.0f + 0.5f).toInt()

    /**
     * 计算从 startColor 过渡到 endColor 过程中百分比的颜色值
     *
     * @param startColor 起始颜色 int 类型
     * @param endColor 结束颜色 int 类型
     * @param percentage 百分比
     * @return 返回 int 格式的 color
     */
    fun calculateColor(
        startColor: Int,
        endColor: Int,
        @FloatRange(from = 0.0, to = 1.0) percentage: Float
    ): Int {
        val startAlpha = Color.alpha(startColor)
        val startRed = Color.red(startColor)
        val startGreen = Color.green(startColor)
        val startBlue = Color.blue(startColor)
        val endAlpha = Color.alpha(endColor)
        val endRed = Color.red(endColor)
        val endGreen = Color.green(endColor)
        val endBlue = Color.blue(endColor)
        val currentAlpha = ((endAlpha - startAlpha) * percentage + startAlpha).toInt()
        val currentRed = ((endRed - startRed) * percentage + startRed).toInt()
        val currentGreen = ((endGreen - startGreen) * percentage + startGreen).toInt()
        val currentBlue = ((endBlue - startBlue) * percentage + startBlue).toInt()
        return Color.argb(currentAlpha, currentRed, currentGreen, currentBlue)
    }

    /**
     * 计算从 startColor 过渡到 endColor 过程中百分比的颜色值
     *
     * @param startColor 起始颜色 （格式 #FFFFFFFF）
     * @param endColor 结束颜色 （格式 #FFFFFFFF）
     * @param percentage 百分比
     * @return 返回 String 格式的 color（格式#FFFFFFFF）
     */
    fun calculateColor(
        startColor: String,
        endColor: String,
        @FloatRange(from = 0.0, to = 1.0) percentage: Float
    ): String {
        val startAlpha = startColor.substring(1, 3).toInt(16)
        val startRed = startColor.substring(3, 5).toInt(16)
        val startGreen = startColor.substring(5, 7).toInt(16)
        val startBlue = startColor.substring(7).toInt(16)
        val endAlpha = endColor.substring(1, 3).toInt(16)
        val endRed = endColor.substring(3, 5).toInt(16)
        val endGreen = endColor.substring(5, 7).toInt(16)
        val endBlue = endColor.substring(7).toInt(16)
        val currentAlpha = ((endAlpha - startAlpha) * percentage + startAlpha).toInt()
        val currentRed = ((endRed - startRed) * percentage + startRed).toInt()
        val currentGreen = ((endGreen - startGreen) * percentage + startGreen).toInt()
        val currentBlue = ((endBlue - startBlue) * percentage + startBlue).toInt()
        return ("#" + getHexString(currentAlpha) + getHexString(currentRed)
                + getHexString(currentGreen) + getHexString(currentBlue))
    }

    /**
     * 将10进制颜色值转换成16进制。
     *
     * @param value 十进制
     */
    private fun getHexString(value: Int): String? {
        var hexString = Integer.toHexString(value)
        if (hexString.length == 1) {
            hexString = "0$hexString"
        }
        return hexString
    }
}