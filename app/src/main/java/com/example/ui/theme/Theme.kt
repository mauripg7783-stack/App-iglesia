package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CustomInversePrimary,
    onPrimary = Color(0xFF002201),
    primaryContainer = CustomPrimaryContainer,
    onPrimaryContainer = CustomOnPrimaryContainer,
    secondary = CustomSecondaryContainer,
    onSecondary = Color(0xFF001C39),
    secondaryContainer = Color(0xFF004883),
    onSecondaryContainer = CustomOnSecondaryContainer,
    tertiary = CustomTertiaryContainer,
    onTertiary = Color(0xFF301400),
    background = CustomInverseSurface,
    onBackground = CustomInverseOnSurface,
    surface = CustomInverseSurface,
    onSurface = CustomInverseOnSurface,
    surfaceVariant = Color(0xFF42493E),
    onSurfaceVariant = CustomOutlineVariant,
    outline = CustomOutline,
    outlineVariant = CustomOutlineVariant,
    error = CustomError,
    onError = CustomOnError,
    errorContainer = CustomErrorContainer,
    onErrorContainer = CustomOnErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = CustomPrimary,
    onPrimary = CustomOnPrimary,
    primaryContainer = CustomPrimaryContainer,
    onPrimaryContainer = CustomOnPrimaryContainer,
    secondary = CustomSecondary,
    onSecondary = CustomOnSecondary,
    secondaryContainer = CustomSecondaryContainer,
    onSecondaryContainer = CustomOnSecondaryContainer,
    tertiary = CustomTertiary,
    onTertiary = CustomOnTertiary,
    background = CustomBackground,
    onBackground = CustomOnBackground,
    surface = CustomSurface,
    onSurface = CustomOnSurface,
    surfaceVariant = CustomSurfaceVariant,
    onSurfaceVariant = CustomOnSurfaceVariant,
    outline = CustomOutline,
    outlineVariant = CustomOutlineVariant,
    error = CustomError,
    onError = CustomOnError,
    errorContainer = CustomErrorContainer,
    onErrorContainer = CustomOnErrorContainer
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to preserve our custom brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
