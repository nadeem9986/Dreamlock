package site.n4deem.dreamlock.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import site.n4deem.dreamlock.di.AppContainer
import site.n4deem.dreamlock.ui.*
import site.n4deem.dreamlock.ui.home.HomeViewModel

@Composable
fun StatsScreen(container: AppContainer, onBack: () -> Unit) {
    val vm: HomeViewModel = viewModel(factory = HomeViewModel.factory(container))
    val actions by vm.actions.collectAsStateWithLifecycle()
    val milestones by vm.milestones.collectAsStateWithLifecycle()
    val sessions by vm.sessionsCompleted.collectAsStateWithLifecycle()
    val returns by vm.dreamReturns.collectAsStateWithLifecycle()
    val reels by vm.reelsToday.collectAsStateWithLifecycle()
    DreamScreen {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            ScreenHeading("Progress", "A record, not a score.", onBack)
            SectionHeader("Current dream")
            DreamCard {
                DreamIcon(DreamSymbol.Progress)
                DetailRow("Daily actions complete", "${actions.count { it.doneAt != null }} of ${actions.size}")
                DetailRow("Milestones complete", "${milestones.count { it.doneAt != null }} of ${milestones.size}")
            }
            SectionHeader("All-time activity")
            DreamCard {
                DetailRow("Focus sessions completed", sessions.toString())
                DetailRow("Returns to your task", returns.toString())
                Text("Includes prototype test interruptions.", style = MaterialTheme.typography.bodySmall)
            }
            SectionHeader("Today · scroll estimate")
            DreamCard {
                DetailRow("Reels and shorts", reels.count.toString())
                DetailRow("Feed time", "${reels.feedSeconds / 60} min")
                Text("Counts scroll bursts on Instagram and YouTube. Can include home-feed scrolls; not a verified video count.",
                    style = MaterialTheme.typography.bodySmall)
            }
            Text("No streak penalties. No comparisons. Your data stays here.",
                modifier = Modifier.padding(vertical = 24.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
