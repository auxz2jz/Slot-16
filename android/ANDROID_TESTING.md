# Android Guided Testing

The Master Guided Testing Standard is mandatory.

## Status rule

A successful CI build creates a CANDIDATE only. Only the user's physical testing can establish the Android LAST VERIFIED BASELINE.

## Foundation test

Test ID: `android_foundation_0a_v1`

The final step labels must match the implemented UI exactly.

### Step F0A-01 — Diagnostic session

WHAT TO DO: Launch the app and start **Test This Version**.

EXPECTED RESULT:
- app remains running;
- diagnostic session exists;
- session-start/test-start events are persisted;
- test session has a unique ID.

PASS requires objective log/session evidence.

### Step F0A-02 — Structured event integrity

Automatic verification checks:
- sequence numbers advance;
- session ID remains consistent;
- UTC timestamps are present;
- monotonic elapsed values are non-negative;
- no relevant logging error occurred.

### Step F0A-03 — Export Diagnostics

WHAT TO DO: Use **Export Diagnostics** and save the package.

EXPECTED RESULT:
- export operation starts;
- a non-empty ZIP is created;
- expected summary/event files exist;
- final save succeeds;
- completion is logged.

Opening a save picker alone is not a PASS.

### Manual failure

The guided test must provide **Expected Behavior Failed** or equivalent so a visible problem can be recorded with current step and diagnostics.

Future camera, decoding, AI, tracking, OCR, zone, recording, and search tests are added only when those features actually exist.
