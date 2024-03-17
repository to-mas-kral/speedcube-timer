package com.tom.speedcubetimer.ui.home

import android.util.Log
import com.tom.speedcubetimer.model.PuzzleScrambler
import com.tom.speedcubetimer.model.PuzzleType
import com.tom.speedcubetimer.model.Timer
import com.tom.speedcubetimer.model.TimerState
import com.tom.speedcubetimer.model.TimerTransition
import com.tom.speedcubetimer.ui.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking

data class HomeUiState(
    val changePuzzleTypeDialogShown: Boolean = false,
    val selectedPuzzleType: PuzzleType = PuzzleType._3,
    val scramble: String = "",
    val scrambleSvg: String? = null,
    val scramblerJob: Job = Job().let { it.complete(); it },
    val timerUpdateJob: Job = Job().let { it.complete(); it },
)

class HomeViewModel(private val puzzleScrambler: PuzzleScrambler) : BaseViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _timerState = MutableStateFlow<Timer>(Timer.Idle())
    val timerState: StateFlow<Timer> = _timerState.asStateFlow()

    init {
        refreshScramble(force = true)
    }

    fun refreshScramble(force: Boolean = false) {
        if (force && _uiState.value.scramblerJob.isActive) {
            _uiState.value.scramblerJob.cancel()
        }

        /// Scrambles can be expensive to calculate for bigger puzzles, do in background...
        val newScramblerJob = launch {
            val scramble = puzzleScrambler.makeScramble(uiState.value.selectedPuzzleType)

            _uiState.update { currentState ->
                currentState.copy(
                    scramble = scramble.text,
                    scrambleSvg = scramble.imageSvg,
                )
            }
        }

        _uiState.update { currentState ->
            currentState.copy(
                scramblerJob = newScramblerJob,
            )
        }
    }

    private fun startTimerUpdate() {
        val timerUpdateJob = launch {
            while (true) {
                delay(1)
                _timerState.update { it.updateTiming() }
            }
        }

        timerUpdateJob.invokeOnCompletion {
            Log.d("HomeViewModel", "stopped timer update")
        }

        _uiState.update { currentState ->
            currentState.copy(
                timerUpdateJob = timerUpdateJob,
            )
        }
    }

    private fun stopTimerUpdate() {
        runBlocking {
            _uiState.value.timerUpdateJob.cancelAndJoin()
        }

        Log.d("HomeViewModel", "invoked cancelAndJoin")
    }

    fun changePuzzleType(newPuzzleType: PuzzleType) {
        if (newPuzzleType != uiState.value.selectedPuzzleType) {
            _uiState.update { currentState ->
                currentState.copy(
                    selectedPuzzleType = newPuzzleType
                )
            }

            refreshScramble(force = true)
        }
    }

    fun togglePuzzleTypeDialogShown() {
        _uiState.update { currentState ->
            currentState.copy(
                changePuzzleTypeDialogShown = !currentState.changePuzzleTypeDialogShown
            )
        }
    }

    fun timerPressed() {
        if (uiState.value.scramblerJob.isActive && timerState.value.isIdle()) {
            return
        }

        if (timerState.value.isTiming()) {
            stopTimerUpdate()
        }

        Log.d("HomeViewModel", "pressed")
        _timerState.update { it.press() }

        if (timerState.value.transition == TimerTransition.StartedHolding) {
            startTimerUpdate()
        }
    }

    fun timerReleased() {
        Log.d("HomeViewModel", "released")

        when (timerState.value.state()) {
            TimerState.Holding -> stopTimerUpdate()

            else -> {}
        }

        _timerState.update { it.release() }

        when (timerState.value.transition) {
            TimerTransition.StartedTiming -> {
                refreshScramble()
                startTimerUpdate()
            }

            else -> {}
        }
    }
}
