package com.chooongg.statusLayout

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView
import com.chooongg.ext.doOnClick
import com.chooongg.ext.inVisible
import com.chooongg.ext.visible
import com.chooongg.statusLayout.status.AbstractStatus
import com.chooongg.statusLayout.status.SuccessStatus
import kotlin.reflect.KClass

class StatusLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private val rootView: FrameLayout = FrameLayout(context)
    private val existingStatus = HashMap<KClass<out AbstractStatus>, AbstractStatus>()
    private var successStatus: SuccessStatus? = null

    private var currentStatus: KClass<out AbstractStatus>? = null

    private var onRetryEventListener: ((KClass<out AbstractStatus>) -> Unit)? = null

    private var onStatusChangeListener: ((KClass<out AbstractStatus>) -> Unit)? = null

    init {
        isFillViewport = true
        super.addView(rootView, -1, generateDefaultLayoutParams())
    }

    fun setOnRetryListener(block: (KClass<out AbstractStatus>) -> Unit) {
        onRetryEventListener = block
    }

    fun setOnStatusChangeListener(block: (KClass<out AbstractStatus>) -> Unit) {
        onStatusChangeListener = block
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (!isInEditMode) show(StatusPage.config.defaultState)
    }

    fun showSuccess() {
        show(SuccessStatus::class)
    }

    fun show(statusClass: KClass<out AbstractStatus>, message: CharSequence?) {
        show(statusClass)
        val status = existingStatus[statusClass] ?: return
        status.messageView(status.targetView)?.text = message
    }

    fun show(statusClass: KClass<out AbstractStatus>) {
        if (currentStatus == statusClass) return
        if (statusClass == SuccessStatus::class) {
            hideAllStatus()
            showSuccessStatus()
        } else {
            hideOtherStatus(statusClass)
            createAndShowStatus(statusClass)
            if (existingStatus[statusClass]?.showSuccess() == true) {
                showSuccessStatus()
            } else hideSuccessStatus()
        }
        onStatusChangeListener?.invoke(statusClass)
    }

    private fun showSuccessStatus() {
        if (successStatus != null) {
            successStatus!!.targetView.visible()
            if (StatusPage.config.enableAnimation) {
                successStatus!!.targetView.animate().cancel()
                successStatus!!.targetView.animate().alpha(1f)
            }
            currentStatus = SuccessStatus::class
        }
    }

    private fun hideSuccessStatus() {
        if (successStatus != null) {
            if (StatusPage.config.enableAnimation) {
                successStatus!!.targetView.animate().cancel()
                successStatus!!.targetView.animate().alpha(0f).withEndAction {
                    successStatus!!.targetView.inVisible()
                }
            } else {
                successStatus!!.targetView.inVisible()
            }
        }
    }

    private fun createAndShowStatus(statusClass: KClass<out AbstractStatus>) {
        if (existingStatus[statusClass] == null) {
            existingStatus[statusClass] =
                createStatus(statusClass).apply {
                    obtainTargetView(context)
                    if (enableAnimation() && StatusPage.config.enableAnimation) {
                        targetView.alpha = 0f
                    }
                    onAttach(context, targetView)
                    reloadEventView(targetView)?.doOnClick {
                        onRetryEventListener?.invoke(statusClass)
                    }
                }
        }
        val status = existingStatus[statusClass]!!
        rootView.addView(status.targetView, LayoutParams(-2, -2, Gravity.CENTER))
        if (status.enableAnimation() && StatusPage.config.enableAnimation) {
            status.targetView.animate().cancel()
            status.targetView.animate().alpha(1f)
        }
        currentStatus = statusClass
    }

    private fun hideAllStatus() {
        existingStatus.forEach {
            hideStatus(it.key)
        }
    }

    private fun hideOtherStatus(statusClass: KClass<out AbstractStatus>) {
        existingStatus.forEach {
            if (it.key != statusClass) {
                hideStatus(statusClass)
            }
        }
    }

    private fun hideStatus(statusClass: KClass<out AbstractStatus>) {
        val status = existingStatus[statusClass] ?: return
        if (status.enableAnimation() && StatusPage.config.enableAnimation) {
            status.targetView.animate().cancel()
            status.targetView.animate().alpha(0f).withEndAction {
                status.onDetach(context, status.targetView)
                rootView.removeView(status.targetView)
                existingStatus.remove(statusClass)
            }
        } else {
            status.onDetach(context, status.targetView)
            rootView.removeView(status.targetView)
            existingStatus.remove(statusClass)
        }
    }

    private fun createStatus(statusClass: KClass<out AbstractStatus>): AbstractStatus {
        return statusClass.java.newInstance()
    }

    override fun addView(child: View?) {
        addView(child, -1)
    }

    override fun addView(child: View?, index: Int) {
        addView(child, index, generateDefaultLayoutParams())
    }

    override fun addView(child: View?, width: Int, height: Int) {
        addView(child, -1, generateDefaultLayoutParams().apply {
            this.width = width
            this.height = height
        })
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        addView(child, -1, params)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (currentStatus == null) {
            existingStatus.forEach {
                if (child == it.value.targetView) {
                    rootView.addView(child, index, params)
                    currentStatus = it.key
                    return
                }
            }
        }
        if (successStatus == null) {
            if (child == null) return
            successStatus = SuccessStatus(child)
            if (rootView.childCount > 0) {
                successStatus!!.targetView.alpha = 0f
                successStatus!!.targetView.inVisible()
            }
            rootView.addView(successStatus!!.targetView, 0, params)
            if (currentStatus == null) currentStatus = SuccessStatus::class
        } else throw IllegalStateException("StatusLayout can host only one direct child")
    }
}