package com.tom.speedcubetimer.ui.home

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp
import com.tom.speedcubetimer.di.dataStore
import com.tom.speedcubetimer.model.DURATION_HOLDING
import com.tom.speedcubetimer.model.Timer
import com.tom.speedcubetimer.model.TimerState
import com.tom.speedcubetimer.model.toSpeedcubeTime
import com.tom.speedcubetimer.persistence.TIMER_UPDATE_ENABLED
import com.tom.speedcubetimer.persistence.settingsGetBoolean
import kotlinx.coroutines.runBlocking

@Composable
fun TimeAndActions(
    timerState: Timer,
    uiState: HomeUiState,
    onRefreshScrambleClick: () -> Unit,
    onDeleteLastSolveClick: () -> Unit
) {
    Column(
        modifier = Modifier.layoutId(TIME_AND_ACTIONS_ID)
    ) {
        Time(timerState)

        TimerActions(uiState, timerState, onRefreshScrambleClick, onDeleteLastSolveClick)
    }
}

@Composable
private fun TimerActions(
    uiState: HomeUiState,
    timerState: Timer,
    onRefreshScrambleClick: () -> Unit,
    onDeleteLastSolveClick: () -> Unit
) {
    val isVisible = !uiState.scramblerJob.isActive && timerState.isIdle()
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onRefreshScrambleClick,
            enabled = isVisible,
            modifier = consumePointerEvents
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh scramble",
            )
        }

        IconButton(
            onClick = onDeleteLastSolveClick,
            enabled = isVisible,
            modifier = consumePointerEvents
        ) {
            Icon(
                imageVector = Icons.Filled.Clear, contentDescription = "Delete last time"
            )
        }
    }
}

@Composable
private fun textScaleTransition(transition: Transition<Boolean>): State<Float> {
    return transition.animateFloat(
        transitionSpec = {
            tween(
                DURATION_HOLDING.inWholeMilliseconds.toInt(), easing = EaseIn
            )
        }, label = "timer text size reduction"
    ) { state ->
        if (state) 0.75f else 1.0f
    }
}

@Composable
private fun textColorTransition(transition: Transition<Boolean>): State<Color> {
    return transition.animateColor(
        transitionSpec = {
            tween(DURATION_HOLDING.inWholeMilliseconds.toInt(), easing = { fraction ->
                if (fraction <= 0.95f) {
                    0.0f
                } else {
                    1.0f
                }
            })
        }, label = "timer text color shift"
    ) { state ->
        if (state) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
    }
}

@Composable
private fun Time(timerState: Timer) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        val shouldAnimate = timerState.isHolding()
        val transition = updateTransition(shouldAnimate, label = "timer holding")
        val textScale by textScaleTransition(transition)
        val textColor by textColorTransition(transition)

        Text(
            text = formatTimerTime(timerState),
            modifier = Modifier.graphicsLayer {
                scaleX = textScale
                scaleY = textScale
                transformOrigin = TransformOrigin.Center
            },
            style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 64.sp,
            color = textColor,
        )
    }
}

@Composable
private fun formatTimerTime(timerState: Timer): String {
    return when (timerState.state()) {
        TimerState.Idle -> timerState.previousTime.toSpeedcubeTime()

        TimerState.Holding -> {
            val prevTime = timerState.previousTime.toSpeedcubeTime()
            prevTime.replace(Regex("\\d"), "0")
        }

        TimerState.Timing -> {
            val dataStore = LocalContext.current.dataStore
            // TODO: probably don't wanna be blocking here ?
            runBlocking {
                if (settingsGetBoolean(dataStore, TIMER_UPDATE_ENABLED)) {
                    timerState.calculateCurrentTimingTime().toSpeedcubeTime()
                } else {
                    "..."
                }
            }
        }
    }
}
