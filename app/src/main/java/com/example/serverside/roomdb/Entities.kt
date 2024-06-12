package com.example.serverside.roomdb

import android.provider.ContactsContract.Data
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

@Entity
data class DataRecord(
    @PrimaryKey val record_id: Int,
    @ColumnInfo(name = "status") val done: Boolean,
    @ColumnInfo(name = "time") val date:String,
)

data class GesturesInRecord(
    @Embedded val record:DataRecord,
    @Relation(
        parentColumn = "record_id",
        entityColumn = "gesture_id"
    )
    val gestures:List<Gesture>
)

@Entity
data class Gesture(
    @PrimaryKey val gesture_id:Int,
    @ColumnInfo(name = "moveToX") val moveToX:Float,
    @ColumnInfo(name = "moveToY") val moveToY:Float,
    @ColumnInfo(name = "lineToX") val lineToX:Float,
    @ColumnInfo(name = "lineToY") val lineToY:Float,
)