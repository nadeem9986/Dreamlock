package site.n4deem.dreamlock.monitoring

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * FUTURE HOME OF THE BLOCKING PROTOTYPE. Not registered in the manifest yet on purpose:
 * an empty exported component is worse than a missing one.
 *
 * What this service must do (Phase 1, the riskiest piece - prove it first, on a real
 * phone, before building anything on top):
 *
 * 1. Run as a foreground service ONLY while a focus session is active. Start it from
 *    FocusTimerViewModel.start(); stop it when the session ends. Never run it all day:
 *    battery, Play policy, and trust all say no.
 *
 * 2. Detect a distracting app opening. First option under test (see Docs/architecture.md):
 *    UsageStatsManager.queryEvents() polled on a short interval to read MOVE_TO_FOREGROUND
 *    events. Needs PACKAGE_USAGE_STATS, which the user grants in Settings - onboarding
 *    must deep-link there with a plain-words explanation first.
 *
 * 3. Respond by drawing the dream interruption. Start with a full-screen Activity launch
 *    (works without overlay permission, slight delay); evaluate SYSTEM_ALERT_WINDOW
 *    overlay only if the activity route is too slow. Log every InterruptionEntity with
 *    the real package name instead of DreamInterruptionViewModel.SIMULATED_PACKAGE.
 *
 * 4. Handle the honesty requirements: battery OEM killers (Motorola/others) will kill
 *    this service; say so in the UI. The overlay is a nudge, not a lock; say that too.
 *
 * Known policy traps: AccessibilityService detection is faster but Play-restricted
 * (needs disclosure + Console declaration). New personal Play accounts need a closed
 * test before production. Both live in the repo docs.
 */
class FocusMonitorService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
