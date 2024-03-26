package com.tom.speedcubetimer.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.tom.speedcubetimer.model.Timer
import java.nio.ByteBuffer

@Composable
fun TimerView(
    innerPadding: PaddingValues,
    uiState: HomeUiState,
    timerState: Timer,
    onTimerPressed: () -> Unit,
    onTimerReleased: () -> Unit,
    onRefreshScrambleClick: () -> Unit,
    onDeleteLastSolveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding(),
            )
            .pointerInput(null) {
                handleTimerInput(onTimerPressed, onTimerReleased)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TimerInner(uiState, timerState, onRefreshScrambleClick, onDeleteLastSolveClick)
    }
}

@Composable
private fun TimerInner(
    uiState: HomeUiState,
    timerState: Timer,
    onRefreshScrambleClick: () -> Unit,
    onDeleteLastSolveClick: () -> Unit
) {
    ConstraintLayout(
        layoutConstraints(), modifier = Modifier.fillMaxSize()
    ) {
        ScrambleText(uiState, timerState)

        TimeAndActions(
            timerState,
            uiState,
            onRefreshScrambleClick,
            onDeleteLastSolveClick
        )

        ScrambleImage(uiState, timerState)
    }
}

private suspend fun PointerInputScope.handleTimerInput(
    onTimerPressed: () -> Unit,
    onTimerReleased: () -> Unit,
) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()

            if (event.changes.any { !it.isConsumed }) {
                when (event.type) {
                    PointerEventType.Press -> onTimerPressed()
                    PointerEventType.Release -> onTimerReleased()
                }
            }
        }
    }
}

@Composable
private fun ScrambleText(
    uiState: HomeUiState, timerState: Timer
) {
    AnimatedVisibility(
        visible = timerState.isIdle(),
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth()
                .layoutId(SCRAMBLE_TEXT_ID)
        ) {
            if (uiState.scramblerJob.isActive) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterVertically),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            } else {
                Text(
                    text = uiState.scramble,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun ScrambleImage(
    uiState: HomeUiState, timerState: Timer
) {
    if (uiState.scrambleSvg != null) {
        AnimateSlideBottom(
            visible = isVisibleRest(timerState) && !uiState.scramblerJob.isActive,
            Modifier.layoutId(
                SCRAMBLE_IMG_ID
            )
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ByteBuffer.wrap(uiState.scrambleSvg.toByteArray()))
                    .decoderFactory(SvgDecoder.Factory()).crossfade(true).build(),
                contentDescription = "scramble image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(192.dp)
            )
        }
    }
}


const val SCRAMBLE_TEXT_ID = "scramble"
const val TIME_AND_ACTIONS_ID = "timeAndActions"
const val SCRAMBLE_IMG_ID = "scrambleImg"

private fun layoutConstraints(): ConstraintSet {
    return ConstraintSet {
        val timeAndActions = createRefFor(TIME_AND_ACTIONS_ID)
        val scrambleText = createRefFor(SCRAMBLE_TEXT_ID)
        val scrambleImg = createRefFor(SCRAMBLE_IMG_ID)

        constrain(timeAndActions) {
            centerVerticallyTo(parent)
            centerHorizontallyTo(parent)
        }

        constrain(scrambleText) {
            centerHorizontallyTo(parent)
            top.linkTo(parent.top)
        }

        constrain(scrambleImg) {
            centerHorizontallyTo(parent)
            bottom.linkTo(parent.bottom)
        }
    }
}

