package site.n4deem.dreamlock.ui.dreamsetup

import androidx.compose.material3.MaterialTheme
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import site.n4deem.dreamlock.di.AppContainer
import site.n4deem.dreamlock.ui.SectionHeader
import site.n4deem.dreamlock.ui.DreamButton
import site.n4deem.dreamlock.ui.DreamScreen

class DreamSetupViewModel(private val container: AppContainer) : ViewModel() {
    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    fun save(title: String, why: String, imageUri: String?, milestone: String, action: String) {
        viewModelScope.launch {
            container.dreamRepository.createDreamWithFirstStep(
                title = title.trim(),
                why = why.trim(),
                imageUri = imageUri,
                milestoneTitle = milestone.trim(),
                actionTitle = action.trim(),
            )
            _saved.value = true
        }
    }

    companion object {
        fun factory(container: AppContainer) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                DreamSetupViewModel(container) as T
        }
    }
}

@Composable
private fun terminalFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
)

@Composable
fun DreamSetupScreen(container: AppContainer, onSaved: () -> Unit) {
    val vm: DreamSetupViewModel = viewModel(factory = DreamSetupViewModel.factory(container))
    val context = LocalContext.current
    val scroll = rememberScrollState()

    var title by rememberSaveable { mutableStateOf("") }
    var why by rememberSaveable { mutableStateOf("") }
    var milestone by rememberSaveable { mutableStateOf("") }
    var action by rememberSaveable { mutableStateOf("") }
    var imageUri by rememberSaveable { mutableStateOf<String?>(null) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null && uri.scheme == "content") {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            } catch (_: SecurityException) {
                // Photo picker grants can be temporary; the image simply stops loading later.
            }
            imageUri = uri.toString()
        }
    }

    val saved by vm.saved.collectAsStateWithLifecycle()
    if (saved) {
        LaunchedEffect(Unit) { onSaved() }
    }

    DreamScreen {
        Column(modifier = Modifier.verticalScroll(scroll)) {
            site.n4deem.dreamlock.ui.ScreenHeading("Make it real", "One dream. One small step to start.")
            SectionHeader("Your dream")
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Your dream") },
                singleLine = true,
                colors = terminalFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            )
            OutlinedTextField(
                value = why,
                onValueChange = { why = it },
                label = { Text("Why it matters") },
                minLines = 3,
                colors = terminalFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            )
            DreamButton(
                primary = false,
                symbol = site.n4deem.dreamlock.ui.DreamSymbol.Photo,
                text = if (imageUri == null) "Attach dream image (optional)" else "Image attached - change",
                onClick = {
                    pickImage.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                },
                modifier = Modifier.padding(vertical = 8.dp),
            )

            SectionHeader("First milestone")
            OutlinedTextField(
                value = milestone,
                onValueChange = { milestone = it },
                label = { Text("Your first checkpoint") },
                singleLine = true,
                colors = terminalFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            )

            SectionHeader("First daily action")
            OutlinedTextField(
                value = action,
                onValueChange = { action = it },
                label = { Text("small enough to start today") },
                singleLine = true,
                colors = terminalFieldColors(),
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            )

            val valid = title.isNotBlank() && why.isNotBlank() &&
                milestone.isNotBlank() && action.isNotBlank()
            DreamButton(
                text = "Save dream",
                onClick = { vm.save(title, why, imageUri, milestone, action) },
                enabled = valid,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}
