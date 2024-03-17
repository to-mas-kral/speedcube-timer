package com.tom.speedcubetimer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tom.speedcubetimer.ui.home.HomeScreen

@Composable
fun AppContainer() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        HomeScreen()
    }
}
