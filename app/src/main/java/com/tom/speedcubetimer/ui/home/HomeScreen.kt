package com.tom.speedcubetimer.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.tom.speedcubetimer.model.Timer
import org.koin.androidx.compose.getViewModel
import java.nio.ByteBuffer

@Preview
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by viewModel.timerState.collectAsState()

    if (uiState.changePuzzleTypeDialogShown) {
        ChangePuzzleTypeDialog(viewModel, uiState)
    }

    Scaffold(
        topBar = {
            val visible = isVisibleRest(timerState)

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + shrinkVertically() + fadeOut()
            ) {
                TopBar(viewModel, uiState)
            }
        },
    ) { innerPadding ->
        MainContent(viewModel, innerPadding, uiState, timerState)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    viewModel: HomeViewModel, uiState: HomeUiState
) {
    CenterAlignedTopAppBar(colors = topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ), title = {
        Button(onClick = { viewModel.togglePuzzleTypeDialogShown() }) {
            Text(text = uiState.selectedPuzzleType.toString())
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Change puzzle type dialog"
            )
        }
    })
}

@Composable
private fun MainContent(
    viewModel: HomeViewModel,
    innerPadding: PaddingValues,
    uiState: HomeUiState,
    timerState: Timer
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding()
            )
            .pointerInput(null) {
                handleTimerInput(viewModel)
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MainContentInner(viewModel, uiState, timerState)
    }
}

@Composable
private fun MainContentInner(
    viewModel: HomeViewModel, uiState: HomeUiState, timerState: Timer
) {
    ConstraintLayout(
        layoutConstraints(), modifier = Modifier.fillMaxSize()
    ) {
        ScrambleText(uiState, timerState)

        TimeAndActions(viewModel, timerState, uiState)

        ScrambleImage(uiState, timerState)
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
        modifier = Modifier.wrapContentHeight()
    ) {
        Row(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
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
        AnimatedVisibility(
            visible = isVisibleRest(timerState) && !uiState.scramblerJob.isActive,
            enter = slideInVertically(
                initialOffsetY = {
                    it / 2
                },
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = {
                    it / 2
                },
            ) + shrinkVertically() + fadeOut(),
            modifier = Modifier
                .layoutId(SCRAMBLE_IMG_ID)
                .padding(bottom = 16.dp)
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

val consumePointerEvents = Modifier.pointerInput(Unit) {
    awaitEachGesture {
        while (true) {
            val event = awaitPointerEvent()
            event.changes.forEach { it.consume() }
        }
    }
}

private fun isVisibleRest(timerState: Timer) =
    timerState.isIdle() || (timerState.isHolding() && !timerState.isHoldingEngaged())


private suspend fun PointerInputScope.handleTimerInput(
    viewModel: HomeViewModel
) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()

            if (event.changes.any { !it.isConsumed }) {
                when (event.type) {
                    PointerEventType.Press -> viewModel.timerPressed()
                    PointerEventType.Release -> viewModel.timerReleased()
                }
            }
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
