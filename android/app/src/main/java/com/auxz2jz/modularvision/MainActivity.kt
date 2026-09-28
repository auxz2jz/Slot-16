package com.auxz2jz.modularvision

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.auxz2jz.modularvision.diagnostics.CrashPreserver
import com.auxz2jz.modularvision.diagnostics.DiagnosticExporter
import com.auxz2jz.modularvision.diagnostics.DiagnosticLogger
import com.auxz2jz.modularvision.testing.GuidedTestController
import org.json.JSONObject
import java.io.File
import java.util.UUID

class MainActivity : Activity() {
    private var pendingFile: File? = null
    private var pendingCorrelation: String? = null
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DiagnosticLogger.initialize(applicationContext)
        CrashPreserver.install(applicationContext)
        GuidedTestController.restore(this)
        DiagnosticLogger.log("APP", "ACTIVITY_CREATED")
        setContentView(makeUi())
    }

    private fun makeUi(): ScrollView {
        val scroll = ScrollView(this)
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(24), dp(24), dp(24))
        }
        box.addView(TextView(this).apply {
            text = "Modular Vision"
            textSize = 26f
        })
        box.addView(TextView(this).apply {
            text = "Android Foundation v${DiagnosticLogger.appVersionName()}\n\nFoundation 0A contains diagnostics, guided testing, and modular engine interfaces. Camera and AI features are not implemented yet."
            textSize = 16f
        })
        box.addView(Button(this).apply {
            text = "Test This Version"
            setOnClickListener { startTest() }
        })
        box.addView(Button(this).apply {
            text = "Export Diagnostics"
            setOnClickListener { startExport() }
        })
        status = TextView(this).apply {
            text = GuidedTestController.summary()
            textSize = 15f
            setPadding(0, dp(20), 0, 0)
        }
        box.addView(status)
        scroll.addView(
            box,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        )
        return scroll
    }

    private fun startTest() {
        val id = UUID.randomUUID().toString()
        DiagnosticLogger.log("UI_ACTION", "TEST_THIS_VERSION_PRESSED", correlationId = id)
        val message = GuidedTestController.start(this)
        status.text = message
        AlertDialog.Builder(this)
            .setTitle("Test This Version")
            .setMessage(message)
            .setPositiveButton("Continue", null)
            .setNegativeButton("Expected Behavior Failed") { _, _ ->
                status.text = GuidedTestController.manualFail(
                    this,
                    "Tester reported that expected behavior failed."
                )
            }
            .show()
    }

    private fun startExport() {
        val id = UUID.randomUUID().toString()
        DiagnosticLogger.log("UI_ACTION", "EXPORT_DIAGNOSTICS_PRESSED", correlationId = id)
        try {
            val pkg = DiagnosticExporter.createPackage(this, id)
            pendingFile = pkg.file
            pendingCorrelation = id
            DiagnosticLogger.log(
                "EXPORT",
                "SAVE_DESTINATION_REQUESTED",
                correlationId = id,
                details = JSONObject().put("packageBytes", pkg.file.length())
            )
            startActivityForResult(
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/zip"
                    putExtra(Intent.EXTRA_TITLE, pkg.file.name)
                },
                4101
            )
        } catch (t: Throwable) {
            DiagnosticLogger.recordError("MainActivity", "EXPORT_PREPARE_FAILED", t, id)
            status.text = GuidedTestController.exportFailed(this, "Could not prepare diagnostics.")
        }
    }

    @Deprecated("Foundation file-save workflow")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != 4101) return
        val id = pendingCorrelation
        val source = pendingFile

        if (resultCode != RESULT_OK || data?.data == null) {
            DiagnosticLogger.log("EXPORT", "EXPORT_SAVE_CANCELLED", "WARNING", id)
            status.text = GuidedTestController.exportFailed(this, "Diagnostics save was cancelled.")
            clearPending()
            return
        }

        try {
            require(source != null && source.exists() && source.length() > 0L)
            val bytes = contentResolver.openOutputStream(data.data!!)?.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            } ?: 0L
            require(bytes > 0L)
            DiagnosticLogger.log(
                "EXPORT",
                "EXPORT_SAVE_COMPLETED",
                correlationId = id,
                details = JSONObject().put("bytesWritten", bytes)
            )
            status.text = GuidedTestController.exportSucceeded(this, bytes)
        } catch (t: Throwable) {
            DiagnosticLogger.recordError("MainActivity", "EXPORT_SAVE_FAILED", t, id)
            status.text = GuidedTestController.exportFailed(this, "Diagnostics save failed.")
        } finally {
            clearPending()
        }
    }

    private fun clearPending() {
        pendingFile = null
        pendingCorrelation = null
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
