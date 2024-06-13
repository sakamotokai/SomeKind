package com.example.serverside.db.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class GestureDbHelper(context:Context):SQLiteOpenHelper(context, "Gesture",null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DBContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(DBContract.SQL_DELETE_ENTRIES)
    }
}