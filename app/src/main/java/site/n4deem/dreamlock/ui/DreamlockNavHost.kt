package site.n4deem.dreamlock.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import site.n4deem.dreamlock.DreamlockApplication
import site.n4deem.dreamlock.ui.dream.DreamProfileScreen
import site.n4deem.dreamlock.ui.dreamsetup.DreamSetupScreen
import site.n4deem.dreamlock.ui.focus.FocusTimerScreen
import site.n4deem.dreamlock.ui.home.HomeScreen
import site.n4deem.dreamlock.ui.interruption.DreamInterruptionScreen
import site.n4deem.dreamlock.ui.onboarding.OnboardingScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val SETUP = "dream_setup"
    const val HOME = "home"
    const val STATS = "stats"
    const val PROFILE = "dream_profile/{dreamId}"
    const val FOCUS = "focus"
    const val INTERRUPTION = "interruption"

    fun profile(dreamId: Long) = "dream_profile/$dreamId"
}

@Composable
fun DreamlockApp() {
    val app = LocalContext.current.applicationContext as DreamlockApplication
    val container = app.container

    // null = still reading the database; list (possibly empty) = ready.
    val dreams by container.dreamRepository.observeDreams()
        .collectAsStateWithLifecycle(initialValue = null)
    val ready = dreams ?: run {
        DreamScreen {
            Text(
                text = "Loading Dreamlock...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (ready.isEmpty()) Routes.ONBOARDING else Routes.HOME,
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(onBegin = { navController.navigate(Routes.SETUP) })
        }
        composable(Routes.SETUP) {
            DreamSetupScreen(
                container = container,
                onSaved = {
                    navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
                },
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                container = container,
                onStartFocus = { navController.navigate(Routes.FOCUS) },
                onOpenDream = { id -> navController.navigate(Routes.profile(id)) },
                onOpenStats = { navController.navigate(Routes.STATS) },
            )
        }
        composable(Routes.PROFILE) { entry ->
            val dreamId = entry.arguments?.getString("dreamId")?.toLongOrNull() ?: return@composable
            DreamProfileScreen(container = container, dreamId = dreamId, onBack = { navController.popBackStack() })
        }
        composable(Routes.STATS) {
            site.n4deem.dreamlock.ui.stats.StatsScreen(container) { navController.popBackStack() }
        }
        composable(Routes.FOCUS) {
            FocusTimerScreen(
                container = container,
                onSimulateDistraction = { navController.navigate(Routes.INTERRUPTION) },
                onLeave = { navController.popBackStack() },
            )
        }
        composable(Routes.INTERRUPTION) {
            DreamInterruptionScreen(
                container = container,
                onLeave = { navController.popBackStack() },
            )
        }
    }
}
