package com.tom.speedcubetimer.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.tom.speedcubetimer.model.PuzzleScrambler
import com.tom.speedcubetimer.ui.home.HomeViewModel
import com.tom.speedcubetimer.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val SETTINGS: String = "settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

val appModules by lazy {
    listOf(homeScreenModule, uiModule)
}

val homeScreenModule = module {
    single {
        // Upstream issue: https://github.com/thewca/tnoodle-lib/issues/13
        Log.w("Koin", "This is an upstream with the WCA tnoodle library")
        PuzzleScrambler()
    }
}

val uiModule = module {
    this.viewModel { HomeViewModel(get<PuzzleScrambler>()) }
    this.viewModel { SettingsViewModel(androidContext().dataStore) }
}
