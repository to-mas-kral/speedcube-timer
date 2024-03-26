package com.tom.speedcubetimer.persistence.solves

import com.tom.speedcubetimer.model.PuzzleType
import com.tom.speedcubetimer.model.Stats
import com.tom.speedcubetimer.model.TimeRecord
import com.tom.speedcubetimer.model.calculateStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolvesRepository(private val timeRecordDao: TimeRecordDao) {
    private val scope = CoroutineScope(Dispatchers.Default)

    val solves = timeRecordDao.getAll()
    private var cachedSolves: List<TimeRecord> = ArrayList()
    val solveStats = MutableStateFlow(Stats())

    init {
        scope.launch {
            timeRecordDao.getAll().collect { solves ->
                cachedSolves = solves
                val stats = calculateStats(solves)
                solveStats.update { stats }
            }
        }
    }

    fun insert(timeRecord: TimeRecord) {
        scope.launch {
            timeRecordDao.insert(timeRecord)
        }
    }

    fun deleteLast(selectedPuzzleType: PuzzleType) {
        scope.launch {
            val lastRecord = cachedSolves.find { it.puzzleType == selectedPuzzleType }
            lastRecord?.let { timeRecordDao.delete(it) }
        }
    }
}
