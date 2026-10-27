package site.n4deem.dreamlock.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** The dream itself: title, personal why, optional image. Never leaves the device. */
@Entity(tableName = "dreams")
data class DreamEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val why: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

/** A dream broken into milestones. [doneAt] null = still open. */
@Entity(
    tableName = "milestones",
    foreignKeys = [ForeignKey(
        entity = DreamEntity::class,
        parentColumns = ["id"],
        childColumns = ["dreamId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("dreamId")],
)
data class MilestoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dreamId: Long,
    val title: String,
    val sortOrder: Int = 0,
    val doneAt: Long? = null,
)

/** A step small enough to do today, tied to one milestone. */
@Entity(
    tableName = "daily_actions",
    foreignKeys = [ForeignKey(
        entity = MilestoneEntity::class,
        parentColumns = ["id"],
        childColumns = ["milestoneId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("milestoneId")],
)
data class DailyActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val milestoneId: Long,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val doneAt: Long? = null,
)

/** One focus session. Completed means the timer ran out, not "perfect behaviour". */
@Entity(
    tableName = "focus_sessions",
    foreignKeys = [ForeignKey(
        entity = DreamEntity::class,
        parentColumns = ["id"],
        childColumns = ["dreamId"],
        onDelete = ForeignKey.CASCADE,
    )],
    indices = [Index("dreamId")],
)
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dreamId: Long,
    /** Plain column on purpose: the action may be deleted later without killing history. */
    val dailyActionId: Long? = null,
    val plannedMinutes: Int,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val completed: Boolean = false,
)

/** One moment the interruption screen was shown, and what the user chose. */
@Entity(tableName = "interruptions")
data class InterruptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** Nullable plain column: sessions can be discarded, the interruption record stays. */
    val sessionId: Long? = null,
    val appPackage: String,
    val at: Long = System.currentTimeMillis(),
    /** "RETURNED" (went back to the task) or "DISMISSED" (kept scrolling). Never judged. */
    val outcome: String,
)
