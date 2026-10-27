package site.n4deem.dreamlock.ui.dream

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import site.n4deem.dreamlock.ui.DetailRow
import site.n4deem.dreamlock.ui.SectionHeader
import site.n4deem.dreamlock.ui.DreamButton
import site.n4deem.dreamlock.ui.DreamCard
import site.n4deem.dreamlock.ui.DreamScreen

class DreamProfileViewModel(container: AppContainer, dreamId: Long) : ViewModel() {
    private val repo = container.dreamRepository

    val dream = repo.observeDream(dreamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val milestones = repo.observeMilestones(dreamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val actions = repo.observeActions(dreamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setMilestoneDone(id: Long, done: Boolean) {
        viewModelScope.launch { repo.setMilestoneDone(id, done) }
    }

    fun setActionDone(id: Long, done: Boolean) {
        viewModelScope.launch { repo.setActionDone(id, done) }
    }

    fun addAction(milestoneId: Long, title: String) {
        viewModelScope.launch { repo.addAction(milestoneId, title.trim()) }
    }

    companion object {
        fun factory(container: AppContainer, dreamId: Long) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DreamProfileViewModel(container, dreamId) as T
        }
    }
}

@Composable
fun DreamProfileScreen(container: AppContainer, dreamId: Long, onBack: () -> Unit) {
    val vm: DreamProfileViewModel = viewModel(
        factory = DreamProfileViewModel.factory(container, dreamId),
    )
    val dream by vm.dream.collectAsStateWithLifecycle()
    val milestones by vm.milestones.collectAsStateWithLifecycle()
    val actions by vm.actions.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    var newAction by remember { mutableStateOf("") }

    DreamScreen {
        Column(modifier = Modifier.verticalScroll(scroll)) {
            val current = dream
            if (current == null) {
                Text("loading dream...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                return@Column
            }

            site.n4deem.dreamlock.ui.ScreenHeading("Your dream", onBack = onBack)
            SectionHeader("The reason")
            DreamCard {
                if (current.imageUri != null) {
                    AsyncImage(
                        model = current.imageUri.takeIf { it.startsWith("content://") },
                        contentDescription = "Dream image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(16.dp)),
                    )
                }
                Text(
                    text = current.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                DetailRow("why", current.why)
            }

            SectionHeader("Milestones")
            DreamCard {
                milestones.forEach { m ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = m.doneAt != null,
                            onCheckedChange = { vm.setMilestoneDone(m.id, it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                        Text(
                            text = m.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (m.doneAt != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            SectionHeader("Daily actions")
            DreamCard {
                actions.forEach { a ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = a.doneAt != null,
                            onCheckedChange = { vm.setActionDone(a.id, it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = MaterialTheme.colorScheme.primary,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                checkmarkColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        )
                        Text(
                            text = a.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (a.doneAt != null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // TODO(edit-flows): full dream edit (title/why/image) is intentionally out of
            // the skeleton; add an edit route here after MVP lock.
            SectionHeader("Add a small step")
            Text("Added to the next open milestone, or the last milestone if all are complete.",
                style = MaterialTheme.typography.bodySmall)
            OutlinedTextField(
                value = newAction,
                onValueChange = { newAction = it },
                label = { Text("next small action") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            val openMilestone = milestones.firstOrNull { it.doneAt == null } ?: milestones.lastOrNull()
            DreamButton(
                text = "Add action",
                enabled = newAction.isNotBlank() && openMilestone != null,
                onClick = {
                    openMilestone?.let { vm.addAction(it.id, newAction) }
                    newAction = ""
                },
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
