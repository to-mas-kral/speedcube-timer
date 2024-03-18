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
import com.tom.speedcubetimer.model.TimeRecord
import com.tom.speedcubetimer.model.toSpeedcubeTime
import org.koin.androidx.compose.getViewModel

@Composable
fun SolvesAndStats(
    innerPadding: PaddingValues,
    selectedPuzzleType: PuzzleType,
    solvesViewModel: SolvesViewModel = getViewModel(),
) {
    val solvesState by solvesViewModel.solvesState.collectAsState(initial = ArrayList())
    val solvesStateFiltered = solvesState.filter { it.puzzleType == selectedPuzzleType }

    SolvesAndStatsInner(innerPadding, SolvesDetail(solvesStateFiltered))
}

@Preview
@Composable
private fun SolvesAndStatsInner(
    innerPadding: PaddingValues = PaddingValues(0.dp), solvesState: SolvesDetail = SolvesDetail()
) {
    Column(
        modifier = Modifier.padding(
            top = innerPadding.calculateTopPadding(),
            bottom = innerPadding.calculateBottomPadding(),
        )
    ) {
        Stats(Modifier.weight(0.5f))
        Solves(solvesState, Modifier.weight(1.0f))
    }
}

@Composable
fun Stats(weight: Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(weight)
    ) {

    }
}

@Composable
fun Solves(solvesState: SolvesDetail, weight: Modifier) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxSize()
            .then(weight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(solvesState.solves) { timeRecord ->
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
