package site.n4deem.dreamlock.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** A small, consistent rounded-stroke vector set. No emoji, font downloads or icon pack. */
enum class DreamSymbol { Focus, Dream, Progress, Back, Check, Photo, Privacy }
fun dreamVector(symbol: DreamSymbol): ImageVector = ImageVector.Builder(
    name = symbol.name, defaultWidth = 24.dp, defaultHeight = 24.dp,
    viewportWidth = 24f, viewportHeight = 24f,
).apply {
    path(fill = null, stroke = androidx.compose.ui.graphics.SolidColor(Color.Black),
        strokeLineWidth = 1.8f, strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
        strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round) {
        when (symbol) {
            DreamSymbol.Focus -> { moveTo(8f,3f); lineTo(3f,3f); lineTo(3f,8f); moveTo(16f,3f); lineTo(21f,3f); lineTo(21f,8f); moveTo(21f,16f); lineTo(21f,21f); lineTo(16f,21f); moveTo(8f,21f); lineTo(3f,21f); lineTo(3f,16f); moveTo(9f,12f); lineTo(11f,14f); lineTo(15f,10f) }
            DreamSymbol.Dream -> { moveTo(12f,3f); lineTo(15f,9f); lineTo(21f,12f); lineTo(15f,15f); lineTo(12f,21f); lineTo(9f,15f); lineTo(3f,12f); lineTo(9f,9f); close() }
            DreamSymbol.Progress -> { moveTo(4f,20f); lineTo(4f,14f); moveTo(12f,20f); lineTo(12f,9f); moveTo(20f,20f); lineTo(20f,4f) }
            DreamSymbol.Back -> { moveTo(14f,5f); lineTo(7f,12f); lineTo(14f,19f) }
            DreamSymbol.Check -> { moveTo(5f,12f); lineTo(10f,17f); lineTo(20f,7f) }
            DreamSymbol.Photo -> { moveTo(4f,4f); lineTo(20f,4f); lineTo(20f,20f); lineTo(4f,20f); close(); moveTo(4f,16f); lineTo(9f,11f); lineTo(14f,16f); lineTo(17f,13f); lineTo(20f,16f) }
            DreamSymbol.Privacy -> { moveTo(12f,3f); lineTo(20f,6f); lineTo(19f,15f); lineTo(12f,21f); lineTo(5f,15f); lineTo(4f,6f); close(); moveTo(8f,12f); lineTo(11f,15f); lineTo(16f,9f) }
        }
    }
}.build()

@Composable
fun DreamIcon(symbol: DreamSymbol, description: String? = null) {
    Icon(imageVector = androidx.compose.runtime.remember(symbol) { dreamVector(symbol) }, contentDescription = description,
        tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
}
@Composable
fun ScreenHeading(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    if (onBack != null) TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
        DreamIcon(DreamSymbol.Back); Text("Back")
    }
    Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
    subtitle?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 16.dp)) }
}
@Composable
fun SectionHeader(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp))
}
@Composable
fun DreamDivider() { HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp)) }
@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label.replace('_', ' ').replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
@Composable
fun DreamButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier,
    enabled: Boolean = true, primary: Boolean = true, symbol: DreamSymbol? = null) {
    val body: @Composable RowScope.() -> Unit = {
        symbol?.let { Icon(androidx.compose.runtime.remember(it) { dreamVector(it) }, null, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(10.dp)) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
    if (primary) Button(onClick = onClick, enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp), content = body)
    else OutlinedButton(onClick = onClick, enabled = enabled,
        modifier = modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp), content = body)
}
@Composable
fun DreamCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp, shadowElevation = 1.dp) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}
@Composable
fun DreamScreen(content: @Composable ColumnScope.() -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding()
            .padding(horizontal = 24.dp, vertical = 12.dp), content = content)
    }
}
