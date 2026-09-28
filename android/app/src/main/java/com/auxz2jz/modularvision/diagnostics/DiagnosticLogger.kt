package com.auxz2jz.modularvision.diagnostics

import android.content.Context
import android.os.Build
import android.os.SystemClock
import org.json.JSONObject
import java.io.File
import java.time.Instant
import java.util.ArrayDeque
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

data class DiagnosticSession(
    val sessionId: String,
    val startedUtc: String,
    val startedElapsedMs: Long
)

object DiagnosticLogger {
    private const val MAX_LOG_BYTES = 2L * 1024L * 1024L
    private const val MAX_RECENT_EVENTS = 500

    private lateinit var appContext: Context
    private lateinit var diagnosticsDir: File
    private lateinit var currentLog: File
    private lateinit var previousLog: File

    private val sequence = AtomicLong(0)
    private val recentEvents = ArrayDeque<String>()
    private val writeLock = Any()

    @Volatile
    var session: DiagnosticSession? = null
        private set

    @Volatile
    var lastWriteError: String? = null
        private set

    @Volatile
    var activeTestSessionId: String? = null
        private set

    @Volatile
    var activeTestId: String? = null
        private set

    @Volatile
    var activeTestStepId: String? = null
        private set

    fun initialize(context: Context) {
        if (::appContext.isInitialized && session != null) return

        appContext = context.applicationContext
        diagnosticsDir = File(appContext.filesDir, "diagnostics").apply { mkdirs() }
        currentLog = File(diagnosticsDir, "events.jsonl")
        previousLog = File(diagnosticsDir, "previous_events.jsonl")

        rotateIfNeeded()

        session = DiagnosticSession(
            sessionId = UUID.randomUUID().toString(),
            startedUtc = Instant.now().toString(),
            startedElapsedMs = SystemClock.elapsedRealtime()
        )
        sequence.set(0)

        log(
            category = "SESSION",
            event = "SESSION_STARTED",
            details = JSONObject()
                .put("osSdk", Build.VERSION.SDK_INT)
                .put("appVersion", appVersionName())
                .put("versionCode", appVersionCode())
        )
    }

    fun isInitialized(): Boolean = ::appContext.isInitialized && session != null

    fun diagnosticsDirectory(): File {
        check(::diagnosticsDir.isInitialized) { "DiagnosticLogger is not initialized" }
        return diagnosticsDir
    }

    fun currentLogFile(): File {
        check(::currentLog.isInitialized) { "DiagnosticLogger is not initialized" }
        return currentLog
    }

    fun previousLogFile(): File? {
        if (!::previousLog.isInitialized) return null
        return previousLog.takeIf { it.exists() && it.length() > 0L }
    }

    fun setTestContext(testSessionId: String?, testId: String?, stepId: String?) {
        activeTestSessionId = testSessionId
        activeTestId = testId
        activeTestStepId = stepId
    }

    fun clearTestContext() {
        activeTestSessionId = null
        activeTestId = null
        activeTestStepId = null
    }

    fun log(
        category: String,
        event: String,
        severity: String = "INFO",
        correlationId: String? = null,
        operationId: String? = null,
        details: JSONObject = JSONObject()
    ): JSONObject {
        val activeSession = session ?: throw IllegalStateException("Diagnostic session is not initialized")
        val seq = sequence.incrementAndGet()
        val elapsedMs = (SystemClock.elapsedRealtime() - activeSession.startedElapsedMs).coerceAtLeast(0L)

        val json = JSONObject()
            .put("eventId", UUID.randomUUID().toString())
            .put("sequence", seq)
            .put("timestampUtc", Instant.now().toString())
            .put("elapsedMs", elapsedMs)
            .put("sessionId", activeSession.sessionId)
            .put("category", category)
            .put("severity", severity)
            .put("event", event)
            .put("appVersion", appVersionName())
            .put("versionCode", appVersionCode())

        putNullable(json, "correlationId", correlationId)
        putNullable(json, "operationId", operationId)
        putNullable(json, "testSessionId", activeTestSessionId)
        putNullable(json, "testId", activeTestId)
        putNullable(json, "testStepId", activeTestStepId)
        json.put("details", sanitize(details))

        val line = json.toString()

        synchronized(writeLock) {
            try {
                currentLog.appendText(line + "\n")
                addRecent(line)
                lastWriteError = null
            } catch (t: Throwable) {
                lastWriteError = "${t::class.java.simpleName}: ${t.message}"
            }
        }

        return json
    }

    fun recentSnapshot(): List<String> = synchronized(writeLock) {
        recentEvents.toList()
    }

    fun verifyFoundationIntegrity(): Pair<Boolean, String> {
        if (!isInitialized()) return false to "Diagnostic session is not initialized."
        val file = currentLogFile()
        if (!file.exists() || file.length() <= 0L) return false to "Event log is missing or empty."
        if (lastWriteError != null) return false to "Last log write failed: $lastWriteError"

        val currentSessionId = session?.sessionId ?: return false to "Session ID is missing."
        val lines = recentSnapshot().filter { it.isNotBlank() }
        if (lines.isEmpty()) return false to "No recent diagnostic events are available."

        var lastSequence = -1L
        var matched = 0
        try {
            for (line in lines) {
                val item = JSONObject(line)
                if (item.optString("sessionId") != currentSessionId) continue
                val seq = item.getLong("sequence")
                if (lastSequence >= 0L && seq <= lastSequence) {
                    return false to "Sequence numbers are not strictly increasing."
                }
                if (item.optString("timestampUtc").isBlank()) {
                    return false to "An event is missing its UTC timestamp."
                }
                if (item.optLong("elapsedMs", -1L) < 0L) {
                    return false to "An event has invalid monotonic elapsed time."
                }
                lastSequence = seq
                matched++
            }
        } catch (t: Throwable) {
            return false to "Could not validate diagnostic JSON: ${t.message}"
        }

        if (matched == 0) return false to "No events belong to the active session."
        return true to "Validated $matched active-session events."
    }

    fun recordError(
        module: String,
        operation: String,
        throwable: Throwable,
        correlationId: String? = null,
        operationId: String? = null
    ) {
        val stack = throwable.stackTraceToString()
        log(
            category = "ERROR",
            event = operation,
            severity = "ERROR",
            correlationId = correlationId,
            operationId = operationId,
            details = JSONObject()
                .put("module", module)
                .put("errorType", throwable::class.java.name)
                .put("message", sanitizeTextForPersistence(throwable.message ?: ""))
                .put("stackTrace", sanitizeTextForPersistence(stack))
        )
    }

    fun appVersionName(): String {
        if (!::appContext.isInitialized) return "unknown"
        val info = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        return info.versionName ?: "unknown"
    }

    @Suppress("DEPRECATION")
    fun appVersionCode(): Long {
        if (!::appContext.isInitialized) return -1L
        val info = appContext.packageManager.getPackageInfo(appContext.packageName, 0)
        return if (Build.VERSION.SDK_INT >= 28) info.longVersionCode else info.versionCode.toLong()
    }

    private fun rotateIfNeeded() {
        if (currentLog.exists() && currentLog.length() >= MAX_LOG_BYTES) {
            if (previousLog.exists()) previousLog.delete()
            currentLog.renameTo(previousLog)
        }
    }

    private fun addRecent(line: String) {
        while (recentEvents.size >= MAX_RECENT_EVENTS) {
            recentEvents.removeFirst()
        }
        recentEvents.addLast(line)
    }

    private fun putNullable(target: JSONObject, key: String, value: String?) {
        if (value == null) target.put(key, JSONObject.NULL) else target.put(key, value)
    }

    private fun sanitize(input: JSONObject): JSONObject {
        val output = JSONObject()
        val keys = input.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val value = input.opt(key)
            val lower = key.lowercase()
            if (
                lower.contains("password") ||
                lower.contains("token") ||
                lower.contains("credential") ||
                lower.contains("secret") ||
                lower.contains("preciselocation")
            ) {
                output.put(key, "[REDACTED]")
            } else if (value is String) {
                output.put(key, sanitizeTextForPersistence(value))
            } else {
                output.put(key, value)
            }
        }
        return output
    }

    fun sanitizeTextForPersistence(value: String): String {
        return value
            .replace(Regex("(?i)(password|token|secret|credential)=([^\\s&]+)"), "\$1=[REDACTED]")
            .take(16000)
    }
}
