package com.chooongg.eventBus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.chooongg.ext.APPLICATION

object ApplicationScopeViewModelProvider : ViewModelStoreOwner {

    internal const val TAG = "BoxEventBus"

    private val eventViewModelStore: ViewModelStore = ViewModelStore()

    override fun getViewModelStore() = eventViewModelStore

    private val applicationProvider: ViewModelProvider by lazy {
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(APPLICATION)
        )
    }

    fun <T : ViewModel> getApplicationScopeViewModel(modelClass: Class<T>): T =
        applicationProvider[modelClass]
}