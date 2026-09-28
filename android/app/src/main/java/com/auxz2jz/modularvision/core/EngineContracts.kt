package com.auxz2jz.modularvision.core

enum class EngineRole {
    PRIMARY,
    SECONDARY,
    FALLBACK,
    TESTING_ONLY,
    DISABLED
}

enum class EngineResultStatus {
    SUCCESS,
    NO_RESULT,
    LOW_CONFIDENCE,
    REJECTED,
    TIMEOUT,
    UNAVAILABLE,
    FAILED,
    CANCELLED
}

data class EngineDescriptor(
    val engineId: String,
    val displayName: String,
    val version: String?,
    val role: EngineRole
)

data class FrameRef(
    val sourceId: String,
    val frameId: Long,
    val timestampEpochMs: Long,
    val width: Int,
    val height: Int
)

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

data class Detection(
    val detectionId: String,
    val frameId: Long,
    val objectClass: String,
    val boundingBox: BoundingBox,
    val confidence: Float,
    val detectorEngineId: String,
    val modelId: String?
)

data class Track(
    val trackId: String,
    val objectClass: String,
    val firstSeenEpochMs: Long,
    val lastSeenEpochMs: Long,
    val currentBox: BoundingBox,
    val trackerEngineId: String
)

data class PlateObservation(
    val observationId: String,
    val vehicleTrackId: String?,
    val frameId: Long,
    val candidateText: String?,
    val confidence: Float,
    val engineId: String
)

data class EngineResult<T>(
    val status: EngineResultStatus,
    val engineId: String,
    val operationId: String,
    val durationMs: Long,
    val value: T? = null,
    val message: String? = null
)

interface FrameSource {
    val sourceId: String
    fun start(operationId: String, callback: (EngineResult<FrameRef>) -> Unit)
    fun stop(operationId: String)
}

interface DetectorEngine {
    val descriptor: EngineDescriptor
    fun detect(frame: FrameRef, operationId: String, callback: (EngineResult<List<Detection>>) -> Unit)
}

interface TrackerEngine {
    val descriptor: EngineDescriptor
    fun update(frame: FrameRef, detections: List<Detection>, operationId: String, callback: (EngineResult<List<Track>>) -> Unit)
}

interface AlprEngine {
    val descriptor: EngineDescriptor
    fun readPlate(frame: FrameRef, vehicleTrack: Track?, operationId: String, callback: (EngineResult<List<PlateObservation>>) -> Unit)
}

interface OcrEngine {
    val descriptor: EngineDescriptor
    fun readText(frame: FrameRef, operationId: String, callback: (EngineResult<String>) -> Unit)
}
