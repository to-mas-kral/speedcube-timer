package com.tom.speedcubetimer.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tom.speedcubetimer.model.PuzzleType
import com.tom.speedcubetimer.model.Timer
import com.tom.speedcubetimer.ui.settings.Settings
import com.tom.speedcubetimer.ui.solves.SolvesAndStats
import org.koin.androidx.compose.getViewModel

const val TIMER_NAV_DEST: String = "timer"
const val SOLVES_NAV_DEST: String = "solves"
const val SETTINGS_NAV_DEST: String = "settings"

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by viewModel.timerState.collectAsState()

    HomeScreenInner(
        uiState, timerState,
        onPuzzleTypeSelected = { newPuzzleType ->
            viewModel.changePuzzleType(newPuzzleType)
            viewModel.togglePuzzleTypeDialogShown()

        },
        onPuzzleTypeChangeDialogDismissRequest = {
            viewModel.togglePuzzleTypeDialogShown()
        },
        onPuzzleTypeButtonClick = {
            viewModel.togglePuzzleTypeDialogShown()
        },
        onTimerPressed = {
            viewModel.timerPressed()
        },
        onTimerReleased = {
            viewModel.timerReleased()
        },
        onRefreshScrambleClick = {
            viewModel.refreshScramble()
        }
    )
}

@Preview
@Composable
fun HomeScreenInner(
    // TODO: passing lambdas instead of the viewModel is a pattern I found online
    //       having to default initialize all args doesn't feel good, but hey, at least the preview works...
    uiState: HomeUiState = HomeUiState(),
    timerState: Timer = Timer.Idle(),
    onPuzzleTypeSelected: (PuzzleType) -> Unit = {},
    onPuzzleTypeChangeDialogDismissRequest: () -> Unit = {},
    onPuzzleTypeButtonClick: () -> Unit = {},
    onTimerPressed: () -> Unit = {},
    onTimerReleased: () -> Unit = {},
    onRefreshScrambleClick: () -> Unit = {},
) {
    val navController = rememberNavController()

    if (uiState.changePuzzleTypeDialogShown) {
        ChangePuzzleTypeDialog(
            uiState.selectedPuzzleType, onPuzzleTypeSelected, onPuzzleTypeChangeDialogDismissRequest
        )
    }

    // TODO: The timer needs to hide the topBar and bottomBar, didn't
    //       know how to move the scaffold and navhost upwards...
    Scaffold(topBar = {
        AnimateSlideTop(visible = isVisibleRest(timerState)) {
            TopBar(uiState, onPuzzleTypeButtonClick)
        }
    }, bottomBar = {
        AnimateSlideBottom(visible = isVisibleRest(timerState)) {
            BottomBar(navController)
        }
    }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TIMER_NAV_DEST,
            enterTransition = { fadeIn(tween(200)) },
            exitTransition = { fadeOut(tween(200)) },
            popEnterTransition = { fadeIn(tween(200)) },
            popExitTransition = { fadeOut(tween(200)) },
        ) {
            composable(TIMER_NAV_DEST) {
                TimerView(
                    innerPadding,
                    uiState,
                    timerState,
                    onTimerPressed,
                    onTimerReleased,
                    onRefreshScrambleClick
                )
            }
            composable(SOLVES_NAV_DEST) {
                SolvesAndStats(innerPadding, uiState.selectedPuzzleType)
            }
            composable(SETTINGS_NAV_DEST) {
                Settings(innerPadding)
            }
        }
    }
}

private const val barsSize = 0.075f

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    uiState: HomeUiState, onPuzzleTypeButtonClick: () -> Unit
) {
    // TODO: Round corners only on bottom of card ?
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ), modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxHeight(barsSize)
    ) {
        CenterAlignedTopAppBar(colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ), modifier = Modifier.fillMaxSize(), title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxHeight()
            ) {
                Button(onClick = onPuzzleTypeButtonClick) {
                    Text(text = uiState.selectedPuzzleType.toString())
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Change puzzle type dialog"
                    )
                }
            }
        })
    }
}

data class NavBarItem(
    val description: String,
    val icon: ImageVector,
    val navDestination: String,
)

val navBarItems = listOf(
    NavBarItem("Main timer", Icons.Filled.Timer, TIMER_NAV_DEST), NavBarItem(
        "Solve history and statistics",

        Icons.Filled.Timeline, SOLVES_NAV_DEST
    ), NavBarItem(
        "Application settings", Icons.Filled.Settings, SETTINGS_NAV_DEST
    )
)

@Composable
fun BottomBar(navController: NavHostController) {
    var selectedItem by remember { mutableIntStateOf(0) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ), modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxHeight(barsSize)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Absolute.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navBarItems.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.0f)
                        .clickable {
                            selectedItem = index
                            navController.navigate(item.navDestination)
                        }, Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.description,
                        modifier = Modifier
                            .fillMaxHeight(0.4f)
                            .aspectRatio(1.0f)
                            .alpha(
                                if (selectedItem == index) {
                                    1.0f
                                } else {
                                    0.7f
                                }
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun <T> AnimateSlideTop(visible: Boolean, content: @Composable () -> T) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically() + fadeIn(),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        content()
    }
}

@Composable
fun <T> AnimateSlideBottom(
    visible: Boolean, modifier: Modifier = Modifier, content: @Composable () -> T
) {
    AnimatedVisibility(
        visible = visible, enter = slideInVertically(
            initialOffsetY = {
                it / 2
            },
        ) + fadeIn(), exit = slideOutVertically(
            targetOffsetY = {
                it / 2
            },
        ) + shrinkVertically() + fadeOut(), modifier = modifier
    ) {
        content()
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

fun isVisibleRest(timerState: Timer) =
    timerState.isIdle() || (timerState.isHolding() && !timerState.isHoldingEngaged())
