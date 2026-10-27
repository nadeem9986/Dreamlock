package site.n4deem.dreamlock.ui.onboarding

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import site.n4deem.dreamlock.ui.DetailRow
import site.n4deem.dreamlock.ui.SectionHeader
import site.n4deem.dreamlock.ui.DreamButton
import site.n4deem.dreamlock.ui.DreamCard
import site.n4deem.dreamlock.ui.DreamDivider
import site.n4deem.dreamlock.ui.DreamScreen

@Composable
fun OnboardingScreen(onBegin: () -> Unit) {
    DreamScreen {
        val scroll = rememberScrollState()
        androidx.compose.foundation.layout.Column(modifier = Modifier.verticalScroll(scroll)) {
            site.n4deem.dreamlock.ui.DreamIcon(site.n4deem.dreamlock.ui.DreamSymbol.Dream)
            site.n4deem.dreamlock.ui.ScreenHeading("Make room for\nyour dream.", "Less autopilot. More of the life you want.")
            DreamButton(text = "Create my dream", onClick = onBegin)
            SectionHeader("The loop")
            DetailRow("plan", "define a dream, split it into milestones, pick today's small step")
            DetailRow("protect", "run a focus session tied to that step")
            DetailRow("prove", "mark it done; progress is recorded, never weaponised")
            DetailRow("see", "watch small steps add up to the dream")

            SectionHeader("Honest limits")
            DreamCard {
                Text(
                    text = "Android cannot truly block an app from launching. " +
                        "This starter previews the intervention, but does not detect or block app launches yet: " +
                        "your image, your words, one tap back. A nudge you opted into, " +
                        "not a lock you fight.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            SectionHeader("Privacy")
            DetailRow("storage", "local only, works offline")
            DetailRow("account", "none")
            DetailRow("analytics", "none that identify you")


        }
    }
}
