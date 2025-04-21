package com.hook.fakewifi

import android.app.Application
import android.content.Context
import com.tencent.mmkv.MMKV
import hook.tool.TopActivityProvider
import hook.tool.injectUI
import hook.ui.ShakeDetector

class App : Application() {
    companion object {
        lateinit var app: Context
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        MMKV.initialize(this)

        TopActivityProvider.init(this)
        if (this.applicationInfo.processName != this.packageName) {
            return
        }
        ShakeDetector(this) {
            val activity = TopActivityProvider.get()
            if (activity == null) {
                return@ShakeDetector
            }
            activity.runOnUiThread {
                injectUI(activity)
            }
        }.start()

        // GlobalScope.launch {
        //     if (isReleased()){
        //         // Google Play Store review passed
        //     }
        // }
    }
}