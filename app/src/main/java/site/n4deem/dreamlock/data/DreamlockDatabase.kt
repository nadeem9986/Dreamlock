package site.n4deem.dreamlock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        DreamEntity::class,
        MilestoneEntity::class,
        DailyActionEntity::class,
        FocusSessionEntity::class,
        InterruptionEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class DreamlockDatabase : RoomDatabase() {
    abstract fun dreamDao(): DreamDao
    abstract fun milestoneDao(): MilestoneDao
    abstract fun dailyActionDao(): DailyActionDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun interruptionDao(): InterruptionDao

    companion object {
        /**
         * Pre-1.0 schema: destructive migration is honest and fine while the model settles.
         * Replace with real migrations before any release with real user data.
         */
        fun build(context: Context): DreamlockDatabase =
            Room.databaseBuilder(context, DreamlockDatabase::class.java, "dreamlock.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
