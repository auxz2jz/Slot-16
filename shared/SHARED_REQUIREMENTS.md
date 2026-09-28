# Shared Requirements

- Local-first operation where practical; no silent diagnostic or media upload.
- Third-party AI components remain behind product-owned interfaces.
- Engine-specific outputs are normalized before shared decision logic uses them.
- Important asynchronous work uses session/correlation/operation IDs.
- A UI action alone never proves success; verify the intended result.
- Important AI decisions retain safe evidence about source, engine, confidence, fallback, association, and result.
- Never commit or persist credentials, auth tokens, secret stream URLs, precise location, or unrelated personal data in diagnostics.
- Optional engine failures should be isolated; configured fallbacks may continue.
- Android and Windows maintain separate candidates, tests, versions, artifacts, and user-verified baselines.
- Recheck current source/model licensing before integrating or distributing third-party components.
