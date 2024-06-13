package com.example.serverside.repository

import android.content.ContentValues

interface DbRepository {
    suspend fun insert(tableName:String, values:ContentValues)
    suspend fun getAll():MutableList<Array<String>>
}