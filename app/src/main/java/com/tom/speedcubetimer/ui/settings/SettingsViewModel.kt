package com.tom.speedcubetimer.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tom.speedcubetimer.persistence.TIMER_UPDATE_ENABLED
import com.tom.speedcubetimer.persistence.settingsGetBoolean
import com.tom.speedcubetimer.persistence.settingsSetBoolean
import com.tom.speedcubetimer.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

class SettingsViewModel(private val dataStore: DataStore<Preferences>) : BaseViewModel() {
    private val _timerUpdateEnabled =
        MutableStateFlow(runBlocking { settingsGetBoolean(dataStore, TIMER_UPDATE_ENABLED) })
    val timerUpdateEnabled: StateFlow<Boolean> = _timerUpdateEnabled

    fun onTimerUpdateEnabledSwitched(newValue: Boolean) {
        _timerUpdateEnabled.value = newValue
        launch { settingsSetBoolean(dataStore, TIMER_UPDATE_ENABLED, newValue) }
    }
}
