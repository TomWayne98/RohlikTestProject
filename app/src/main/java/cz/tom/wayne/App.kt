package cz.tom.wayne

import android.app.Application
import cz.tom.wayne.di.dbModule
import cz.tom.wayne.di.homeScreenModule
import cz.tom.wayne.di.navigationModule
import cz.tom.wayne.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@App)
            modules(
                listOf(
                    networkModule, navigationModule, dbModule, homeScreenModule
                )
            )
        }
        Timber.plant(Timber.DebugTree())
    }
}