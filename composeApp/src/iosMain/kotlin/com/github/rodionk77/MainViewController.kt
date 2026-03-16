package com.github.rodionk77

import androidx.compose.ui.window.ComposeUIViewController
import com.github.rodionk77.common.App
import com.github.rodionk77.common.AppContainer
import com.github.rodionk77.common.database.getRoomDatabase
import com.liftric.kvault.KVault

fun MainViewController() = ComposeUIViewController { App() }.also {
    //AppContainer.dataStore = createDataStore()
    AppContainer.kVault = KVault()
    AppContainer.database = getRoomDatabase(getDatabaseBuilder())
}