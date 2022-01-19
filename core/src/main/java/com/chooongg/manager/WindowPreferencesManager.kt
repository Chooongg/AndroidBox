package com.chooongg.manager

import android.R
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.core.graphics.ColorUtils
import com.google.android.material.color.MaterialColors

class WindowPreferencesManager(private val context: Context) {

    private val isEdgeToEdgeEnabled: Boolean = true

    fun applyEdgeToEdgePreference(window: Window) {
        val edgeToEdgeEnabled: Boolean = isEdgeToEdgeEnabled
        val statusBarColor: Int = getStatusBarColor(isEdgeToEdgeEnabled)
        val navbarColor: Int = getNavBarColor(isEdgeToEdgeEnabled)
        val lightBackground: Boolean = MaterialColors.isColorLight(
            MaterialColors.getColor(
                context, R.attr.colorBackground, Color.BLACK
            )
        )
        val lightStatusBar: Boolean = MaterialColors.isColorLight(statusBarColor)
        val showDarkStatusBarIcons: Boolean =
            lightStatusBar || (statusBarColor == Color.TRANSPARENT && lightBackground)
        val lightNavbar: Boolean = MaterialColors.isColorLight(navbarColor)
        val showDarkNavbarIcons: Boolean =
            lightNavbar || (navbarColor == Color.TRANSPARENT && lightBackground)
        val decorView: View = window.decorView
        val currentStatusBar: Int =
            if (showDarkStatusBarIcons && VERSION.SDK_INT >= VERSION_CODES.M) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else 0
        val currentNavBar: Int =
            if (showDarkNavbarIcons && VERSION.SDK_INT >= VERSION_CODES.O) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0
        window.navigationBarColor = navbarColor
        window.statusBarColor = statusBarColor
        val systemUiVisibility: Int =
            ((if (edgeToEdgeEnabled) EDGE_TO_EDGE_FLAGS else View.SYSTEM_UI_FLAG_VISIBLE)
                    or currentStatusBar
                    or currentNavBar)
        decorView.systemUiVisibility = systemUiVisibility
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private fun getStatusBarColor(isEdgeToEdgeEnabled: Boolean): Int {
        if (isEdgeToEdgeEnabled && VERSION.SDK_INT < VERSION_CODES.M) {
            val opaqueStatusBarColor: Int =
                MaterialColors.getColor(context, R.attr.statusBarColor, Color.BLACK)
            return ColorUtils.setAlphaComponent(opaqueStatusBarColor, EDGE_TO_EDGE_BAR_ALPHA)
        }
        if (isEdgeToEdgeEnabled) {
            return Color.TRANSPARENT
        }
        return MaterialColors.getColor(context, R.attr.statusBarColor, Color.BLACK)
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    private fun getNavBarColor(isEdgeToEdgeEnabled: Boolean): Int {
        if (isEdgeToEdgeEnabled && VERSION.SDK_INT < VERSION_CODES.O_MR1) {
            val opaqueNavBarColor: Int =
                MaterialColors.getColor(context, R.attr.navigationBarColor, Color.BLACK)
            return ColorUtils.setAlphaComponent(opaqueNavBarColor, EDGE_TO_EDGE_BAR_ALPHA)
        }
        if (isEdgeToEdgeEnabled) {
            return Color.TRANSPARENT
        }
        return MaterialColors.getColor(context, R.attr.navigationBarColor, Color.BLACK)
    }

    companion object {
        private const val EDGE_TO_EDGE_BAR_ALPHA: Int = 128

        @RequiresApi(VERSION_CODES.LOLLIPOP)
        private val EDGE_TO_EDGE_FLAGS: Int =
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}