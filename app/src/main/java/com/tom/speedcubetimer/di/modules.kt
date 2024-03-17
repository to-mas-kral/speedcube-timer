package com.tom.speedcubetimer.di

import android.util.Log
import com.tom.speedcubetimer.model.PuzzleScrambler
import com.tom.speedcubetimer.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


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
}
