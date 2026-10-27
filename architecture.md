# Architecture sketch (draft)

Planning notes only. Nothing here is implemented yet.

## Layers

- **UI:** Jetpack Compose screens: dream profile, focus session, interruption overlay, progress view.
- **Domain:** plain Kotlin use cases (start session, record action, pick next step). Rules-based, no AI in v1.
- **Data:** Room database with repositories. Local-first, no network dependency.
- **Monitoring:** a foreground service that watches the foreground app during an active session only.

## Detecting a distracting app

Android cannot stop another app from launching. Options under evaluation:

1. **Usage Access** to read the foreground app, plus **display over other apps** to draw the interruption.
2. **AccessibilityService** for faster detection. It is restricted on Google Play and needs a clear disclosure and a Play Console declaration.

The first prototype will test option 1 on a real device and measure latency and reliability.

## Data model (initial)

- `Dream`: id, title, imageUri, why
- `Milestone`: id, dreamId, title, status
- `DailyAction`: id, milestoneId, title, doneAt
- `FocusSession`: id, dreamActionId, startedAt, endedAt
- `Interruption`: id, sessionId, appPackage, at, outcome (returned or dismissed)

## Privacy

- No account, no analytics that identify the user.
- Dream data never leaves the device in v1.
- Permissions are requested only when needed and explained first.

## Risks

- Play policy on accessibility and overlay permissions.
- Aggressive battery management on some phone brands killing the monitoring service.
- Overlay not being tamper-proof, which the product must say openly.
