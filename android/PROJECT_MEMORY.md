# Android Project Memory

## Identity

- Product: Modular AI Vision & Tracking System
- Repository: auxz2jz/Slot-16
- Platform owner: Android
- Planned package/application ID: com.auxz2jz.modularvision

## Master rules

Read `auxz2jz/master-instruction-library/INSTRUCTION_INDEX.md` first, then all mandatory files it references. This project is cross-platform, so the Cross-Platform Collaboration Standard is mandatory.

Emergency recovery commands are conditional. Do not execute them unless their stated trigger occurs or the user explicitly issues one.

## Current state

- Project type: brand-new cross-platform project
- Android current phase: Foundation 0A
- Android current status: PLANNED / initialization in progress
- Android last verified baseline: NONE
- Android latest candidate: NONE
- Windows status: NOT STARTED
- Existing verified Android source: NONE

## User-approved product direction

Build a modular local AI surveillance/computer-vision platform that accepts cameras/video/images and eventually supports motion detection, object detection, persistent tracking, ALPR, optional face recognition, zones/tripwires, event intelligence, history/search, recording/snapshots, engine fallback, and detailed decision diagnostics.

The program owns the workflow. Third-party engines sit behind adapters and normalized product data models.

## Current task

Foundation 0A:

1. establish durable project/shared/platform documentation;
2. checkpoint the empty starting state;
3. create Android project scaffold using a toolchain already proven in the user's recent Android repositories;
4. implement diagnostics foundation before sophisticated AI;
5. create product-owned engine/data interfaces;
6. add a minimal Test This Version workflow;
7. build via GitHub Actions;
8. record build result as candidate only;
9. await physical user verification before establishing a verified baseline.

## Proven Android build pattern selected

Reference: auxz2jz/Slot-14 latest successful Android CI run on 2026-09-26.

Pattern:

- compile/target SDK 36
- JDK 17
- Android Gradle Plugin 9.4.0
- Gradle 9.6.0
- GitHub Actions installs Android 36 SDK/build-tools

This is a build-pattern reference only; Slot-14 application code is not being copied into this project.

## Planned Foundation 0A controls/workflows

The first Android candidate should contain only controls justified by foundation functionality:

- Test This Version
- Export Diagnostics
- simple status/about information

No fake camera, AI, tracking, ALPR, or recording controls should be added before those subsystems exist.

## Architecture decisions

- Android code stays under `android/`.
- Windows code is protected and not owned by this worker.
- Shared product information stays under `shared/`.
- Core contracts are product-owned.
- Diagnostics are permanent infrastructure.
- Guided testing is permanent infrastructure.
- No camera credentials or tokens are committed.
- No third-party AI source is merged into the core during Foundation 0A.

## Known bugs

None; no Android candidate exists yet.

## Failed approaches

- A single large batched documentation write was blocked by the connector safety guard. No repository damage occurred. Switched to smaller explicit writes.

## Files/results received

- User supplied the full project concept and requested Master Instruction Library compliance.
- Slot-16 confirmed empty before initialization.
- Slot-14 latest CI build confirmed successful and provides the selected starting toolchain pattern.

## Exact next action

Create the remaining Foundation 0A documentation, then add the Android scaffold and diagnostics core. Build it with GitHub Actions and record the result without calling it user-verified.
