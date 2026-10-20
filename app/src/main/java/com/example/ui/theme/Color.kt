package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// HIGH-CONTRAST PALETTES FOR MAXIMUM READABILITY & MODERN AESTHETIC
// =========================================================================

// --- 1. Deep Atmospheric Palette (Midnight Sapphire & Electric Cyan) ---
val DarkBgTop = Color(0xFF0A1128)        // Deep midnight sapphire
val DarkBgMid = Color(0xFF101F42)        // Atmospheric dark navy
val DarkBgBottom = Color(0xFF0F172A)     // Deep slate base
val DarkCardBg = Color(0xFF1E293B)       // Elevated slate card surface
val DarkCardBorder = Color(0xFF334155)   // High-contrast slate border
val DarkTextPrimary = Color(0xFFFFFFFF)  // Pure crisp white (18:1 contrast)
val DarkTextSecondary = Color(0xFFE2E8F0)// Bright silver slate (12:1 contrast)
val DarkTextMuted = Color(0xFFCBD5E1)    // Light silver-gray (8:1 contrast)

// --- 2. Clean Day Palette (Vivid Sky Blue & Deep Contrast Navy) ---
val LightBgTop = Color(0xFF7DD3FC)       // Vivid daytime sky blue top
val LightBgMid = Color(0xFFBAE6FD)       // Crisp sky blue mid
val LightBgBottom = Color(0xFFE0F2FE)    // Clean atmospheric sky base
val LightCardBg = Color(0xFFFFFFFF)      // Pure white card
val LightCardBorder = Color(0xFF93C5FD)  // Clear sky blue border
val LightTextPrimary = Color(0xFF091429) // Deep midnight navy (18:1 contrast)
val LightTextSecondary = Color(0xFF1E293B)// Deep slate (13:1 contrast)
val LightTextMuted = Color(0xFF334155)   // Dark slate (8:1 contrast)

// --- 3. Royal Azure Palette (Cobalt Sky & Electric Accents) ---
val AzureBgTop = Color(0xFF1E3A8A)       // Royal Indigo
val AzureBgMid = Color(0xFF1D4ED8)       // Royal Cobalt Azure
val AzureBgBottom = Color(0xFF0F172A)    // Deep Navy Base

// --- Vibrant Weather Accent Colors (High saturation, high readability) ---
val AccentRainBlue = Color(0xFF0284C7)      // Vibrant Azure
val AccentCyan = Color(0xFF38BDF8)          // Electric Sky Cyan (ultra bright & legible)
val AccentSunYellow = Color(0xFFFBBF24)     // Radiant Golden Amber
val AccentSunOrange = Color(0xFFFB923C)     // Solar Sunset Orange
val AccentStormPurple = Color(0xFF818CF8)   // Atmospheric Indigo Purple
val AccentSuccessGreen = Color(0xFF34D399)  // Clean Emerald Green
val AccentWarningRed = Color(0xFFF87171)    // High-Visibility Warning Red
val AccentEmergencyRed = Color(0xFFEF4444)  // Severe Emergency Red

/**
 * App Theme Modes available for user selection.
 */
enum class AppThemeMode(val displayName: String, val subtitle: String) {
    DEEP_ATMOSPHERIC("Deep Atmospheric", "Midnight Navy & Electric Cyan (High Contrast)"),
    ROYAL_AZURE("Royal Azure", "Vibrant Cobalt Sky & Crisp White"),
    CLEAN_LIGHT("Clean Day", "Bright Daytime Sky & Dark Bold Text"),
    SYSTEM("System Default", "Follows device dark/light settings")
}
