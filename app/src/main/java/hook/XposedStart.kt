package hook

import android.app.AndroidAppHelper
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import hook.tool.getValue

class XposedStart : IXposedHookLoadPackage {
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
//        if (loadPackageParam.packageName == "com.tencent.mm") {
//        }

        XposedBridge.log("hook started")

        // 获取需要的class (如果所hook的函数参数需要)
        // val wifiInfoCLz = XposedHelpers.findClass("android.net.wifi.WifiInfo", loadPackageParam.classLoader)

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
                    param?.result = getValue(
                        "ssid",
                        AndroidAppHelper.currentApplication()
                    ).also {
                        XposedBridge.log("ssid:${it}")
                    }
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
                    param?.result = getValue(
                        "bssid",
                        AndroidAppHelper.currentApplication()
                    ).also {
                        XposedBridge.log("bssid:${it}")
                    }
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
                    param?.result = getValue(
                        "mac",
                        AndroidAppHelper.currentApplication()
                    ).also {
                        XposedBridge.log("mac:${it}")
                    }
                }
            })
    }
}


