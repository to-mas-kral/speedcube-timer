package com.tom.speedcubetimer.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tom.speedcubetimer.model.PuzzleType

@Composable
fun ChangePuzzleTypeDialog(
    selectedPuzzleType: PuzzleType,
    onPuzzleTypeSelected: (PuzzleType) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                PuzzleType.entries.forEach { puzzleType ->
                    SingleRadioButton(
                        puzzleType, selectedPuzzleType, onPuzzleTypeSelected = onPuzzleTypeSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleRadioButton(
    puzzleType: PuzzleType,
    selectedPuzzleType: PuzzleType,
    onPuzzleTypeSelected: (PuzzleType) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selectedPuzzleType == puzzleType,
            onClick = { onPuzzleTypeSelected(puzzleType) },
        )
        Text(
            text = puzzleType.toString(),
        )
    }
}
