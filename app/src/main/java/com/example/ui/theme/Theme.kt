package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    primaryContainer = Gray800,
    onPrimaryContainer = PureWhite,
    secondary = Gray300,
    onSecondary = PureBlack,
    secondaryContainer = Gray900,
    onSecondaryContainer = Gray200,
    tertiary = Gray400,
    onTertiary = PureBlack,
    background = DarkBackground,
    onBackground = PureWhite,
    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Gray400,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = PureBlack,
    onPrimary = PureWhite,
    primaryContainer = Gray200,
    onPrimaryContainer = PureBlack,
    secondary = Gray700,
    onSecondary = PureWhite,
    secondaryContainer = Gray100,
    onSecondaryContainer = Gray900,
    tertiary = Gray600,
    onTertiary = PureWhite,
    background = LightBackground,
    onBackground = PureBlack,
    surface = LightSurface,
    onSurface = PureBlack,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Gray500,
    outline = LightBorder,
    outlineVariant = LightBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to ultra-sleek minimalist dark mode
    dynamicColor: Boolean = false, // Strictly preserve monochrome palette
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
