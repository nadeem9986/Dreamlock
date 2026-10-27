package site.n4deem.dreamlock.di

import android.content.Context
import site.n4deem.dreamlock.data.DreamRepository
import site.n4deem.dreamlock.data.ReelsCounter
import site.n4deem.dreamlock.data.DreamlockDatabase
import site.n4deem.dreamlock.data.SessionRepository

/**
 * Manual dependency wiring: one container, created once in [DreamlockApplication].
 * Deliberately no Hilt/Dagger yet - the graph is a handful of objects. Adopt Hilt when the
 * monitoring service and WorkManager land and injection stops being trivial.
 */
class AppContainer(context: Context) {
    private val db: DreamlockDatabase = DreamlockDatabase.build(context.applicationContext)
    val dreamRepository: DreamRepository = DreamRepository(db)
    val sessionRepository: SessionRepository = SessionRepository(db)
    val reelsCounter: ReelsCounter = ReelsCounter(context)
}
