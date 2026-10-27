package site.n4deem.dreamlock.monitoring

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import site.n4deem.dreamlock.DreamlockApplication

/**
 * Counts reels and shorts. Runs ONLY because the user turned it on in
 * Settings > Accessibility; the home screen shows the toggle state and deep-links there.
 *
 * HOW IT COUNTS, and its honest limits:
 * - Instagram and YouTube fire TYPE_VIEW_SCROLLED constantly while a feed moves. A swipe
 *   to the next reel is a burst of those events, so one reel = one burst, debounced to
 *   [REEL_DEBOUNCE_MS]. Telling the reels feed apart from the home feed needs fragile
 *   view-id reads we deliberately skip - the count is an ESTIMATE of short-video
 *   scrolling, and an estimate is all the nudge needs.
 * - Feed time comes from gaps between events: events closer than [SESSION_GAP_MS] mean
 *   the feed is still alive. Videos autoplaying without touches undercount; noted, not
 *   hidden, in the README.
 * - Reads NO content: canRetrieveWindowContent is false, the config limits delivery to
 *   the two packages, and only the event type and time are ever used.
 *
 * Play policy: accessibility APIs are restricted - production needs the accessibility
 * disclosure and Play Console declaration, same policy wall as the blocking prototype
 * (see FocusMonitorService.kt and Docs/architecture.md).
 */
class ReelsMonitorService : AccessibilityService() {

    private var lastReelAt = 0L
    private var lastEventAt = 0L
    private var pendingFeedMs = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val counter = (application as? DreamlockApplication)?.container?.reelsCounter ?: return
        val now = event.eventTime

        // Any event from the package means the feed is alive; fold the gap into feed time.
        val last = lastEventAt
        if (last > 0 && now - last in 1..SESSION_GAP_MS) {
            pendingFeedMs += now - last
            val seconds = (pendingFeedMs / 1000).toInt()
            if (seconds > 0) {
                counter.recordFeedSeconds(seconds)
                pendingFeedMs -= seconds * 1000L
            }
        }
        lastEventAt = now

        // A burst of scroll events = the next reel. Count the burst, not every event.
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED && now - lastReelAt >= REEL_DEBOUNCE_MS) {
            lastReelAt = now
            counter.recordReel()
        }
    }

    override fun onInterrupt() = Unit

    companion object {
        /** Scroll bursts closer together than this are one reel, not two. */
        private const val REEL_DEBOUNCE_MS = 1_200L

        /** Events further apart than this mean the feed was left. */
        private const val SESSION_GAP_MS = 30_000L

        fun isEnabled(context: Context): Boolean {
            val expected = ComponentName(context, ReelsMonitorService::class.java).flattenToString()
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            ) ?: return false
            return enabled.split(':').any { it.equals(expected, ignoreCase = true) }
        }
    }
}
