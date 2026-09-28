# Android Roadmap

## Phase 0A — Foundation
Status: CANDIDATE v0.1.0 BUILT — USER VERIFICATION PENDING

Completed in v0.1.0 candidate:
- [x] durable project memory/checkpoint/status
- [x] Android project scaffold
- [x] diagnostic session system
- [x] JSONL structured event logger
- [x] bounded recent-event history and log rotation
- [x] central error logging
- [x] crash preservation with redaction
- [x] explicit Export Diagnostics
- [x] guided Test This Version framework
- [x] normalized core data models
- [x] input/detector/tracker/plate/OCR interfaces
- [x] CI build and APK artifact
- [ ] physical user verification of v0.1.0
- [ ] broader SQLite/Room event/configuration persistence seams
- [ ] polish completed-test diagnostics export so the first saved package can contain final PASS state

## Phase 0B — Input foundation

- image input
- prerecorded video input
- frame abstraction/timestamps
- safe input metadata
- decode/error diagnostics
- input guided tests

## Phase 0C — Live input

- supported local/USB camera input
- RTSP/IP source
- reconnect/backoff
- stream-health metrics
- stall detection
- live input guided tests

## Phase 1 — Primary object detector

- integrate one detector through DetectorEngine
- normalize person/vehicle/animal classes
- configurable confidence/fallback thresholds
- performance and health metrics
- detection diagnostics and guided tests

## Phase 2 — Tracking

- first tracker adapter
- stable track IDs
- lifecycle/lost-track handling
- objective plus human tracking verification

## Phase 3 — Zones / tripwires

- polygon zones
- line crossing
- direction
- dwell timing

## Phase 4 — Plate/OCR pipeline

- vehicle association
- plate detection
- OCR
- confidence/history

## Phase 5 — Multi-frame consensus

- best-frame selection
- multiple observations
- character/frame voting
- enhancement fallback
- consensus evidence

## Phase 6 — Secondary/fallback AI

- secondary detector adapters
- fallback policy
- engine manager roles
- engine health/failover

## Phase 7 — Advanced tracking

- advanced tracker/ReID candidates
- occlusion recovery
- advanced association tests

## Phase 8 — Event intelligence

- loitering
- wrong-way movement
- entry/exit
- stop events
- object left/removed where reliable

## Phase 9 — Full UI/history

- live sources
- timeline/events
- object and plate history views
- search
- AI engine management
- diagnostics
- settings

## Phase 10 — Optimization

Only after behavior is correct:
- batching
- runtime/accelerator selection
- GPU/NPU acceleration where supported
- frame skipping
- ROI processing
- dynamic resolution
- multi-camera scheduling

Every phase must update diagnostics and guided tests for the actual features added.
