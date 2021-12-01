package com.chooongg.simple

import android.app.Application
import com.osama.firecrasher.FireCrasher

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FireCrasher.install(this)
    }
}