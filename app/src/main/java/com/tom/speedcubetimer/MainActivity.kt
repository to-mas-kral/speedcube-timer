package com.tom.speedcubetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tom.speedcubetimer.ui.AppContainer
import com.tom.speedcubetimer.ui.theme.SpeedcubeTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedcubeTimerTheme {
                AppContainer()
            }
        }
    }
}
