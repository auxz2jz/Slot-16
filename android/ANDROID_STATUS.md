# Android Status

- Phase: Foundation 0A
- Status: CANDIDATE — AWAITING USER VERIFICATION
- Version: v0.1.0
- Last user-verified baseline: NONE
- Latest candidate: v0.1.0
- Candidate source commit: `a7aaccb2c84bed577d0e71506a943228fffd5ca8`
- Build status: PASS — GitHub Actions run 36427037252
- Physical-device test status: NOT STARTED

## Candidate artifact

- APK: `ModularVision-Android-v0.1.0-debug.apk`
- APK size: 908,556 bytes
- APK SHA-256: `df051cb71d3dbf44b06be786746845d1c9f089ed277c9c62c8761d8dbdeba111`
- GitHub artifact ID: 10972165454
- GitHub artifact ZIP SHA-256: `cb37f48a511c3b7409a44858534416868c8bd25b0126881ab6b4d2804ca96f57`

## Implemented in this candidate

- app/session diagnostics initialization
- structured JSONL event logging
- correlation/test context
- bounded recent-event history and rolling log
- error logging and crash preservation with redaction
- validated local diagnostics ZIP generation
- visible **Test This Version**
- visible **Export Diagnostics**
- persistent guided-test state
- product-owned input/detector/tracker/ALPR/OCR engine contracts

## Not implemented yet

Camera/video/image input, motion analysis, AI detection, tracking execution, plate/OCR execution, zones/tripwires, event history/search, recording, and multi-camera scheduling are not yet implemented.

## Verification rule

The successful CI build proves compilation and artifact generation only. v0.1.0 remains CANDIDATE until the user physically tests it and confirms the guided test passes.
