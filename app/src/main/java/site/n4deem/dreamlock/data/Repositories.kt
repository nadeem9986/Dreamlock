package site.n4deem.dreamlock.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

/** Dream side: profiles, milestones, daily actions. All local, all offline. */
class DreamRepository(private val db: DreamlockDatabase) {

    fun observeDreams(): Flow<List<DreamEntity>> = db.dreamDao().observeDreams()
    fun observePrimaryDream(): Flow<DreamEntity?> = db.dreamDao().observePrimaryDream()
    fun observeDream(id: Long): Flow<DreamEntity?> = db.dreamDao().observeDream(id)
    fun observeMilestones(dreamId: Long) = db.milestoneDao().observeForDream(dreamId)
    fun observeActions(dreamId: Long) = db.dailyActionDao().observeForDream(dreamId)
    fun observeCompletedActionCount() = db.dailyActionDao().observeCompletedCount()

    /** Onboarding creates the dream, its first milestone and its first daily action atomically. */
    suspend fun createDreamWithFirstStep(
        title: String,
        why: String,
        imageUri: String?,
        milestoneTitle: String,
        actionTitle: String,
    ): Long = db.withTransaction {
        val dreamId = db.dreamDao().insert(DreamEntity(title = title, why = why, imageUri = imageUri))
        val milestoneId = db.milestoneDao().insert(
            MilestoneEntity(dreamId = dreamId, title = milestoneTitle, sortOrder = 0),
        )
        db.dailyActionDao().insert(DailyActionEntity(milestoneId = milestoneId, title = actionTitle))
        dreamId
    }

    suspend fun setMilestoneDone(id: Long, done: Boolean) =
        db.milestoneDao().setDone(id, if (done) System.currentTimeMillis() else null)

    suspend fun setActionDone(id: Long, done: Boolean) =
        db.dailyActionDao().setDone(id, if (done) System.currentTimeMillis() else null)

    suspend fun addAction(milestoneId: Long, title: String) =
        db.dailyActionDao().insert(DailyActionEntity(milestoneId = milestoneId, title = title))
}

/** Session side: focus sessions and interruption outcomes. */
class SessionRepository(private val db: DreamlockDatabase) {

    fun observeSessions() = db.focusSessionDao().observeAll()
    fun observeCompletedSessionCount() = db.focusSessionDao().observeCompletedCount()
    fun observeReturnedInterruptionCount() = db.interruptionDao().observeReturnedCount()

    suspend fun startSession(dreamId: Long, dailyActionId: Long?, plannedMinutes: Int): Long =
        db.focusSessionDao().insert(
            FocusSessionEntity(
                dreamId = dreamId,
                dailyActionId = dailyActionId,
                plannedMinutes = plannedMinutes,
            ),
        )

    suspend fun finishSession(id: Long, completed: Boolean) =
        db.focusSessionDao().finish(id, System.currentTimeMillis(), completed)

    suspend fun logInterruption(sessionId: Long?, appPackage: String, outcome: String) =
        db.interruptionDao().insert(
            InterruptionEntity(sessionId = sessionId, appPackage = appPackage, outcome = outcome),
        )
}
