package com.tom.speedcubetimer.persistence.solves

import com.tom.speedcubetimer.model.Stats
import com.tom.speedcubetimer.model.calculateStats
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolvesRepository(timeRecordDao: TimeRecordDao) {
    private val scope = CoroutineScope(Dispatchers.Default)

    val solves = timeRecordDao.getAll()
    val solveStats = MutableStateFlow(Stats())

    init {
        scope.launch {
            timeRecordDao.getAll().collect { solves ->
                val stats = calculateStats(solves)
                solveStats.update { stats }
            }
        }
    }
}
