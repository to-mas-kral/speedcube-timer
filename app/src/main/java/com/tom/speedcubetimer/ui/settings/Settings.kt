package com.tom.speedcubetimer.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.getViewModel

@Composable
fun Settings(
    innerPadding: PaddingValues,
    viewModel: SettingsViewModel = getViewModel(),
) {
    val state = viewModel.timerUpdateEnabled.collectAsState()

    // TODO: need to repeat this column with innerPadding because I can't put it directly in navhost...
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding(),
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SettingsInner(viewModel, state)
    }
}

@Composable
fun SettingsInner(viewModel: SettingsViewModel, state: State<Boolean>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        BooleanSwitch(
            text = "Timer updates while solving",
            checked = state.value,
            onChange = { newValue ->
                viewModel.onTimerUpdateEnabledSwitched(newValue)
            })

        //Divider()
    }
}

@Composable
fun BooleanSwitch(
    text: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 8.dp,
                end = 8.dp,
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Absolute.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = text, fontSize = 16.sp)
            Switch(checked = checked, onCheckedChange = onChange, modifier = Modifier.scale(0.75f))
        }
    }
}
