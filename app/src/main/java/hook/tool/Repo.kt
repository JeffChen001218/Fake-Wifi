package hook.tool

import android.content.Context


private const val PREF_NAME = "my_shared_prefs"

fun saveValue(key: String, value: String, context: Context) {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    prefs.edit().putString(key, value).apply()
}

fun getValue(key: String, context: Context): String {
    val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return prefs.getString(key, "") ?: ""
}
