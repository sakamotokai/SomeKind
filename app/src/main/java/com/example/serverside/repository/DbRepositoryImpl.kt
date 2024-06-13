package com.example.serverside.repository

import android.content.ContentValues
import android.provider.BaseColumns
import com.example.serverside.db.sqlite.DBContract
import com.example.serverside.db.sqlite.GestureDbHelper

class DbRepositoryImpl(private val db: GestureDbHelper) : DbRepository {

    override suspend fun insert(tableName: String, values: ContentValues) {
        db.writableDatabase.apply {
            this.insert(tableName, null, values)
        }
    }

    override suspend fun getAll(): MutableList<Array<String>> {
        val dbReadable = db.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            DBContract.Gesture.COLUMN_NAME_MOVETOX,
            DBContract.Gesture.COLUMN_NAME_MOVETOY,
            DBContract.Gesture.COLUMN_NAME_LINETOX,
            DBContract.Gesture.COLUMN_NAME_LINETOY,
            DBContract.Gesture.COLUMN_NAME_DONE
        )
        val cursor = dbReadable.query(
            DBContract.Gesture.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val items = mutableListOf<Array<String>>()
        with(cursor) {
            while (moveToNext()) {
                items.add(
                    arrayOf(
                        getString(getColumnIndexOrThrow(DBContract.Gesture.COLUMN_NAME_MOVETOX)),
                        getString(getColumnIndexOrThrow(DBContract.Gesture.COLUMN_NAME_MOVETOY)),
                        getString(getColumnIndexOrThrow(DBContract.Gesture.COLUMN_NAME_LINETOX)),
                        getString(getColumnIndexOrThrow(DBContract.Gesture.COLUMN_NAME_LINETOY)),
                        getString(getColumnIndexOrThrow(DBContract.Gesture.COLUMN_NAME_DONE)),
                    )
                )
            }
        }
        return items
    }
}