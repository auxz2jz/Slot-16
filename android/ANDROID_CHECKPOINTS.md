# Android Checkpoints

## CP-000 — Empty repository / pre-code state

**Status:** RECORDED STARTING STATE — not a verified application baseline  
**Repository:** auxz2jz/Slot-16  
**Initial inspection:** repository size 0; no source/files  
**Initialization commit:** `86d0f3174a1b1c9f1e787c6b47099a89a88be67c`  
**Android application version:** none  
**Candidate:** none  
**User-verified baseline:** none  

This checkpoint protects the known empty starting state and the decision to use separate shared/Android/Windows ownership areas.

---

## CP-001 — Android Foundation v0.1.0

**Status:** CANDIDATE — BUILD PASSED / USER TEST PENDING  
**Version:** v0.1.0  
**Last user-verified baseline:** NONE  
**Exact build source commit:** `a7aaccb2c84bed577d0e71506a943228fffd5ca8`  
**Source tree:** `7b61862a9341212e5ac6ae04ea8e1bb6d52bc375`  
**GitHub Actions run:** 36427037252 — SUCCESS  
**Artifact ID:** 10972165454  
**Artifact ZIP SHA-256:** `cb37f48a511c3b7409a44858534416868c8bd25b0126881ab6b4d2804ca96f57`  
**APK:** `ModularVision-Android-v0.1.0-debug.apk`  
**APK size:** 908,556 bytes  
**APK SHA-256:** `df051cb71d3dbf44b06be786746845d1c9f089ed277c9c62c8761d8dbdeba111`

### What this build proves

- Android source compiled.
- APK was produced.
- GitHub Actions uploaded the artifact.

It does **not** prove the app works correctly on the user's device and does not create a VERIFIED baseline.

### Candidate functionality

- diagnostic session and structured JSONL logging
- error/crash preservation and privacy redaction
- diagnostic ZIP generation and validation
- guided Foundation test with persistent state
- Test This Version control
- Export Diagnostics control
- modular product-owned engine/data contracts

### First real build failure found and fixed

Initial workflow YAML used a quoted SDK command in a form GitHub could not parse, producing failed runs with no jobs. The valid block-scalar workflow pattern was applied; subsequent builds ran normally.

### User verification still required

The user must install v0.1.0 and complete `android_foundation_0a_v1`. Only an explicit successful user result may promote this checkpoint to the LAST VERIFIED BASELINE.

### Exact next action

Physical v0.1.0 guided test. On failure, export/share diagnostics and locate the first abnormal event. On success, record v0.1.0 VERIFIED before continuing Foundation 0A.
