package com.tom.speedcubetimer.ui.solves

import com.tom.speedcubetimer.model.TimeRecord
import com.tom.speedcubetimer.persistence.solves.SolvesRepository
import com.tom.speedcubetimer.ui.base.BaseViewModel

data class SolvesDetail(
    var solves: List<TimeRecord> = ArrayList(),
)

class SolvesViewModel(
    solvesRepository: SolvesRepository,
) : BaseViewModel() {
    val solves = solvesRepository.solves
    val solveStats = solvesRepository.solveStats
}
