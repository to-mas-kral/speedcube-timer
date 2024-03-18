package com.tom.speedcubetimer.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.tom.speedcubetimer.model.PuzzleScrambler
import com.tom.speedcubetimer.persistence.AppDatabase
import com.tom.speedcubetimer.ui.home.HomeViewModel
import com.tom.speedcubetimer.ui.settings.SettingsViewModel
import com.tom.speedcubetimer.ui.solves.SolvesViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val SETTINGS: String = "settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

val appModules by lazy {
    listOf(homeScreenModule, uiModule, persistenceModule)
}

val persistenceModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(), AppDatabase::class.java, "speedcube-timer-database"
        ).build()
    }
}

val homeScreenModule = module {
    single {
        // Upstream issue: https://github.com/thewca/tnoodle-lib/issues/13
        Log.w("Koin", "This is an upstream with the WCA tnoodle library")
        PuzzleScrambler()
    }
}

val uiModule = module {
    this.viewModel { HomeViewModel(get<PuzzleScrambler>(), get<AppDatabase>().timeRecordDao()) }
    this.viewModel { SettingsViewModel(androidContext().dataStore) }
    this.viewModel { SolvesViewModel(get<AppDatabase>().timeRecordDao()) }
}
