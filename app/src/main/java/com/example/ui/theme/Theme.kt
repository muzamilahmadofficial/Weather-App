package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * CompositionLocal providing current active AppThemeMode to all composables.
 */
val LocalThemeMode = compositionLocalOf { AppThemeMode.DEEP_ATMOSPHERIC }

// Deep Atmospheric Color Scheme (Midnight Navy & Luminous Sky Cyan)
private val DeepAtmosphericColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color(0xFF031628),
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color.White,
    secondary = AccentSunYellow,
    onSecondary = Color(0xFF1E1000),
    background = DarkBgTop,
    onBackground = DarkTextPrimary,
    surface = DarkCardBg,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkCardBorder,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkTextMuted,
    outlineVariant = Color(0xFF475569)
)

// Royal Azure Color Scheme (Royal Cobalt Sky & High Contrast White)
private val RoyalAzureColorScheme = darkColorScheme(
    primary = AccentCyan,
    onPrimary = Color(0xFF031628),
    primaryContainer = Color(0xFF1D4ED8),
    onPrimaryContainer = Color.White,
    secondary = AccentSunYellow,
    onSecondary = Color(0xFF1E1000),
    background = AzureBgTop,
    onBackground = DarkTextPrimary,
    surface = Color(0xFF16233B),
    onSurface = DarkTextPrimary,
    surfaceVariant = Color(0xFF283B60),
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkTextMuted,
    outlineVariant = Color(0xFF38BDF8)
)

// Clean Day Light Color Scheme (Vivid Sky Blue & Deep Bold Text)
private val CleanDayColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = LightTextPrimary,
    secondary = Color(0xFFEA580C),
    onSecondary = Color.White,
    background = LightBgBottom,
    onBackground = LightTextPrimary,
    surface = LightCardBg,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCardBorder,
    onSurfaceVariant = LightTextSecondary,
    outline = LightTextMuted,
    outlineVariant = Color(0xFFCBD5E1)
)

/**
 * Returns the atmospheric vertical gradient brush matching the active theme mode.
 */
@Composable
fun weatherBackgroundBrush(themeMode: AppThemeMode = LocalThemeMode.current): Brush {
    val isSystemDark = isSystemInDarkTheme()
    return when (themeMode) {
        AppThemeMode.DEEP_ATMOSPHERIC -> {
            Brush.verticalGradient(
                colors = listOf(
                    DarkBgTop,
                    DarkBgMid,
                    DarkBgBottom
                )
            )
        }
        AppThemeMode.ROYAL_AZURE -> {
            Brush.verticalGradient(
                colors = listOf(
                    AzureBgTop,
                    AzureBgMid,
                    AzureBgBottom
                )
            )
        }
        AppThemeMode.CLEAN_LIGHT -> {
            Brush.verticalGradient(
                colors = listOf(
                    LightBgTop,
                    LightBgMid,
                    LightBgBottom
                )
            )
        }
        AppThemeMode.SYSTEM -> {
            if (isSystemDark) {
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBgTop,
                        DarkBgMid,
                        DarkBgBottom
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        LightBgTop,
                        LightBgMid,
                        LightBgBottom
                    )
                )
            }
        }
    }
}

@Composable
fun MyApplicationTheme(
    themeMode: AppThemeMode = AppThemeMode.DEEP_ATMOSPHERIC,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val colorScheme = when (themeMode) {
        AppThemeMode.DEEP_ATMOSPHERIC -> DeepAtmosphericColorScheme
        AppThemeMode.ROYAL_AZURE -> RoyalAzureColorScheme
        AppThemeMode.CLEAN_LIGHT -> CleanDayColorScheme
        AppThemeMode.SYSTEM -> if (isSystemDark) DeepAtmosphericColorScheme else CleanDayColorScheme
    }

    CompositionLocalProvider(LocalThemeMode provides themeMode) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
