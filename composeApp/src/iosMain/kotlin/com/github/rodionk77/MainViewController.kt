package com.github.rodionk77

import androidx.compose.ui.window.ComposeUIViewController
import com.github.rodionk77.common.App
import com.github.rodionk77.common.AppContainer

fun MainViewController() = ComposeUIViewController { App() }.also {
    AppContainer.dataStore = createDataStore()
}