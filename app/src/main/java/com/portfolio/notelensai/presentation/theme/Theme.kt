package com.portfolio.notelensai.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Indigo,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Lavender,
    onPrimaryContainer = Ink,
    secondary = androidx.compose.ui.graphics.Color(0xFF5F5E71),
    background = Canvas,
    onBackground = Ink,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = Ink,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE8E7EE),
    onSurfaceVariant = MutedInk,
    error = ErrorRed,
)

private val DarkColors = darkColorScheme(
    primary = IndigoDark,
    onPrimary = androidx.compose.ui.graphics.Color(0xFF242778),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF3E4198),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFE1E1FF),
    background = DarkCanvas,
    onBackground = androidx.compose.ui.graphics.Color(0xFFE6E1E9),
    surface = DarkSurface,
    onSurface = androidx.compose.ui.graphics.Color(0xFFE6E1E9),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF47464F),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFC8C5D0),
)

@Composable
fun NoteLensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = NoteLensTypography,
        content = content,
    )
}

