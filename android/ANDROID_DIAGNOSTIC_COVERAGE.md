# Android Diagnostic Coverage Map

Reconciled against Android v0.1.0 candidate source commit `a7aaccb2c84bed577d0e71506a943228fffd5ca8`.

The current app has only Foundation 0A controls and automatic operations. Camera and AI controls do not exist yet and are therefore not listed as implemented coverage.

| Feature | Trigger/action | Request/state/result evidence | PASS requirement | Failure evidence | Guided test |
|---|---|---|---|---|---|
| App/session start | app/activity launch | SESSION_STARTED, ACTIVITY_CREATED | session ID exists and event log is writable | initialization/log exception | F0A-01 |
| Test This Version | button press | TEST_THIS_VERSION_PRESSED → GUIDED_TEST_STARTED → STEP_STARTED/PASSED/FAILED | objective F0A-01/F0A-02 checks succeed | STEP_FAILED / ERROR | F0A-01, F0A-02 |
| Guided-test restore | app reopened during active test | GUIDED_TEST_RESTORED with persisted step context | prior in-progress test context restored | missing/corrupt state or logging error | covered by persistent state design; expanded later |
| Structured event write | internal/user operation | JSONL event with session, sequence, UTC, elapsed | current-session events parse; sequence rises; elapsed non-negative | serialization/write/integrity failure | F0A-02 |
| Export package | Export Diagnostics button | EXPORT_DIAGNOSTICS_PRESSED → EXPORT_PACKAGE_STARTED → EXPORT_PACKAGE_COMPLETED | ZIP non-empty and required entries validate | EXPORT_PACKAGE_FAILED | F0A-03 |
| Save exported ZIP | Android document save result | SAVE_DESTINATION_REQUESTED → EXPORT_SAVE_COMPLETED | actual bytes written > 0 | EXPORT_SAVE_CANCELLED / EXPORT_SAVE_FAILED | F0A-03 |
| Manual test failure | Expected Behavior Failed | UI action + STEP_FAILED + GUIDED_TEST_FINISHED | tester-reported failure preserved with active step | missing test context/error | all active tests |
| Uncaught crash preservation | uncaught exception | ERROR/UNCAUGHT_EXCEPTION + local last_crash.json when possible | crash evidence written before normal handler delegation | best-effort preservation failure | destructive controlled test deferred |

## Privacy coverage

Diagnostic details pass through a redactor for credential-like fields/text. Crash message and stack persistence use the same sanitizer. Diagnostics stay local until the user explicitly exports them.

## Per-feature rule

Every future real control, background job, automatic operation, state transition, or output must update this map, its error path, PASS/FAIL criteria, and guided-test coverage.
