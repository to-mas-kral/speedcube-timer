package com.tom.speedcubetimer.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tom.speedcubetimer.model.TimeRecord

@Database(
    entities = [TimeRecord::class], version = 1
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun timeRecordDao(): TimeRecordDao
}
