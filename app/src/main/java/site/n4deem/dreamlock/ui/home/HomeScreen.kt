package site.n4deem.dreamlock.ui.home

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import site.n4deem.dreamlock.data.DailyActionEntity
import site.n4deem.dreamlock.data.DreamEntity
import site.n4deem.dreamlock.data.MilestoneEntity
import site.n4deem.dreamlock.di.AppContainer
import site.n4deem.dreamlock.monitoring.ReelsMonitorService
import site.n4deem.dreamlock.ui.DetailRow
import site.n4deem.dreamlock.ui.SectionHeader
import site.n4deem.dreamlock.ui.DreamButton
import site.n4deem.dreamlock.ui.DreamCard
import site.n4deem.dreamlock.ui.DreamDivider
import site.n4deem.dreamlock.ui.DreamScreen

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(container: AppContainer) : ViewModel() {
    private val dreams = container.dreamRepository
    private val sessions = container.sessionRepository

    val dream = dreams.observePrimaryDream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val milestones = dream.filterNotNull()
        .flatMapLatest { dreams.observeMilestones(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<MilestoneEntity>())
    val actions = dream.filterNotNull()
        .flatMapLatest { dreams.observeActions(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<DailyActionEntity>())
    val sessionsCompleted = sessions.observeCompletedSessionCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val dreamReturns = sessions.observeReturnedInterruptionCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val reelsToday = container.reelsCounter.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), container.reelsCounter.today())

    /** The mission of the day: the first daily action still open. */
    fun missionOf(actions: List<DailyActionEntity>): DailyActionEntity? =
        actions.firstOrNull { it.doneAt == null }

    fun completeAction(id: Long) {
        viewModelScope.launch { dreams.setActionDone(id, true) }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HomeViewModel(container) as T
        }
    }
}

@Composable
fun HomeScreen(
    container: AppContainer,
    onStartFocus: () -> Unit,
    onOpenDream: (Long) -> Unit,
    onOpenStats: () -> Unit,
) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(container))
    val dream by vm.dream.collectAsStateWithLifecycle()
    val milestones by vm.milestones.collectAsStateWithLifecycle()
    val actions by vm.actions.collectAsStateWithLifecycle()
    val reelsToday by vm.reelsToday.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var monitorOn by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(ReelsMonitorService.isEnabled(context)) }
    // Refresh permission state after returning from Android Settings.
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    androidx.compose.runtime.DisposableEffect(lifecycle) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) monitorOn = ReelsMonitorService.isEnabled(context)
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    DreamScreen {
        androidx.compose.foundation.layout.Column(Modifier.verticalScroll(rememberScrollState())) {
            site.n4deem.dreamlock.ui.ScreenHeading("Today", "Small steps. A different future.")
            val current = dream
            if (current == null) { Text("Loading your dream..."); return@Column }
            androidx.compose.material3.TextButton(onClick = { onOpenDream(current.id) }) {
                site.n4deem.dreamlock.ui.DreamIcon(site.n4deem.dreamlock.ui.DreamSymbol.Dream)
                Text(current.title, modifier = Modifier.padding(start = 8.dp))
            }
            SectionHeader("Your next step")
            val mission = vm.missionOf(actions)
            DreamCard {
                if (mission == null) {
                    Text("All steps complete", style = MaterialTheme.typography.titleLarge)
                    Text("Add your next small step in your dream.")
                    DreamButton("Open my dream", { onOpenDream(current.id) })
                } else {
                    Text(mission.title, style = MaterialTheme.typography.headlineSmall)
                    Text(milestones.firstOrNull { it.id == mission.milestoneId }?.title ?: "Your next checkpoint",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    DreamButton("Start focus", onStartFocus, symbol = site.n4deem.dreamlock.ui.DreamSymbol.Focus)
                    androidx.compose.material3.TextButton(onClick = { vm.completeAction(mission.id) }) {
                        Text("Already done? Mark complete")
                    }
                }
            }
            SectionHeader("Your momentum")
            val done = actions.count { it.doneAt != null }
            DreamCard {
                DetailRow("Steps complete", "$done of ${actions.size}")
                LinearProgressIndicator(progress = { if (actions.isEmpty()) 0f else done.toFloat() / actions.size },
                    modifier = Modifier.fillMaxWidth())
                DreamButton("View progress", onOpenStats, primary = false,
                    symbol = site.n4deem.dreamlock.ui.DreamSymbol.Progress)
            }
            SectionHeader("Scroll awareness")
            DreamCard {
                if (monitorOn) {
                    Text("${reelsToday.count} estimated reels today", style = MaterialTheme.typography.titleMedium)
                    Text("${reelsToday.feedSeconds / 60} min in feed · an estimate, not an exact count")
                } else {
                    Text("Counter is off", style = MaterialTheme.typography.titleMedium)
                    Text("Opt in to estimate Instagram and YouTube scrolls. Scroll events only; no content is read.")
                    DreamButton("Set up counter", { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }, primary = false)
                }
            }
            Text("Offline. On your device. No account.", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 24.dp))
        }
    }
}
