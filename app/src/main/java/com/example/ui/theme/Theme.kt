package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BentoLightAccent,
    secondary = BentoMediumGrey,
    tertiary = BentoDarkGrey,
    background = BentoBg,
    surface = BentoSurface,
    onPrimary = BentoDarkBlue,
    onSecondary = BentoTextLight,
    onBackground = BentoTextLight,
    onSurface = BentoTextLight,
    outline = BentoBorder
)

private val LightColorScheme = lightColorScheme(
    primary = BentoBlueGradientStart,
    secondary = BentoMediumGrey,
    tertiary = BentoLightAccent,
    background = BentoBg, // Bento theme uses dark/deep background by default
    surface = BentoSurface,
    onPrimary = BentoTextLight,
    onSecondary = BentoTextLight,
    onBackground = BentoTextLight,
    onSurface = BentoTextLight,
    outline = BentoBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
