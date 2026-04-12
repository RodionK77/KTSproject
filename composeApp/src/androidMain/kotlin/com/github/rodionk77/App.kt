package com.github.rodionk77

import android.app.Application
import com.github.rodionk77.di.androidPlatformModule
import com.github.rodionk77.di.networkModule
import com.github.rodionk77.di.repositoryModule
import com.github.rodionk77.di.viewModelModule
import com.google.firebase.BuildConfig
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            // Debug build

            // disable firebase crashlytics
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
            // init napier
            Napier.base(DebugAntilog())
        } else {
            // Others(Release build)

            // enable firebase crashlytics
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            // init napier
            Napier.base(CrashlyticsAntilog())
        }
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(androidPlatformModule, networkModule, repositoryModule, viewModelModule)
        }
    }
}
