package site.n4deem.dreamlock

import android.app.Application
import site.n4deem.dreamlock.di.AppContainer

class DreamlockApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
