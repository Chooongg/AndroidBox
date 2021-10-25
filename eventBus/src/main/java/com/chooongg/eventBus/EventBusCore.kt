package com.chooongg.eventBus

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chooongg.ext.debug
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class EventBusCore : ViewModel() {

    /**
     * 正常事件
     */
    private val eventFlows = HashMap<String, MutableSharedFlow<Any>>()

    /**
     * 粘性事件
     */
    private val stickyEventFlows = HashMap<String, MutableSharedFlow<Any>>()

    private fun getEventFlow(eventName: String, isSticky: Boolean) =
        if (isSticky) {
            stickyEventFlows[eventName]
        } else {
            eventFlows[eventName]
        } ?: MutableSharedFlow<Any>(
            if (isSticky) 1 else 0,
            Int.MAX_VALUE
        ).also {
            if (isSticky) {
                stickyEventFlows[eventName] = it
            } else {
                eventFlows[eventName] = it
            }
        }

    fun <T : Any> observeEvent(
        lifecycleOwner: LifecycleOwner,
        eventName: String,
        minState: Lifecycle.State,
        dispatcher: CoroutineDispatcher,
        isSticky: Boolean,
        onReceived: (T) -> Unit
    ) {
        debug {
            Log.w(ApplicationScopeViewModelProvider.TAG, "observe Event: $eventName")
        }
        lifecycleOwner.launchWhenStateAtLeast(minState) {
            getEventFlow(eventName, isSticky).collect { value ->
                this.launch(dispatcher) {
                    invokeReceived(value, onReceived)
                }
            }
        }
    }

    suspend fun <T : Any> observeWithoutLifecycle(
        eventName: String,
        isSticky: Boolean,
        onReceived: (T) -> Unit
    ) {
        getEventFlow(eventName, isSticky).collect { value ->
            invokeReceived(value, onReceived)
        }
    }

    fun postEvent(eventName: String, value: Any, timeMillis: Long) {
        debug {
            Log.w(ApplicationScopeViewModelProvider.TAG, "post Event: $eventName")
        }
        listOfNotNull(
            getEventFlow(eventName, false),
            getEventFlow(eventName, true)
        ).forEach { flow ->
            viewModelScope.launch {
                delay(timeMillis)
                flow.emit(value)
            }
        }
    }

    fun removeStickEvent(eventName: String) {
        stickyEventFlows.remove(eventName)
    }

    fun clearStickEvent(eventName: String) {
        stickyEventFlows[eventName]?.resetReplayCache()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> invokeReceived(value: Any, onReceived: (T) -> Unit) {
        try {
            onReceived.invoke(value as T)
        } catch (e: ClassCastException) {
            debug {
                Log.w(
                    ApplicationScopeViewModelProvider.TAG,
                    "class cast error on message received: $value"
                )
            }
        } catch (e: Exception) {
            debug {
                Log.w(
                    ApplicationScopeViewModelProvider.TAG,
                    "error on message received: $value"
                )
            }
        }
    }

    fun getEventObserverCount(eventName: String): Int {
        val stickyObserverCount = stickyEventFlows[eventName]?.subscriptionCount?.value ?: 0
        val normalObserverCount = eventFlows[eventName]?.subscriptionCount?.value ?: 0
        return stickyObserverCount + normalObserverCount
    }
}