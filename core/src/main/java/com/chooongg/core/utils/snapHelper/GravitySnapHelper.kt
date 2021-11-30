package com.chooongg.core.utils.snapHelper

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

class GravitySnapHelper(
    private val gravity: Int,
    private val snapLastItem: Boolean = false,
    private var snapListener: SnapListener? = null
) : LinearSnapHelper() {

    companion object {
        const val FLING_DISTANCE_DISABLE = -1
        const val FLING_SIZE_FRACTION_DISABLE = -1f
    }

    private var isRtl = false
    private var nextSnapPosition: Int = 0
    private var isScrolling = false
    private var snapToPadding = false
    private var scrollMsPerInch = 100f
    private var maxFlingDistance = FLING_DISTANCE_DISABLE
    private var maxFlingSizeFraction = FLING_SIZE_FRACTION_DISABLE

    private var verticalHelper:OrientationHelper
    private var horizontalHelper:OrientationHelper
    private var recyclerView: RecyclerView
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            this@GravitySnapHelper
        }
    }

    interface SnapListener {
        /**
         * 粘性移动到 position 时
         */
        fun onSnap(position: Int)
    }

}