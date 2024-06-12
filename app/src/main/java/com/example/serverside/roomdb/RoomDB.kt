package com.example.serverside.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataRecord::class,Gesture::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): AccessDao
}