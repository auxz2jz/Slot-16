package com.auxz2jz.modularvision.diagnostics

import android.content.Context
import android.os.Build
import org.json.JSONObject
import java.io.File
import java.time.Instant
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

object DiagnosticExporter {
    data class ExportPackage(
        val file: File,
        val entries: List<String>
    )

    fun createPackage(context: Context, correlationId: String): ExportPackage {
        val operationId = "export-${System.currentTimeMillis()}"
        val started = android.os.SystemClock.elapsedRealtime()

        DiagnosticLogger.log(
            category = "EXPORT",
            event = "EXPORT_PACKAGE_STARTED",
            correlationId = correlationId,
            operationId = operationId
        )

        try {
            val exportDir = File(context.cacheDir, "diagnostic_exports").apply { mkdirs() }
            val safeVersion = DiagnosticLogger.appVersionName().replace(Regex("[^A-Za-z0-9._-]"), "_")
            val output = File(
                exportDir,
                "ModularVision-Diagnostics-v${safeVersion}-${System.currentTimeMillis()}.zip"
            )

            val planned = linkedMapOf<String, File>()
            planned["events.jsonl"] = DiagnosticLogger.currentLogFile()
            DiagnosticLogger.previousLogFile()?.let { planned["previous_events.jsonl"] = it }

            val diagDir = DiagnosticLogger.diagnosticsDirectory()
            val crash = File(diagDir, "last_crash.json")
            if (crash.exists() && crash.length() > 0L) planned["last_crash.json"] = crash

            val testResults = File(diagDir, "guided_test_results.json")
            if (testResults.exists() && testResults.length() > 0L) {
                planned["guided_test_results.json"] = testResults
            }

            val summary = File(exportDir, "summary.txt").apply {
                writeText(buildSummary())
            }
            val device = File(exportDir, "device_app_info.txt").apply {
                writeText(buildDeviceInfo())
            }
            val readme = File(exportDir, "README.txt").apply {
                writeText(
                    "Modular Vision diagnostics\n" +
                        "Generated locally. No upload was performed.\n" +
                        "Use summary.txt first, then events.jsonl for chronological evidence.\n"
                )
            }

            planned["README.txt"] = readme
            planned["summary.txt"] = summary
            planned["device_app_info.txt"] = device

            ZipOutputStream(output.outputStream().buffered()).use { zip ->
                for ((entryName, source) in planned) {
                    if (!source.exists() || !source.isFile) continue
                    zip.putNextEntry(ZipEntry(entryName))
                    source.inputStream().buffered().use { input -> input.copyTo(zip) }
                    zip.closeEntry()
                }
            }

            val entries = validate(output)
            if (output.length() <= 0L || "events.jsonl" !in entries || "summary.txt" !in entries) {
                throw IllegalStateException("Diagnostic ZIP validation failed.")
            }

            DiagnosticLogger.log(
                category = "EXPORT",
                event = "EXPORT_PACKAGE_COMPLETED",
                correlationId = correlationId,
                operationId = operationId,
                details = JSONObject()
                    .put("bytes", output.length())
                    .put("entryCount", entries.size)
                    .put("durationMs", android.os.SystemClock.elapsedRealtime() - started)
            )

            return ExportPackage(output, entries)
        } catch (t: Throwable) {
            DiagnosticLogger.recordError(
                module = "DiagnosticExporter",
                operation = "EXPORT_PACKAGE_FAILED",
                throwable = t,
                correlationId = correlationId,
                operationId = operationId
            )
            throw t
        }
    }

    private fun validate(file: File): List<String> {
        val names = mutableListOf<String>()
        ZipFile(file).use { zip ->
            val entries = zip.entries()
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                if (!entry.isDirectory) names += entry.name
            }
        }
        return names
    }

    private fun buildSummary(): String {
        val session = DiagnosticLogger.session
        val integrity = DiagnosticLogger.verifyFoundationIntegrity()
        return buildString {
            appendLine("Modular Vision Diagnostic Summary")
            appendLine("Generated UTC: ${Instant.now()}")
            appendLine("App version: ${DiagnosticLogger.appVersionName()}")
            appendLine("Version code: ${DiagnosticLogger.appVersionCode()}")
            appendLine("Session ID: ${session?.sessionId ?: "unavailable"}")
            appendLine("Active test: ${DiagnosticLogger.activeTestId ?: "none"}")
            appendLine("Active step: ${DiagnosticLogger.activeTestStepId ?: "none"}")
            appendLine("Log integrity: ${if (integrity.first) "PASS" else "FAIL"}")
            appendLine("Integrity detail: ${integrity.second}")
            appendLine("Last logger error: ${DiagnosticLogger.lastWriteError ?: "none"}")
        }
    }

    private fun buildDeviceInfo(): String {
        return buildString {
            appendLine("App: Modular Vision")
            appendLine("Version: ${DiagnosticLogger.appVersionName()} (${DiagnosticLogger.appVersionCode()})")
            appendLine("Android SDK: ${Build.VERSION.SDK_INT}")
            appendLine("Android release: ${Build.VERSION.RELEASE}")
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Architecture: ${Build.SUPPORTED_ABIS.joinToString()}")
        }
    }
}
