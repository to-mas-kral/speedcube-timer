package com.tom.speedcubetimer.model

import android.util.Log
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlin.time.TimeSource.Monotonic.ValueTimeMark

val DURATION_HOLDING = 200.milliseconds

/// What state the Timer is currently in
enum class TimerState {
    Idle, Holding, Timing,
}

/// What transition happened on previous TimerState change
enum class TimerTransition {
    Startup, StartedTiming, StoppedTiming, StartedHolding, ContinuedHolding, StoppedHolding,
}

sealed class Timer {
    data class Idle(
        override var previousTime: Duration = 0.milliseconds,
        override var transition: TimerTransition = TimerTransition.Startup
    ) : Timer()

    data class Holding(
        val startTime: ValueTimeMark,
        val currentTime: ValueTimeMark,
        override var previousTime: Duration,
        override var transition: TimerTransition,
    ) : Timer()

    data class Timing(
        val startTime: ValueTimeMark,
        val currentTime: ValueTimeMark,
        override var previousTime: Duration,
        override var transition: TimerTransition,
    ) : Timer()

    abstract var previousTime: Duration
    abstract var transition: TimerTransition

    fun press(): Timer {
        Log.d("Timer", "Pressed: ${state()}")
        return when (this) {
            is Idle -> {
                val now = TimeSource.Monotonic.markNow()
                Holding(now, now, previousTime, TimerTransition.StartedHolding)
            }

            is Holding -> {
                val now = TimeSource.Monotonic.markNow()
                Holding(
                    this.startTime, now, previousTime, TimerTransition.ContinuedHolding
                )
            }

            is Timing -> {
                val now = TimeSource.Monotonic.markNow()
                val duration = now - this.startTime
                Idle(duration, TimerTransition.StoppedTiming)
            }
        }.apply {
            Log.d("Timer", "After press: ${state()}")
        }
    }

    fun release(): Timer {
        Log.d("Timer", "Released: ${state()}")
        return when (this) {
            is Idle -> {
                // In this case, the Timer was *just* stopped in the previous press event
                Idle(previousTime, TimerTransition.StoppedTiming)
            }

            is Holding -> {
                val now = TimeSource.Monotonic.markNow()
                val duration = now - this.startTime

                if (duration >= DURATION_HOLDING) {
                    Timing(now, now, previousTime, TimerTransition.StartedTiming)
                } else {
                    Idle(previousTime, TimerTransition.StoppedHolding)
                }
            }

            is Timing -> throw Exception("Invalid combination of Timing Timer and Release event")
        }.apply {
            Log.d("Timer", "After released: ${state()}")
        }
    }

    fun updateTiming(): Timer {
        return when (this) {
            is Idle -> throw Exception("Invalid Timer state Idle, should be Timing")
            is Holding -> {
                val now = TimeSource.Monotonic.markNow()
                Holding(startTime, now, previousTime, transition)
            }

            is Timing -> {
                val now = TimeSource.Monotonic.markNow()
                Timing(startTime, now, previousTime, transition)
            }
        }
    }

    fun calculateCurrentTimingTime(): Duration {
        return when (this) {
            is Holding -> throw Exception("Invalid Timer state Idle, should be Timing")
            is Idle -> throw Exception("Invalid Timer state Idle, should be Timing")
            is Timing -> {
                val now = TimeSource.Monotonic.markNow()
                now - startTime
            }
        }
    }

    fun state(): TimerState {
        return when (this) {
            is Holding -> TimerState.Holding
            is Idle -> TimerState.Idle
            is Timing -> TimerState.Timing
        }
    }

    fun isIdle(): Boolean = state() == TimerState.Idle
    fun isHolding(): Boolean = state() == TimerState.Holding
    fun isHoldingEngaged(): Boolean {
        return when (this) {
            is Idle -> false
            is Holding -> {
                val now = TimeSource.Monotonic.markNow()
                val duration = now - this.startTime
                duration > DURATION_HOLDING
            }

            is Timing -> false
        }
    }

    fun isTiming(): Boolean = state() == TimerState.Timing
}

fun Duration.toSpeedcubeTime(): String {
    toComponents { hours, minutes, seconds, nanoseconds ->
        var time = ""
        if (hours > 0) {
            time += "${hours}:"
        }

        if (minutes > 0) {
            time += "${minutes}:"
        }

        time += "${seconds}."

        val millis = (nanoseconds / 1000000).toString().take(3).padEnd(3, '0')
        time += millis

        return time
    }
}
