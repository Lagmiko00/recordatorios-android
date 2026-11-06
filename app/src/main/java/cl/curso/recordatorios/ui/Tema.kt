package cl.curso.recordatorios.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val claro = lightColorScheme(
    primary = Color(0xFF205B4D),
    onPrimary = Color.White,
    background = Color(0xFFF7F8F5),
    onBackground = Color(0xFF19231F),
    surface = Color(0xFFF7F8F5),
    onSurface = Color(0xFF19231F),
    surfaceVariant = Color(0xFFE5EBE6),
    onSurfaceVariant = Color(0xFF3F4B43),
    outline = Color(0xFF6B7870),
)

private val oscuro = darkColorScheme(
    primary = Color(0xFF9ED6BD),
    onPrimary = Color(0xFF003829),
    background = Color(0xFF111914),
    onBackground = Color(0xFFE0E8DF),
    surface = Color(0xFF111914),
    onSurface = Color(0xFFE0E8DF),
    surfaceVariant = Color(0xFF2C3930),
    onSurfaceVariant = Color(0xFFBFCCC1),
    outline = Color(0xFF8C9B90),
)

@Composable
fun TemaRecordatorios(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) oscuro else claro,
        content = content,
    )
}
