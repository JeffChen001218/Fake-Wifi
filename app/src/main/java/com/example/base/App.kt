package com.hook.fakewifi

import android.app.Application
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.children
import com.tencent.mmkv.MMKV
import hook.tool.INJECT_UI_TAG
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
        ShakeDetector(this) {
            val activity = TopActivityProvider.get() ?: return@ShakeDetector
            val decorView = activity.window.decorView as ViewGroup

            if (decorView.children.any { it.tag == INJECT_UI_TAG }) {
                return@ShakeDetector
            }

            val composeView = ComposeView(activity).apply parent@{
                val parent = this
                tag = INJECT_UI_TAG
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                )
                setContent {
                    injectUI(parent)
                }
            }

            decorView.addView(composeView)
        }.start()

        // GlobalScope.launch {
        //     if (isReleased()){
        //         // Google Play Store review passed
        //     }
        // }
    }
}