package com.github.rodionk77

import android.app.Application
import com.github.rodionk77.di.androidPlatformModule
import com.github.rodionk77.di.networkModule
import com.github.rodionk77.di.repositoryModule
import com.github.rodionk77.di.viewModelModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(androidPlatformModule, networkModule, repositoryModule, viewModelModule)
        }
    }
}
