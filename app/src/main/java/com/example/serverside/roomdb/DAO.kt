package com.example.serverside.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccessDao{
    @Insert
    fun insert(vararg records: DataRecord)
}