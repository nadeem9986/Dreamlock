package site.n4deem.dreamlock.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Humanist system sans, not Apple's licensed SF font. Semantic roles work in both modes.
val DreamAccent = Color(0xFF0064D9)
val DreamSecondary = Color(0xFF545B66)
val DreamMuted = Color(0xFF69717D)
val DreamWarning = Color(0xFF8A5900)
private val Light = lightColorScheme(primary = DreamAccent, onPrimary = Color.White,
    background = Color(0xFFF5F6F8), onBackground = Color(0xFF161A22),
    surface = Color.White, onSurface = Color(0xFF161A22),
    surfaceVariant = Color(0xFFE9EDF2), onSurfaceVariant = DreamSecondary,
    outline = Color(0xFF747C89), outlineVariant = Color(0xFFDDE2E9))
private val Dark = darkColorScheme(primary = Color(0xFF8EC1FF), onPrimary = Color(0xFF002C62),
    background = Color(0xFF101216), onBackground = Color(0xFFF0F2F6),
    surface = Color(0xFF1C2027), onSurface = Color(0xFFF0F2F6),
    surfaceVariant = Color(0xFF292E38), onSurfaceVariant = Color(0xFFBBC3CF),
    outline = Color(0xFF929BA9))
private val Type = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 32.sp, lineHeight = 38.sp,
        fontWeight = FontWeight.Bold, letterSpacing = (-0.6).sp),
    displayLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 64.sp, lineHeight = 76.sp,
        fontWeight = FontWeight.Light, letterSpacing = (-2).sp),
    bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    labelLarge = TextStyle(fontSize = 16.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
)
@Composable
fun DreamlockTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, typography = Type,
        shapes = Shapes(small = RoundedCornerShape(14.dp), medium = RoundedCornerShape(20.dp),
            large = RoundedCornerShape(24.dp)), content = content)
}
