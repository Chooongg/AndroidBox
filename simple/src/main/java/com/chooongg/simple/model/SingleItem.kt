package com.chooongg.simple.model

import android.view.View

data class SingleItem(
    val title: String,
    val block: (View) -> Unit
)