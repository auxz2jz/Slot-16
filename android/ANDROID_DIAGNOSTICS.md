# Android Diagnostics

The Master Diagnostics Standard is mandatory.

## Foundation requirements

Each app session records a UUID session ID, UTC start time, monotonic elapsed origin, sequence counter, app version/build, and active guided-test context.

Important behavior is separated into:

USER_ACTION / TRIGGER → OPERATION_REQUEST → STATE / PROGRESS → OPERATION_RESULT or ERROR → TEST_RESULT

A UI action alone never proves success.

## Structured logging

Primary chronological format: JSONL.

Foundation fields include event ID, sequence, UTC timestamp, elapsed milliseconds, session ID, correlation/operation IDs, test IDs, category, severity, module/control, event name, useful state/result data, duration, and error information.

## Persistence and retention

- bounded persistent current log
- rotated previous log
- bounded recent-event buffer
- prompt persistence for important state changes, errors, test results, and crash markers
- no unlimited growth

## Error/crash preservation

Important caught failures retain type, message, stack trace, operation/test context, and recent events when useful.

An uncaught-exception handler attempts to preserve a last-crash record, then delegates to normal Android crash handling.

## Privacy

Diagnostics stay local until explicitly exported. Do not persist credentials, tokens, secret stream URLs, precise location, or unrelated personal information.

## Export

The visible **Export Diagnostics** action records its own request and result. PASS requires a non-empty package to be written successfully; opening a file picker is not success.

## Future AI coverage

When AI subsystems exist, diagnostics must preserve the engine/model, input/frame reference, normalized result, confidence, fallback/fusion reason, tracking association, timing, final result, and first abnormal stage.
