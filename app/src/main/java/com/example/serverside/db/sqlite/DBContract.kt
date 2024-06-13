package com.example.serverside.db.sqlite

import android.provider.BaseColumns

object DBContract {

    object Gesture:BaseColumns{
        const val TABLE_NAME = "gesture"
        const val COLUMN_NAME_MOVETOX = "moveToX"
        const val COLUMN_NAME_MOVETOY = "moveToY"
        const val COLUMN_NAME_LINETOX = "lineToX"
        const val COLUMN_NAME_LINETOY = "lineToY"
        const val COLUMN_NAME_DONE = "done"
    }

    const val SQL_CREATE_ENTRIES =
        "CREATE TABLE ${Gesture.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${Gesture.COLUMN_NAME_MOVETOX} TEXT," +
                "${Gesture.COLUMN_NAME_MOVETOY} TEXT,"+
                "${Gesture.COLUMN_NAME_LINETOX} TEXT,"+
                "${Gesture.COLUMN_NAME_LINETOY} TEXT,"+
                "${Gesture.COLUMN_NAME_DONE} TEXT)"

    const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Gesture.TABLE_NAME}"
}