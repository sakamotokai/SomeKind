package com.example.serverside.repository

import android.content.ContentValues
import android.provider.BaseColumns
import android.util.Log
import com.example.serverside.db.sqlite.DBContract
import com.example.serverside.db.sqlite.GestureDbHelper

class DbRepositoryImpl(private val db: GestureDbHelper) : DbRepository {

    override suspend fun insert(tableName: String, values: ContentValues) {
        db.writableDatabase.apply {
            this.insert(tableName, null, values)
        }
    }

    override suspend fun getAll(): MutableList<Array<String>> {
        Log.e("lofigirl", "Inside get all")
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
            DBContract.Gesture.TABLE_NAME,   // The table to query
            projection,             // The array of columns to return (pass null to get all)
            null,              // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,                   // don't group the rows
            null,                   // don't filter by row groups
            null               // The sort order
        )
        val items = mutableListOf<Array<String>>()
        with(cursor) {
            while (moveToNext()) {
                Log.e(
                    "lofigirl",
                    "Inside while loop: ${getString(getColumnIndexOrThrow(DBContract.Gesture.COLUMN_NAME_MOVETOX))}"
                )
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