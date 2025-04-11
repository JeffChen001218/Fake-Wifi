package hook.tool

import android.content.Context

object ReflectContextHelper {

    fun getContext(): Context? {
        return try {
            // 尝试从 ActivityThread.currentApplication()
            getContextFromActivityThread()
                ?: getContextFromAppGlobals()
                ?: getContextFromLoadedApk()
        } catch (e: Exception) {
            null
        }
    }

    private fun getContextFromActivityThread(): Context? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentApplicationMethod = activityThreadClass.getMethod("currentApplication")
            currentApplicationMethod.invoke(null) as? Context
        } catch (e: Exception) {
            null
        }
    }

    private fun getContextFromAppGlobals(): Context? {
        return try {
            val appGlobalsClass = Class.forName("android.app.AppGlobals")
            val getInitialApplication = appGlobalsClass.getMethod("getInitialApplication")
            getInitialApplication.invoke(null) as? Context
        } catch (e: Exception) {
            null
        }
    }

    private fun getContextFromLoadedApk(): Context? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread")
            val activityThread = currentActivityThreadMethod.invoke(null)

            val mInitialApplicationField = activityThreadClass.getDeclaredField("mInitialApplication")
            mInitialApplicationField.isAccessible = true
            mInitialApplicationField.get(activityThread) as? Context
        } catch (e: Exception) {
            null
        }
    }
}