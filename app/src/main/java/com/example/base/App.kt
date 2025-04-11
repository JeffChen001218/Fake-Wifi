package com.hook.fakewifi

import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV

class App : Application() {
    companion object {
        lateinit var app: Context
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        MMKV.initialize(this)
        // GlobalScope.launch {
        //     if (isReleased()){
        //         // Google Play Store review passed
        //     }
        // }
    }
}