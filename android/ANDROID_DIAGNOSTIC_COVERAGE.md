# Android Diagnostic Coverage Map

This file tracks actual important behavior. At CP-000 there was no Android application, so there were no existing controls or background jobs to instrument.

Planned Foundation 0A coverage must be reconciled with the implemented source before the first candidate is considered complete.

| Feature | Trigger | Software request | Success evidence | Failure evidence | Test |
|---|---|---|---|---|---|
| App/session start | Activity/process launch | initialize diagnostics | session created and SESSION_STARTED persisted | initialization exception or missing log | F0A-01 |
| Test This Version | user button | start guided test session | test ID/session/step persisted | controller/session failure | F0A-01/F0A-02 |
| Export Diagnostics | user button | create and save ZIP | ZIP non-empty and save completes | export/copy/validation error | F0A-03 |
| Event write | internal operation | append JSONL event | event persists with valid sequence/session | serialization/write failure | F0A-02 |
| Uncaught crash preservation | uncaught exception | persist crash evidence then delegate | crash file written when possible | crash preservation failure | controlled test deferred |

## Rule

Whenever a real button, menu, navigation path, background worker, automatic operation, file operation, state transition, or output is added, update this map with request/result evidence and guided-test coverage.
