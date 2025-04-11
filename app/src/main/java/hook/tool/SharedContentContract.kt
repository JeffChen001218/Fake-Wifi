package hook.tool

import android.net.Uri

object SharedContentContract {
    const val AUTHORITY = "com.hook.fakewifi.share"
    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/texts")

    object TextEntry {
        const val TABLE_NAME = "texts"

        const val COLUMN_KEY = "_key"
        const val COLUMN_VALUE = "_value"

        val allColumns = arrayOf(COLUMN_KEY, COLUMN_VALUE)
    }
}