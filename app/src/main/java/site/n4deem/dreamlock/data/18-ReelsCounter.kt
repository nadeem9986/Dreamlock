package site.n4deem.dreamlock.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Today's reels/shorts tally. Count is an estimate: one counted reel per debounced scroll burst. */
data class ReelsToday(val date: String, val count: Int, val feedSeconds: Int)

/**
 * The reels/shorts counter store. SharedPreferences, not Room: three numbers keyed by
 * date do not need a migration, and the monitor service writes them off the UI path.
 * Per-day history moves to Room when the blocking prototype lands.
 */
class ReelsCounter(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val _today = MutableStateFlow(load())

    /** Live view of today's tally; updates the moment the monitor counts another reel. */
    fun observe(): StateFlow<ReelsToday> = _today

    /** Snapshot for screens that only need the number once (the interruption screen). */
    fun today(): ReelsToday = _today.value

    /** One debounced scroll burst = one reel watched. Called by the monitor service. */
    @Synchronized
    fun recordReel() {
        val t = rolledToToday()
        save(t.copy(count = t.count + 1))
    }

    /** Seconds spent inside the reels/shorts apps, accumulated from gaps between events. */
    @Synchronized
    fun recordFeedSeconds(seconds: Int) {
        if (seconds <= 0) return
        val t = rolledToToday()
        save(t.copy(feedSeconds = t.feedSeconds + seconds))
    }

    private fun rolledToToday(): ReelsToday {
        val current = _today.value
        val today = dayKey()
        return if (current.date == today) current else ReelsToday(today, 0, 0)
    }

    private fun save(t: ReelsToday) {
        prefs.edit()
            .putString(KEY_DATE, t.date)
            .putInt(KEY_COUNT, t.count)
            .putInt(KEY_SECONDS, t.feedSeconds)
            .apply()
        _today.value = t
    }

    private fun load(): ReelsToday {
        val today = dayKey()
        return if (prefs.getString(KEY_DATE, null) == today) {
            ReelsToday(today, prefs.getInt(KEY_COUNT, 0), prefs.getInt(KEY_SECONDS, 0))
        } else {
            ReelsToday(today, 0, 0)
        }
    }

    companion object {
        private const val PREFS = "reels_counter"
        private const val KEY_DATE = "date"
        private const val KEY_COUNT = "count"
        private const val KEY_SECONDS = "feed_seconds"

        fun dayKey(now: Long = System.currentTimeMillis()): String =
            SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(now))
    }
}
