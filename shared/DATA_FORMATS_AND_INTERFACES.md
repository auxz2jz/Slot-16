# Shared Data and Interface Concepts

Platform-neutral concepts include:

- Detection: source/frame, timestamp, class, bounding box, confidence, engine/model ID, operation ID.
- Track: track ID, class, first/last seen, current box/source, tracker ID, zone/direction/association metadata.
- Engine result: engine ID, operation ID, status, duration, result metadata, and error information.
- Plate observation: vehicle/frame association, candidate text, engine, confidence, timestamp.
- Plate consensus: contributing observations, disagreement information, final/unresolved value, confidence, reason.
- Diagnostic event: session, sequence, UTC/elapsed time, correlation/operation/test IDs, category, request/state/result/error, timing, app version/build.

If both platforms later exchange saved data, add explicit schema versions and migration rules before depending on interoperability.
