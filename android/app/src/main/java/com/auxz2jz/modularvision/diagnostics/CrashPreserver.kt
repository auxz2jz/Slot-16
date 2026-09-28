package com.auxz2jz.modularvision.diagnostics

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.Instant

object CrashPreserver {
    @Volatile
    private var installed = false

    fun install(context: Context) {
        if (installed) return
        installed = true

        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                if (DiagnosticLogger.isInitialized()) {
                    DiagnosticLogger.recordError(
                        module = "CrashPreserver",
                        operation = "UNCAUGHT_EXCEPTION",
                        throwable = throwable
                    )

                    val recent = JSONArray()
                    DiagnosticLogger.recentSnapshot().takeLast(100).forEach { recent.put(it) }

                    val crash = JSONObject()
                        .put("timestampUtc", Instant.now().toString())
                        .put("sessionId", DiagnosticLogger.session?.sessionId)
                        .put("thread", thread.name)
                        .put("errorType", throwable::class.java.name)
                        .put("message", throwable.message ?: "")
                        .put("stackTrace", throwable.stackTraceToString())
                        .put("appVersion", DiagnosticLogger.appVersionName())
                        .put("versionCode", DiagnosticLogger.appVersionCode())
                        .put("testSessionId", DiagnosticLogger.activeTestSessionId)
                        .put("testId", DiagnosticLogger.activeTestId)
                        .put("testStepId", DiagnosticLogger.activeTestStepId)
                        .put("recentEvents", recent)

                    File(DiagnosticLogger.diagnosticsDirectory(), "last_crash.json")
                        .writeText(crash.toString(2))
                }
            } catch (_: Throwable) {
                // Crash preservation is best-effort; normal Android crash handling must continue.
            } finally {
                previous?.uncaughtException(thread, throwable)
            }
        }
    }
}
