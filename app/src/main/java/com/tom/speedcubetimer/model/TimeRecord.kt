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

    @TypeConverter
    fun toPuzzleType(value: Int) = enumValues<PuzzleType>()[value]

    @TypeConverter
    fun fromPuzzleType(value: PuzzleType) = value.ordinal
}

// TODO: it would have been better to create a separate table for every puzzleType...

@Entity(tableName = "time_records")
@TypeConverters(Converters::class)
class TimeRecord(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,

    @ColumnInfo(name = "puzzle_type")
    @TypeConverters(Converters::class)
    val puzzleType: PuzzleType,

    @ColumnInfo(name = "time")
    @TypeConverters(Converters::class)
    val time: Duration
)
