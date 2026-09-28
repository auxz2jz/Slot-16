# Android Project Memory

## Identity

- Product: Modular AI Vision & Tracking System
- Repository: auxz2jz/Slot-16
- Platform owner: Android
- Package/application ID: com.auxz2jz.modularvision
- Project type: cross-platform
- Windows status: NOT STARTED

## Mandatory startup

Read `auxz2jz/master-instruction-library/INSTRUCTION_INDEX.md`, all mandatory files it references, and the Cross-Platform Collaboration Standard before development. Emergency recovery commands activate only when their stated condition occurs or the user explicitly issues one.

## Current state

- Android phase: Foundation 0A
- Android status: CANDIDATE — AWAITING USER VERIFICATION
- Current candidate: v0.1.0
- Last user-verified baseline: NONE
- Candidate source commit: `a7aaccb2c84bed577d0e71506a943228fffd5ca8`
- Candidate source tree: `7b61862a9341212e5ac6ae04ea8e1bb6d52bc375`
- CI run: 36427037252 — SUCCESS
- Artifact ID: 10972165454
- APK SHA-256: `df051cb71d3dbf44b06be786746845d1c9f089ed277c9c62c8761d8dbdeba111`
- Artifact ZIP SHA-256: `cb37f48a511c3b7409a44858534416868c8bd25b0126881ab6b4d2804ca96f57`

## Product direction

Build a modular local AI surveillance/computer-vision platform for cameras, video, and images. The long-term product includes motion/preprocessing, object detection, persistent tracking, plate recognition, optional face recognition, zones/tripwires, event intelligence, history/search, snapshots/clips, engine health, confidence-based multi-engine verification/fallback, and reconstructable decision diagnostics.

The application owns the workflow. Third-party engines stay behind product-owned adapters and normalized result models.

## Foundation 0A implemented

Actual user-facing controls:
- **Test This Version**
- **Export Diagnostics**

Actual automatic/background behavior:
- diagnostic session initialization on app start
- persistent JSONL event writes
- bounded recent event buffer
- log rotation
- guided-test persistence/restore
- diagnostics package generation/validation
- uncaught-crash preservation followed by normal Android crash handling

Product-owned contracts now exist for:
- FrameSource
- DetectorEngine
- TrackerEngine
- AlprEngine
- OcrEngine
- normalized Detection, Track, PlateObservation, FrameRef, EngineResult

## Guided test

Test ID: `android_foundation_0a_v1`

- F0A-01 objectively verifies the diagnostic session/log is writable.
- F0A-02 objectively validates structured event integrity.
- F0A-03 requires an actual non-zero diagnostics ZIP save; merely opening the save picker cannot pass.

The user can report **Expected Behavior Failed** during testing.

## Build history / first real failures

1. Early workflow runs failed before jobs started because the SDK shell command was written as invalid YAML.
2. An attempted text replacement did not alter the actual workflow because it matched escaped newline text rather than real line breaks.
3. The workflow was then replaced directly with the known-working Slot-14 block-scalar pattern plus the Android subdirectory. Run 5 built successfully.
4. Before checkpointing, crash persistence was found to bypass the standard diagnostic redactor. The smallest correction routed crash message/stack text through the same sanitizer.
5. Run 6 after that privacy correction completed successfully and is the retained v0.1.0 candidate.

## Known limitations

- No camera or AI engine is integrated yet.
- Foundation storage currently covers diagnostics and guided-test state; the broader SQLite/Room event/configuration persistence layer remains a Foundation 0A task.
- The export used to complete F0A-03 is assembled before the final PASS is recorded. The UI tells the tester to export diagnostics once more if a package containing the completed test-result record is desired.
- Uncaught-crash preservation exists but its destructive controlled crash test is deferred.

## Exact next action

The user physically installs and tests v0.1.0:
1. launch the app;
2. tap **Test This Version**;
3. allow F0A-01/F0A-02 automatic checks to complete;
4. tap **Export Diagnostics** and actually save the ZIP;
5. confirm the app reports **Foundation test PASS**.

If it passes, record v0.1.0 as the first Android VERIFIED baseline, then finish remaining Foundation 0A persistence/configuration seams before Phase 0B input work. If it fails, inspect the exported diagnostics and fix the first real failure only.
