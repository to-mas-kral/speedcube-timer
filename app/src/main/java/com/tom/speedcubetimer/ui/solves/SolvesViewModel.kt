package com.tom.speedcubetimer.ui.solves

import com.tom.speedcubetimer.model.TimeRecord
import com.tom.speedcubetimer.persistence.TimeRecordDao
import com.tom.speedcubetimer.ui.base.BaseViewModel

data class SolvesDetail(
    var solves: List<TimeRecord> = ArrayList(),
)

class SolvesViewModel(
    private val timeRecordDao: TimeRecordDao,
) : BaseViewModel() {
    // TODO: this is really only proof-of-concept
    val solvesState = timeRecordDao.getAll()
}
