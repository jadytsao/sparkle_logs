package com.sparklelog.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SparkleDarkColorScheme = darkColorScheme(
    primary = Accent,
    secondary = AccentSecondary,
    tertiary = AccentTertiary,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceContainer = SurfaceDark,
    onBackground = InkDark,
    onSurface = InkDark,
    onSurfaceVariant = MutedDark,
    outline = BorderDark
)

private val SparkleLightColorScheme = lightColorScheme(
    primary = Accent,
    secondary = AccentSecondary,
    tertiary = AccentTertiary,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceContainer = SurfaceLight,
    onBackground = InkLight,
    onSurface = InkLight,
    onSurfaceVariant = MutedLight,
    outline = BorderLight
)

@Composable
fun SparkleLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SparkleDarkColorScheme else SparkleLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
