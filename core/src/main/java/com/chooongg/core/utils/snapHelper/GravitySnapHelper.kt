package com.chooongg.core.utils.snapHelper

import androidx.recyclerview.widget.LinearSnapHelper

class GravitySnapHelper(
    gravity: Int,
    enableSnapLastItem: Boolean = false,
    snapListener: SnapListener? = null
) : LinearSnapHelper() {

    interface SnapListener {
        /**
         * 粘性移动到 position 时
         */
        fun onSnap(position: Int)
    }

}