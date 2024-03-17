package com.tom.speedcubetimer.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Converters {
    @TypeConverter
    fun fromDuration(value: Duration): Long {
        return value.inWholeMilliseconds
    }

    @TypeConverter
    fun longToDuration(value: Long): Duration {
        return value.milliseconds
    }
}

@Entity(tableName = "time_records")
@TypeConverters(Converters::class)
class TimeRecord(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "puzzle_type") val puzzleType: PuzzleType,
    @ColumnInfo(name = "time") val time: Duration
)
