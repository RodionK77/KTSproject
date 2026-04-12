package com.github.rodionk77

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

class CrashlyticsAntilog : Antilog() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun performLog(
        priority: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: String?
    ) {
        val prefix = "[${priority.name}]${tag?.let { " [$it]" } ?: ""}"

        message?.let {
            crashlytics.log("$prefix $it")
        }

        if (priority == LogLevel.ERROR || priority == LogLevel.ASSERT) {
            throwable?.let {
                crashlytics.recordException(it)
            } ?: message?.let {
                crashlytics.recordException(RuntimeException("$prefix $it"))
            }
        }
    }
}