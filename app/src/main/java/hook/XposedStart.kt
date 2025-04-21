package hook

import android.app.Application
import android.content.Context
import android.view.ViewGroup
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
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
        XposedHelpers.findAndHookMethod(
            "android.content.ContextWrapper",
            loadPackageParam.classLoader,
            "attachBaseContext",
            Context::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val context = param.args[0] as Context
                    XposedBridge.log(
                        "context info => " +
                                "\n\tpackageName: ${context.packageName}" +
                                "\n\tapplicationContext: ${context.applicationContext}"
                    )
                    val application = context.applicationContext as? Application ?: run {
                        XposedBridge.log("applicationContext is null. return")
                        return
                    }
                    TopActivityProvider.init(application)
                    if (context.applicationInfo.processName != context.packageName) {
                        XposedBridge.log("not main process. return")
                        return
                    }
                    ShakeDetector(context) {
                        XposedBridge.log("shake detected")
                        val activity = TopActivityProvider.get()
                        if (activity == null) {
                            XposedBridge.log("no activity found. return")
                            return@ShakeDetector
                        }
                        activity.runOnUiThread {
                            XposedBridge.log("==== start inject UI ====")
                            injectUI(activity)
                            XposedBridge.log("==== complete inject UI ====")
                        }
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


