package site.n4deem.dreamlock.ui.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import site.n4deem.dreamlock.data.DailyActionEntity
import site.n4deem.dreamlock.di.AppContainer
import site.n4deem.dreamlock.ui.SectionHeader
import site.n4deem.dreamlock.ui.DreamButton
import site.n4deem.dreamlock.ui.DreamCard
import site.n4deem.dreamlock.ui.DreamScreen

/**
 * TODO(monitoring): the countdown lives in this ViewModel, so it stops when the app
 * is backgrounded. Before real use, move it into the foreground service sketched in
 * monitoring/FocusMonitorService.kt and keep this screen as the session's control UI.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FocusTimerViewModel(private val container: AppContainer) : ViewModel() {
    private val dreamRepo = container.dreamRepository
    private val sessionRepo = container.sessionRepository

    val dream = dreamRepo.observePrimaryDream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val actions = dream.filterNotNull()
        .flatMapLatest { dreamRepo.observeActions(it.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<DailyActionEntity>())

    var selectedMinutes by mutableIntStateOf(25)
        private set
    var secondsLeft by mutableIntStateOf(25 * 60)
        private set
    var started by mutableStateOf(false)
        private set
    var running by mutableStateOf(false)
        private set
    var finished by mutableStateOf(false)
        private set
    var finishedCompleted by mutableStateOf(false)
        private set

    private var sessionId = 0L
    private var activeActionId: Long? = null
    private var timerJob: Job? = null

    fun selectMinutes(minutes: Int) {
        if (!started) {
            selectedMinutes = minutes
            secondsLeft = minutes * 60
        }
    }

    fun start() {
        val currentDream = dream.value ?: return
        if (started) return
        val action = actions.value.firstOrNull { it.doneAt == null }
        started = true
        finished = false
        viewModelScope.launch {
            sessionId = sessionRepo.startSession(currentDream.id, action?.id, selectedMinutes)
            activeActionId = action?.id
            runTimer()
        }
    }

    private fun runTimer() {
        running = true
        timerJob = viewModelScope.launch {
            while (secondsLeft > 0) {
                delay(1000)
                secondsLeft -= 1
            }
            completeSession(completed = true)
        }
    }

    fun pause() {
        timerJob?.cancel()
        running = false
    }

    fun resume() {
        if (started && !finished && !running) runTimer()
    }

    fun endEarly() {
        timerJob?.cancel()
        running = false
        if (started && !finished) {
            viewModelScope.launch { completeSession(completed = false) }
        }
    }

    private suspend fun completeSession(completed: Boolean) {
        if (finished) return
        finished = true
        finishedCompleted = completed
        running = false
        sessionRepo.finishSession(sessionId, completed)
        if (completed) activeActionId?.let { dreamRepo.setActionDone(it, true) }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                FocusTimerViewModel(container) as T
        }
    }
}

private fun formatClock(totalSeconds: Int): String =
    "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)

@Composable
fun FocusTimerScreen(
    container: AppContainer,
    onSimulateDistraction: () -> Unit,
    onLeave: () -> Unit,
) {
    val vm: FocusTimerViewModel = viewModel(factory = FocusTimerViewModel.factory(container))
    val dream by vm.dream.collectAsStateWithLifecycle()
    val actions by vm.actions.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    val mission = actions.firstOrNull { it.doneAt == null }

    DreamScreen {
        Column(modifier = Modifier.verticalScroll(scroll)) {
            site.n4deem.dreamlock.ui.ScreenHeading("Focus", "One thing at a time.")
            Text(text = formatClock(vm.secondsLeft), style = MaterialTheme.typography.displayLarge,
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp))
            Text(if (vm.finished) "Session ended" else if (vm.running) "In focus" else if (vm.started) "Paused" else "Ready when you are",
                modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            SectionHeader("Your intention")
            DreamCard {
                Text(
                    text = "dream: " + (dream?.title ?: "..."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "action: " + (mission?.title ?: "none open"),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            SectionHeader("Length")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 25, 45).forEach { minutes ->
                    val selected = vm.selectedMinutes == minutes
                    androidx.compose.material3.FilterChip(
                        selected = selected,
                        onClick = { vm.selectMinutes(minutes) },
                        enabled = !vm.started,
                        modifier = Modifier.weight(1f),
                        label = {
                        Text(
                            text = minutes.toString() + "m",
                            color = when {
                                selected -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    })
                }
            }

            SectionHeader("Session")
            when {
                !vm.started -> DreamButton(
                    text = "Start session",
                    enabled = mission != null,
                    onClick = { vm.start() },
                )

                !vm.finished -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (vm.running) {
                        DreamButton(text = "Pause", onClick = { vm.pause() })
                    } else {
                        DreamButton(text = "Resume", onClick = { vm.resume() })
                    }
                    var showPreview by remember { mutableStateOf(false) }
                    androidx.compose.material3.TextButton(onClick = { showPreview = !showPreview }) { Text("Developer preview") }
                    if (showPreview) {
                        Text("Preview only. No app detection or blocking is connected yet.", style = MaterialTheme.typography.bodySmall)
                        DreamButton("Preview interruption", { vm.pause(); onSimulateDistraction() }, primary = false)
                    }
                    DreamButton(text = "End session", onClick = { vm.endEarly() }, primary = false)
                }

                else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (vm.finishedCompleted) {
                            "session logged. one small step complete."
                        } else {
                            "session ended early. logged, no judgement."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (vm.finishedCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    DreamButton(text = "Back to today", onClick = onLeave)
                }
            }
            if (!vm.started) DreamButton("Back to today", onLeave, primary = false,
                modifier = Modifier.padding(top = 12.dp))
            Text("Keep this prototype open. Reliable background timing is not implemented yet.",
                style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}
