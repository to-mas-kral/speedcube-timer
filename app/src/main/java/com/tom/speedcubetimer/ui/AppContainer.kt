package com.tom.speedcubetimer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tom.speedcubetimer.ui.home.HomeScreen

@Composable
fun AppContainer() {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        val controller = rememberNavController()

        NavHost(navController = controller, startDestination = DEST_HOME) {
            composable(DEST_HOME) {
                HomeScreen()
            }
        }
    }
}

private const val DEST_HOME = "home"

