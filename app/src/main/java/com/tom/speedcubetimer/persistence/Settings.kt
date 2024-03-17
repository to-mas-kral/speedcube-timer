package com.tom.speedcubetimer.persistence

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// TODO: unsure of what a good architecture is, just use simple functions for now :)
//       Google docs about DataStore are a bit confusing...

val TIMER_UPDATE_ENABLED = booleanPreferencesKey("is_timer_update_enabled")

suspend fun settingsGetBoolean(
    dataStore: DataStore<Preferences>, key: Preferences.Key<Boolean>
): Boolean {
    return dataStore.data.map { preferences -> preferences[key] ?: false }.first()
}

suspend fun settingsSetBoolean(
    dataStore: DataStore<Preferences>, key: Preferences.Key<Boolean>, value: Boolean
) {
    dataStore.edit { it[key] = value }
}
