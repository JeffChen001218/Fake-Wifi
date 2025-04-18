package hook

import android.app.Application
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.children
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findClass
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import hook.tool.INJECT_UI_TAG
import hook.tool.TopActivityProvider
import hook.tool.getValue
import hook.tool.injectUI
import hook.ui.ShakeDetector

class XposedStart : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
//        if (loadPackageParam.packageName == "com.tencent.mm") {
//        }

        XposedBridge.log("hook started")

        // 获取需要的class (如果所hook的函数参数需要)
//         val wifiInfoCLz = XposedHelpers.findClass("android.net.wifi.WifiInfo", loadPackageParam.classLoader)

        // Helper获取Context（可能取不到，跟时机有关系）
//         val context = AndroidAppHelper.currentApplication()

        // 可以获取Context，但是使用ContentProvider似乎会报错：Given calling package android does not match caller's uid
//        val context = callMethod(
//            callStaticMethod(
//                findClass("android.app.ActivityThread", loadPackageParam.classLoader),
//                "currentActivityThread"
//            ), "getSystemContext"
//        ) as Context

        // hook获取Content
        XposedBridge.hookAllMethods(
            findClass("android.app.Instrumentation", loadPackageParam.classLoader),
            "newApplication", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val context = param.result as Context

                    TopActivityProvider.init(context.applicationContext as Application)
                    ShakeDetector(context) {
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

                    XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiInfo",
                        loadPackageParam.classLoader,
                        "getSSID",
                        // 函数参数class（可能0~多个，这里没有就不传）
                        object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                super.beforeHookedMethod(param)
                                // 读取参数
                                // val arg0 = param.args[0]
                            }

                            @Throws(Throwable::class)
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                super.afterHookedMethod(param)
                                // modify return value
                                param?.result = getValue("ssid", context)
                            }
                        })

                    XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiInfo",
                        loadPackageParam.classLoader,
                        "getBSSID",
                        object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                super.afterHookedMethod(param)
                                param?.result = getValue("bssid", context)
                            }
                        })

                    XposedHelpers.findAndHookMethod(
                        "android.net.wifi.WifiInfo",
                        loadPackageParam.classLoader,
                        "getMacAddress",
                        object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun afterHookedMethod(param: MethodHookParam?) {
                                super.afterHookedMethod(param)
                                param?.result = getValue("mac", context)
                            }
                        })

                }
            });
    }
}


