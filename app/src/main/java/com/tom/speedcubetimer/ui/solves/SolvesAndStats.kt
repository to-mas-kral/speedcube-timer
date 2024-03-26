package com.tom.speedcubetimer.ui.solves

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tom.speedcubetimer.model.PuzzleType
import com.tom.speedcubetimer.model.StatRecord
import com.tom.speedcubetimer.model.TimeRecord
import com.tom.speedcubetimer.model.toSpeedcubeTime
import org.koin.androidx.compose.getViewModel
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SolvesAndStats(
    innerPadding: PaddingValues,
    selectedPuzzleType: PuzzleType,
    solvesViewModel: SolvesViewModel = getViewModel(),
) {
    val solvesState by solvesViewModel.solves.collectAsState(initial = ArrayList())
    val solves = solvesState.filter { it.puzzleType == selectedPuzzleType }
    val stats by solvesViewModel.solveStats.collectAsState()

    SolvesAndStatsInner(
        innerPadding, solves, stats.records[selectedPuzzleType] ?: ArrayList()
    )
}

@Preview
@Composable
private fun SolvesAndStatsInner(
    innerPadding: PaddingValues = PaddingValues(0.dp),
    solves: List<TimeRecord> = ArrayList(),
    stats: List<StatRecord> = ArrayList(),
) {
    Column(
        modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding(),
        )
    ) {
        Stats(Modifier.weight(0.8f), stats, solves)
        Solves(solves, Modifier.weight(1.0f))
    }
}

@Composable
fun Stats(weight: Modifier, stats: List<StatRecord>, solves: List<TimeRecord>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(weight)
    ) {
        SolveCountAndMean(solves, Modifier.weight(0.4f))
        Averages(stats, Modifier.weight(1.0f))
    }
}

@Composable
private fun Averages(stats: List<StatRecord>, weightModifier: Modifier) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .then(weightModifier)
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f)
                .padding(
                    PaddingValues(
                        top = 4.dp, start = 8.dp, end = 4.dp, bottom = 8.dp
                    )
                )
        ) {
            Column(modifier = Modifier.padding(PaddingValues(8.dp))) {
                Text(
                    text = "Current",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                for (record in stats) {
                    Text(
                        text = "Avg ${record.size}: ${record.current.toSpeedcubeTime()}",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f)
                .padding(
                    PaddingValues(
                        top = 4.dp, start = 4.dp, end = 8.dp, bottom = 8.dp
                    )
                )
        ) {
            Column(modifier = Modifier.padding(PaddingValues(8.dp))) {
                Text(
                    text = "Best",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                for (record in stats) {
                    Text(
                        text = "Avg ${record.size}: ${record.best.toSpeedcubeTime()}",
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun SolveCountAndMean(solves: List<TimeRecord>, weightModifier: Modifier) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .then(weightModifier)
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f)
                .padding(
                    PaddingValues(
                        top = 8.dp, start = 8.dp, end = 4.dp, bottom = 4.dp
                    )
                )
        ) {
            Column(modifier = Modifier.padding(PaddingValues(8.dp))) {
                Text(
                    text = "Solves",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = solves.size.toString(),
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.5f)
                .padding(
                    PaddingValues(
                        top = 8.dp, start = 4.dp, end = 8.dp, bottom = 4.dp
                    )
                )
        ) {
            Column(modifier = Modifier.padding(PaddingValues(8.dp))) {
                Text(
                    text = "Mean",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                if (solves.isNotEmpty()) {
                    Text(
                        // TODO: move this somewhere else...
                        text = solves.map { it.time.inWholeMilliseconds }
                            .average().milliseconds.toSpeedcubeTime(),
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@Composable
fun Solves(solves: List<TimeRecord>, weight: Modifier) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(start = 8.dp, end = 8.dp))
            .then(weight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(solves) { timeRecord ->
                Solve(timeRecord)
                Divider()
            }
        }
    }
}

@Composable
fun Solve(timeRecord: TimeRecord) {
    Row(
        horizontalArrangement = Arrangement.Absolute.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                all = 8.dp
            )
    ) {
        Text(
            text = timeRecord.time.toSpeedcubeTime(),
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp
        )
    }
}
