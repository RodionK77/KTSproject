package com.github.rodionk77

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.github.rodionk77.common.App
import com.github.rodionk77.common.AppContainer
import com.github.rodionk77.common.database.AppDatabase
import com.github.rodionk77.common.database.getRoomDatabase
import com.liftric.kvault.KVault

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        //AppContainer.dataStore = createDataStore(applicationContext)
        AppContainer.kVault = KVault(context = this)
        AppContainer.database = getRoomDatabase(getDatabaseBuilder(this))

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}