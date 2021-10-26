package com.chooongg.simple.model

data class SingleItem(
    val title: String,
    val block: () -> Unit
)