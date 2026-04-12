package com.github.rodionk77.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readBytes
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIModalPresentationFormSheet
import platform.UniformTypeIdentifiers.UTTypeItem
import platform.darwin.NSObject

actual class FilePickerLauncher(private val onLaunch: () -> Unit) {
    actual fun launch() = onLaunch()
}

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberFilePicker(onFilePicked: (PickedFile?) -> Unit): FilePickerLauncher {
    val callback = remember { onFilePicked }
    return remember {
        FilePickerLauncher {
            val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                override fun documentPicker(
                    controller: UIDocumentPickerViewController,
                    didPickDocumentsAtURLs: List<*>
                ) {
                    val url = didPickDocumentsAtURLs.firstOrNull() as? NSURL
                        ?: run { callback(null); return }
                    val data = NSData.dataWithContentsOfURL(url)
                        ?: run { callback(null); return }
                    val bytes = data.bytes?.readBytes(data.length.toInt())
                        ?: run { callback(null); return }
                    callback(PickedFile(url.lastPathComponent ?: "file", bytes))
                }

                override fun documentPickerWasCancelled(
                    controller: UIDocumentPickerViewController
                ) {
                    callback(null)
                }
            }
            val picker = UIDocumentPickerViewController(
                forOpeningContentTypes = listOf(UTTypeItem),
                asCopy = true
            )
            picker.delegate = delegate
            picker.allowsMultipleSelection = false
            picker.modalPresentationStyle = UIModalPresentationFormSheet
            UIApplication.sharedApplication.keyWindow?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}
