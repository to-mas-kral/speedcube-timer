package com.tom.speedcubetimer

import android.app.Application
import com.tom.speedcubetimer.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(applicationContext)
            androidLogger(
                Level.DEBUG
                // TODO if (BuildConfig.DEBUG)
            )
            modules(appModules)
        }
    }
}
