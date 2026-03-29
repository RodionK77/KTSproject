package com.github.rodionk77.common

import androidx.compose.runtime.Composable

data class PickedFile(val name: String, val bytes: ByteArray)

expect class FilePickerLauncher {
    fun launch()
}

@Composable
expect fun rememberFilePicker(onFilePicked: (PickedFile?) -> Unit): FilePickerLauncher
