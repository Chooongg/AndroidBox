package com.chooongg.ext

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.provider.Settings
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment


fun Context.getScreenWidth() = resources.displayMetrics.widthPixels

fun Context.getScreenHeight() = resources.displayMetrics.heightPixels

fun getScreenDisplayMetrics() = Resources.getSystem().displayMetrics

fun getScreenDensity() = Resources.getSystem().displayMetrics.density

fun getScreenDensityDpi() = Resources.getSystem().displayMetrics.densityDpi

fun getScreenXDpi() = Resources.getSystem().displayMetrics.xdpi

fun getScreenYDpi() = Resources.getSystem().displayMetrics.ydpi

fun getScreenScaledDensity() = Resources.getSystem().displayMetrics.scaledDensity

fun Context.isLandscape() =
    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.isPortrait() =
    resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

/**
 * 获取状态栏高度
 */
fun getStatusBarHeight(): Int {
    val resources = Resources.getSystem()
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/**
 * 显示系统栏
 */
fun Activity.showSystemBars() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.show(WindowInsetsCompat.Type.systemBars())
}

/**
 * 隐藏系统栏
 */
fun Activity.hideSystemBars() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.hide(WindowInsetsCompat.Type.systemBars())
}

/**
 * 显示状态栏
 */
fun Activity.showStatusBars() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.show(WindowInsetsCompat.Type.statusBars())
}

/**
 * 隐藏状态栏
 */
fun Activity.hideStatusBars() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.hide(WindowInsetsCompat.Type.statusBars())
}

/**
 * 显示导航栏
 */
fun Activity.showNavigationBars() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.show(WindowInsetsCompat.Type.navigationBars())
}

/**
 * 隐藏导航栏
 */
fun Activity.hideNavigationBars() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.hide(WindowInsetsCompat.Type.navigationBars())
}

/**
 * 显示输入法
 */
fun Fragment.showInputMethodEditor() = activity?.showInputMethodEditor()
fun Activity.showInputMethodEditor() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.show(WindowInsetsCompat.Type.ime())
}

/**
 * 隐藏输入法
 */
fun Fragment.hideInputMethodEditor() = activity?.hideInputMethodEditor()
fun Activity.hideInputMethodEditor() {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.hide(WindowInsetsCompat.Type.ime())
}

/**
 * 设置亮色状态栏
 */
fun Activity.setLightStatusBars(isLightMode: Boolean) {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.isAppearanceLightStatusBars = isLightMode
}

/**
 * 是否是亮色状态栏
 */
fun Activity.isLightStatusBars(): Boolean? {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return null
    return insetsController.isAppearanceLightStatusBars
}

/**
 * 设置亮色导航栏
 */
fun Activity.setLightNavigationBars(isLightMode: Boolean) {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return
    insetsController.isAppearanceLightNavigationBars = isLightMode
}

/**
 * 是否是亮色导航栏
 */
fun Activity.isLightNavigationBars(): Boolean? {
    val insetsController = WindowCompat.getInsetsController(window, decorView) ?: return null
    return insetsController.isAppearanceLightNavigationBars
}

/**
 * 屏幕是否亮屏
 */
fun isScreenBrightOn() = APPLICATION.powerManager.isInteractive

/**
 * 屏幕是否熄灭
 */
fun isScreenBrightOff() = !isScreenBrightOn()

/**
 * 屏幕是否锁屏
 */
fun isScreenLocked() = APPLICATION.keyguardManager.isKeyguardLocked

/**
 * 屏幕是否解锁
 */
fun isScreenUnlocked() = !isScreenLocked()

/**
 * 判断和设置是否保持屏幕常亮，只作用于当前窗口
 */
var Activity.isKeepScreenOn: Boolean
    get() {
        val flag = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        return (window.attributes.flags and flag) == flag
    }
    set(value) {
        when (value) {
            true -> window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            false -> window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

/**
 * 保持屏幕常亮，只作用于当前窗口
 */
fun Activity.setKeepScreenOn() {
    if (!isKeepScreenOn) {
        isKeepScreenOn = true
    }
}

/**
 * 取消保持屏幕常亮，只作用于当前窗口
 */
fun Activity.setNonKeepScreenOn() {
    if (isKeepScreenOn) {
        isKeepScreenOn = false
    }
}

/**
 * 获取自动锁屏时间
 * @throws Settings.SettingNotFoundException
 */
fun getScreenAutoLockTime() = try {
    Settings.System.getInt(
        APPLICATION.contentResolver,
        Settings.System.SCREEN_OFF_TIMEOUT
    )
} catch (e: Settings.SettingNotFoundException) {
    e.printStackTrace()
    -1
}

/**
 * 设置自动锁屏时间
 * @return 设置成功返回true
 */
@RequiresPermission(android.Manifest.permission.WRITE_SETTINGS)
fun setScreenAutoLockTime(time: Int): Boolean =
    Settings.System.putInt(
        APPLICATION.contentResolver,
        Settings.System.SCREEN_OFF_TIMEOUT,
        time
    )

/**
 * 设置永不自动锁屏，即自动锁屏时间为Int.MAX_VALUE
 * @return 设置成功返回true
 */
@RequiresPermission(android.Manifest.permission.WRITE_SETTINGS)
fun setScreenAutoLockNever(): Boolean = setScreenAutoLockTime(Int.MAX_VALUE)
