package com.auxz2jz.modularvision.testing

import android.content.Context
import com.auxz2jz.modularvision.diagnostics.DiagnosticLogger
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.Instant
import java.util.UUID

object GuidedTestController {
    private const val TEST_ID = "android_foundation_0a_v1"
    private const val PREFS = "guided_test"
    private var testSessionId: String? = null
    private var currentStep: String? = null
    private var overall = "NOT_RUN"
    private val results = JSONArray()

    fun restore(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (p.getString("overall", "") != "IN_PROGRESS") return
        testSessionId = p.getString("session", null)
        currentStep = p.getString("step", null)
        overall = "IN_PROGRESS"
        DiagnosticLogger.setTestContext(testSessionId, TEST_ID, currentStep)
        DiagnosticLogger.log("TEST", "GUIDED_TEST_RESTORED")
    }

    fun start(context: Context): String {
        testSessionId = UUID.randomUUID().toString()
        currentStep = "F0A-01"
        overall = "IN_PROGRESS"
        clearResults()
        DiagnosticLogger.setTestContext(testSessionId, TEST_ID, currentStep)
        DiagnosticLogger.log("TEST", "GUIDED_TEST_STARTED")

        DiagnosticLogger.log("TEST", "STEP_STARTED", details = JSONObject().put("stepId", "F0A-01"))
        DiagnosticLogger.log("DIAGNOSTIC", "FOUNDATION_WRITE_PROBE")
        val logOk = DiagnosticLogger.session != null &&
            DiagnosticLogger.currentLogFile().exists() &&
            DiagnosticLogger.currentLogFile().length() > 0L &&
            DiagnosticLogger.lastWriteError == null

        if (!logOk) return fail(context, "F0A-01", "Diagnostic log was not writable.", "AUTO_FAIL")
        pass("F0A-01", "Diagnostic session exists and event log is writable.")

        currentStep = "F0A-02"
        DiagnosticLogger.setTestContext(testSessionId, TEST_ID, currentStep)
        DiagnosticLogger.log("TEST", "STEP_STARTED", details = JSONObject().put("stepId", "F0A-02"))
        val integrity = DiagnosticLogger.verifyFoundationIntegrity()
        if (!integrity.first) return fail(context, "F0A-02", integrity.second, "AUTO_FAIL")
        pass("F0A-02", integrity.second)

        currentStep = "F0A-03"
        DiagnosticLogger.setTestContext(testSessionId, TEST_ID, currentStep)
        DiagnosticLogger.log(
            "TEST",
            "STEP_STARTED",
            details = JSONObject()
                .put("stepId", "F0A-03")
                .put("expected", "Diagnostics ZIP is created and actually saved.")
        )
        persist(context)
        saveResults()
        return "Automatic checks passed. Tap Export Diagnostics and save the ZIP. Opening the save picker alone will not pass the test."
    }

    fun awaitingExport(): Boolean = overall == "IN_PROGRESS" && currentStep == "F0A-03"

    fun exportSucceeded(context: Context, bytesSaved: Long): String {
        if (!awaitingExport()) return "Diagnostics saved."
        if (bytesSaved <= 0L) return fail(context, "F0A-03", "Saved file contained zero bytes.", "AUTO_FAIL")

        pass("F0A-03", "Diagnostics ZIP saved successfully ($bytesSaved bytes).")
        overall = "PASS"
        DiagnosticLogger.log("TEST", "GUIDED_TEST_FINISHED", details = JSONObject().put("overall", "PASS"))
        persist(context)
        saveResults()
        DiagnosticLogger.clearTestContext()
        return "Foundation test PASS. Export Diagnostics again if you want a ZIP containing the final completed test-result record."
    }

    fun exportFailed(context: Context, reason: String): String {
        if (!awaitingExport()) return reason
        return fail(context, "F0A-03", reason, "AUTO_FAIL")
    }

    fun manualFail(context: Context, note: String): String {
        val step = currentStep ?: "UNKNOWN"
        if (overall != "IN_PROGRESS") return "No guided test is active."
        return fail(context, step, note, "MANUAL_FAIL")
    }

    fun summary(): String = "Test $TEST_ID — $overall — step ${currentStep ?: "none"}"

    private fun pass(step: String, message: String) {
        results.put(result(step, "PASS", "AUTO_VERIFIED", message))
        DiagnosticLogger.log(
            "TEST",
            "STEP_PASSED",
            details = JSONObject().put("stepId", step).put("resultSource", "AUTO_VERIFIED")
        )
        saveResults()
    }

    private fun fail(context: Context, step: String, message: String, source: String): String {
        results.put(result(step, "FAIL", source, message))
        overall = "FAIL"
        currentStep = step
        DiagnosticLogger.log(
            "TEST",
            "STEP_FAILED",
            severity = "ERROR",
            details = JSONObject().put("stepId", step).put("resultSource", source).put("reason", message)
        )
        DiagnosticLogger.log("TEST", "GUIDED_TEST_FINISHED", severity = "ERROR", details = JSONObject().put("overall", "FAIL"))
        persist(context)
        saveResults()
        DiagnosticLogger.clearTestContext()
        return "Foundation test FAIL at $step: $message"
    }

    private fun result(step: String, status: String, source: String, message: String) =
        JSONObject()
            .put("stepId", step)
            .put("result", status)
            .put("resultSource", source)
            .put("message", message)
            .put("timestampUtc", Instant.now().toString())

    private fun persist(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString("session", testSessionId)
            .putString("step", currentStep)
            .putString("overall", overall)
            .apply()
    }

    private fun saveResults() {
        if (!DiagnosticLogger.isInitialized()) return
        val root = JSONObject()
            .put("testId", TEST_ID)
            .put("testSessionId", testSessionId)
            .put("currentStep", currentStep)
            .put("overallStatus", overall)
            .put("completed", overall != "IN_PROGRESS")
            .put("stepResults", results)
        File(DiagnosticLogger.diagnosticsDirectory(), "guided_test_results.json").writeText(root.toString(2))
    }

    private fun clearResults() {
        while (results.length() > 0) results.remove(0)
    }
}
