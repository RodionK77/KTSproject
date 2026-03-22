package com.github.rodionk77

import androidx.compose.ui.window.ComposeUIViewController
import com.github.rodionk77.common.App
import com.github.rodionk77.di.iosPlatformModule
import com.github.rodionk77.di.networkModule
import com.github.rodionk77.di.repositoryModule
import com.github.rodionk77.di.viewModelModule
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.compose.KoinApplication

fun MainViewController() = ComposeUIViewController {
    Napier.base(DebugAntilog())
    KoinApplication(application = {
        modules(iosPlatformModule, networkModule, repositoryModule, viewModelModule)
    }) {
        App()
    }
}
