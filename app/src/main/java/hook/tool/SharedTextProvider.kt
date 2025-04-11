package hook.tool

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import com.hook.fakewifi.App

class SharedTextProvider : ContentProvider() {

    private lateinit var dbHelper: SQLiteOpenHelper

    override fun onCreate(): Boolean {
        App.app = context ?: return false
        dbHelper = object : SQLiteOpenHelper(context, "shared_texts.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE ${SharedContentContract.TextEntry.TABLE_NAME} (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        ${SharedContentContract.TextEntry.COLUMN_KEY} TEXT,
                        ${SharedContentContract.TextEntry.COLUMN_VALUE} TEXT
                    )
                    """.trimIndent()
                )
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
                // No-op
            }
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val db = dbHelper.readableDatabase
        return db.query(
            SharedContentContract.TextEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = dbHelper.writableDatabase
        val id = db.insert(SharedContentContract.TextEntry.TABLE_NAME, null, values)
        return ContentUris.withAppendedId(uri, id)
    }

    override fun getType(uri: Uri): String? {
        return "vnd.android.cursor.dir/vnd.${SharedContentContract.AUTHORITY}.texts"
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        return db.delete(SharedContentContract.TextEntry.TABLE_NAME, selection, selectionArgs)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = dbHelper.writableDatabase
        return db.update(SharedContentContract.TextEntry.TABLE_NAME, values, selection, selectionArgs)
    }
}