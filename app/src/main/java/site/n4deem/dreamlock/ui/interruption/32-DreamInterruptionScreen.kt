package site.n4deem.dreamlock.ui.interruption

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import site.n4deem.dreamlock.di.AppContainer
import site.n4deem.dreamlock.ui.SectionHeader
import site.n4deem.dreamlock.ui.DreamButton
import site.n4deem.dreamlock.ui.DreamCard
import site.n4deem.dreamlock.ui.DreamScreen

class DreamInterruptionViewModel(private val container: AppContainer) : ViewModel() {
    val dream = container.dreamRepository.observePrimaryDream()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Dealt once when this screen instance is created, so every appearance of the
     * block screen shows a fresh quote. The deck avoids repeats until it cycles.
     */
    val quote: MotivationQuote = MotivationDeck.deal()

    /** Snapshot, not a live flow: the number that matters is the one on arrival. */
    val reelsToday = container.reelsCounter.today()

    /**
     * The real trigger passes the detected package name and the active session id.
     * The static test trigger passes neither.
     */
    fun logOutcome(sessionId: Long?, appPackage: String, outcome: String, onDone: () -> Unit) {
        viewModelScope.launch {
            container.sessionRepository.logInterruption(sessionId, appPackage, outcome)
            onDone()
        }
    }

    companion object {
        const val SIMULATED_PACKAGE = "debug.simulated"

        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DreamInterruptionViewModel(container) as T
        }
    }
}

/**
 * The moment of the whole product: a distracting app was opened during a session.
 * No shame, no lecture. The dream, in the user's own words and image, a fresh
 * motivational quote from MotivationDeck, and one tap back.
 *
 * Today this is shown by a test button on the focus screen. The blocking prototype
 * will show it from monitoring/FocusMonitorService.kt as an overlay instead.
 */
@Composable
fun DreamInterruptionScreen(container: AppContainer, onLeave: () -> Unit) {
    val vm: DreamInterruptionViewModel = viewModel(factory = DreamInterruptionViewModel.factory(container))
    val dream by vm.dream.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    DreamScreen {
        Column(modifier = Modifier.verticalScroll(scroll)) {
            Text(
                text = "a moment to choose",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            site.n4deem.dreamlock.ui.ScreenHeading("Remember your why.")
            DreamCard {
                val current = dream
                if (current?.imageUri != null) {
                    AsyncImage(
                        model = current.imageUri.takeIf { it.startsWith("content://") },
                        contentDescription = "Your dream",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)),
                    )
                }
                Text(
                    text = current?.title ?: "your dream",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = current?.why ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            DreamButton(
                text = "Back to my task", modifier = Modifier.padding(top = 20.dp),
                onClick = { vm.logOutcome(null, DreamInterruptionViewModel.SIMULATED_PACKAGE, "RETURNED", onLeave) },
                symbol = site.n4deem.dreamlock.ui.DreamSymbol.Back,
            )
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                Text(
                    text = "a small reminder",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = vm.quote.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                vm.quote.attribution?.let { source ->
                    Text(
                        text = "- " + source,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                // Today's reels count, stated as information, never as a verdict.
                if (vm.reelsToday.count > 0) {
                    Text(
                        text = "estimated reels_today: " + vm.reelsToday.count +
                            " · feed_time: " + (vm.reelsToday.feedSeconds / 60) + " min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DreamButton(
                    text = "Dismiss preview",
                    primary = false,
                    onClick = {
                        vm.logOutcome(null, DreamInterruptionViewModel.SIMULATED_PACKAGE, "DISMISSED", onLeave)
                    },
                )
                Text(
                    text = "preview only. dismissing logs a choice; it does not grant extra app time.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
