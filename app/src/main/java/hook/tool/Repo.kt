package hook.tool

import android.content.ContentValues
import android.content.Context
import hook.tool.ReflectContextHelper.getContext
import hook.tool.SharedContentContract.CONTENT_URI
import hook.tool.SharedContentContract.TextEntry.COLUMN_KEY
import hook.tool.SharedContentContract.TextEntry.COLUMN_VALUE
import hook.tool.SharedContentContract.TextEntry.allColumns

fun saveValue(key: String, value: String, context: Context? = getContext()) {
    context ?: return
    val values = ContentValues().apply {
        put(COLUMN_VALUE, value)
    }

    val rowsUpdated = context.contentResolver.update(
        CONTENT_URI,
        values,
        "${COLUMN_KEY} = ?",
        arrayOf(key)
    )
    if (rowsUpdated <= 0) {
        // 更新条目为0，即表中还没有数据，需要插入
        val values = ContentValues().apply {
            put(COLUMN_KEY, key)
            put(COLUMN_VALUE, value)
        }

        val uri = context.contentResolver.insert(CONTENT_URI, values)
        val success = uri != null
    }
}

fun getValue(key: String, context: Context? = getContext()): String {
    context ?: return ""
    val cursor = context.contentResolver.query(
        CONTENT_URI,
        allColumns,
        null, null, null
    )
    return cursor?.use {
        while (it.moveToNext()) {
            if (it.getString(it.getColumnIndexOrThrow(COLUMN_KEY)) == key) {
                return@use it.getString(it.getColumnIndexOrThrow(COLUMN_VALUE))
            }
        }
        return@use null
    } ?: ""
}
