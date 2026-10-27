package site.n4deem.dreamlock.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DreamDao {
    @Query("SELECT * FROM dreams ORDER BY createdAt ASC")
    fun observeDreams(): Flow<List<DreamEntity>>

    @Query("SELECT * FROM dreams ORDER BY createdAt ASC LIMIT 1")
    fun observePrimaryDream(): Flow<DreamEntity?>

    @Query("SELECT * FROM dreams WHERE id = :id")
    fun observeDream(id: Long): Flow<DreamEntity?>

    @Insert
    suspend fun insert(dream: DreamEntity): Long
}

@Dao
interface MilestoneDao {
    @Query("SELECT * FROM milestones WHERE dreamId = :dreamId ORDER BY sortOrder ASC, id ASC")
    fun observeForDream(dreamId: Long): Flow<List<MilestoneEntity>>

    @Insert
    suspend fun insert(milestone: MilestoneEntity): Long

    @Query("UPDATE milestones SET doneAt = :doneAt WHERE id = :id")
    suspend fun setDone(id: Long, doneAt: Long?)
}

@Dao
interface DailyActionDao {
    /** Join instead of an IN-list so an empty milestone list can never produce bad SQL. */
    @Query(
        "SELECT da.* FROM daily_actions da " +
            "INNER JOIN milestones m ON da.milestoneId = m.id " +
            "WHERE m.dreamId = :dreamId ORDER BY da.createdAt ASC, da.id ASC",
    )
    fun observeForDream(dreamId: Long): Flow<List<DailyActionEntity>>

    @Insert
    suspend fun insert(action: DailyActionEntity): Long

    @Query("UPDATE daily_actions SET doneAt = :doneAt WHERE id = :id")
    suspend fun setDone(id: Long, doneAt: Long?)

    @Query("SELECT COUNT(*) FROM daily_actions WHERE doneAt IS NOT NULL")
    fun observeCompletedCount(): Flow<Int>
}

@Dao
interface FocusSessionDao {
    @Insert
    suspend fun insert(session: FocusSessionEntity): Long

    @Query("UPDATE focus_sessions SET endedAt = :endedAt, completed = :completed WHERE id = :id")
    suspend fun finish(id: Long, endedAt: Long, completed: Boolean)

    @Query("SELECT * FROM focus_sessions ORDER BY startedAt DESC")
    fun observeAll(): Flow<List<FocusSessionEntity>>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE completed = 1")
    fun observeCompletedCount(): Flow<Int>
}

@Dao
interface InterruptionDao {
    @Insert
    suspend fun insert(interruption: InterruptionEntity): Long

    @Query("SELECT * FROM interruptions ORDER BY at DESC")
    fun observeAll(): Flow<List<InterruptionEntity>>

    @Query("SELECT COUNT(*) FROM interruptions WHERE outcome = 'RETURNED'")
    fun observeReturnedCount(): Flow<Int>
}
