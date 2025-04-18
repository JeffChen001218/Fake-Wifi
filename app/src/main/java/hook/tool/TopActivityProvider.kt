package hook.tool

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle

@SuppressLint("StaticFieldLeak")
object TopActivityProvider : Application.ActivityLifecycleCallbacks {
    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(TopActivityProvider)
    }

    private var currentActivity: Activity? = null

    fun get(): Activity? = currentActivity

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }

    // 其他生命周期方法可以留空
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}