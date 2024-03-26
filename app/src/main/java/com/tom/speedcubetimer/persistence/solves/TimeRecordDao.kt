package com.tom.speedcubetimer.persistence.solves

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tom.speedcubetimer.model.TimeRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeRecordDao {
    @Query("SELECT * FROM time_records ORDER BY uid DESC")
    fun getAll(): Flow<List<TimeRecord>>

    @Insert
    fun insert(obj: TimeRecord)

    @Insert
    fun insert(vararg obj: TimeRecord)

    @Update
    fun update(obj: TimeRecord)

    @Delete
    fun delete(obj: TimeRecord)
}
