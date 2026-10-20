package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.ui.components.LegalTab
import com.example.ui.components.PrivacyPolicyDialog
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertType
import com.example.data.model.CityLocation
import com.example.data.model.TemperatureUnit
import com.example.data.model.WindSpeedUnit
import com.example.data.preferences.AppLanguage
import com.example.data.preferences.TimeFormat
import com.example.ui.theme.AccentRainBlue
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.weatherBackgroundBrush

@Composable
fun SettingsScreen(
    tempUnit: TemperatureUnit,
    windUnit: WindSpeedUnit,
    savedCities: List<CityLocation>,
    hasGpsPermission: Boolean,
    alertsEnabled: Boolean,
    onToggleAlertsEnabled: (Boolean) -> Unit,
    onTriggerTestNotification: (AlertType) -> Unit = {},
    onSetTempUnit: (TemperatureUnit) -> Unit,
    onSetWindUnit: (WindSpeedUnit) -> Unit,
    onRequestGpsLocation: () -> Unit,
    onRemoveSavedCity: (CityLocation) -> Unit,
    onSelectCity: (CityLocation) -> Unit,
    themeMode: AppThemeMode = AppThemeMode.DEEP_ATMOSPHERIC,
    onSetThemeMode: (AppThemeMode) -> Unit = {},
    isFrench: Boolean = false,
    appLanguage: AppLanguage = AppLanguage.ENGLISH,
    onSetLanguage: (AppLanguage) -> Unit = {},
    syncIntervalMinutes: Long = 60L,
    onSetSyncInterval: (Long) -> Unit = {},
    timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
    onSetTimeFormat: (TimeFormat) -> Unit = {},
    isEmergencyModeActive: Boolean = false,
    activeEmergencyReason: String? = null,
    onTriggerEmergencyMode: (String) -> Unit = {},
    onStopEmergencyMode: () -> Unit = {},
    onResetAppData: () -> Unit = {},
    bottomBarPadding: Dp = 144.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var privacyDialogInitialTab by remember { mutableStateOf(LegalTab.PRIVACY) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }
    var showCustomSyncDialog by remember { mutableStateOf(false) }
    var customMinutesInput by remember(syncIntervalMinutes) { mutableStateOf(syncIntervalMinutes.toString()) }
    var customMinutesError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = weatherBackgroundBrush())
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("settings_screen_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = bottomBarPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = com.example.ui.util.WeatherStrings.settingsTitle(isFrench),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = com.example.ui.util.WeatherStrings.settingsSubtitle(isFrench),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // GPS Auto-Location Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(AccentRainBlue.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = "GPS",
                                    tint = AccentRainBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isFrench) "Détection automatique de position" else "Automatic Location Detection",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = if (isFrench) {
                                        if (hasGpsPermission) "Localisation GPS active" else "Autorisation requise pour détection auto"
                                    } else {
                                        if (hasGpsPermission) "Location tracking active" else "Permission required for auto-detect"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (hasGpsPermission) AccentRainBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onRequestGpsLocation),
                            shape = RoundedCornerShape(16.dp),
                            color = AccentRainBlue
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 13.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isFrench) "Détecter ma position maintenant" else "Detect Current Location Now",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Severe Weather Alerts & Push Notifications Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(Color(0xFFFEF2F2), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = "Severe Weather Alerts",
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.severeAlertsTitle(isFrench),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = if (isFrench) {
                                            if (alertsEnabled) "Actif pour la position actuelle" else "Alertes désactivées"
                                        } else {
                                            if (alertsEnabled) "Active for current location" else "Alerts turned off"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (alertsEnabled) AccentRainBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            Switch(
                                checked = alertsEnabled,
                                onCheckedChange = onToggleAlertsEnabled,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AccentRainBlue
                                ),
                                modifier = Modifier.testTag("toggle_weather_alerts_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Sends real-time push notifications when severe thunderstorms, lightning, extreme heat warnings (>38°C), or gale winds approach your detected location.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )

                    }
                }
            }

            // App Visual Theme & Color Contrast Selector
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("theme_selector_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Theme",
                                tint = AccentRainBlue
                            )
                            Column {
                                Text(
                                    text = com.example.ui.util.WeatherStrings.visualThemeSection(isFrench),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = if (isFrench) "Choisissez une palette pour une lisibilité optimale" else "Choose a high-contrast palette for optimal text readability",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            AppThemeMode.values().forEach { mode ->
                                val selected = mode == themeMode
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSetThemeMode(mode) }
                                        .testTag("theme_option_${mode.name}"),
                                    shape = RoundedCornerShape(18.dp),
                                    color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                    border = BorderStroke(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mode.displayName,
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            )
                                            Text(
                                                text = mode.subtitle,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }

                                        // Theme preview swatches
                                        val previewColors = when (mode) {
                                            AppThemeMode.DEEP_ATMOSPHERIC -> listOf(Color(0xFF0A1128), Color(0xFF38BDF8))
                                            AppThemeMode.ROYAL_AZURE -> listOf(Color(0xFF1D4ED8), Color(0xFF38BDF8))
                                            AppThemeMode.CLEAN_LIGHT -> listOf(Color(0xFF7DD3FC), Color(0xFF0284C7))
                                            AppThemeMode.SYSTEM -> listOf(Color(0xFF334155), Color(0xFF94A3B8))
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            previewColors.forEach { col ->
                                                Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .background(col, CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Language Selector Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("language_selector_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = AccentRainBlue
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.languageSection(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AppLanguage.values().forEach { lang ->
                                val selected = lang == appLanguage
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSetLanguage(lang) },
                                    label = {
                                        Text(
                                            text = lang.displayName,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentRainBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Temperature Unit Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = "Temperature",
                                tint = AccentRainBlue
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.tempUnitSection(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            TemperatureUnit.values().forEach { unit ->
                                val selected = unit == tempUnit
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSetTempUnit(unit) },
                                    label = {
                                        Text(
                                            text = if (unit == TemperatureUnit.CELSIUS) "Celsius (°C)" else "Fahrenheit (°F)",
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentRainBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Wind Speed Unit Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Wind speed unit",
                                tint = AccentRainBlue
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.windUnitSection(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            WindSpeedUnit.values().forEach { unit ->
                                val selected = unit == windUnit
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSetWindUnit(unit) },
                                    label = {
                                        Text(
                                            text = unit.label,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentRainBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Time Format Selector Card (12h vs 24h)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("time_format_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = "Time format",
                                tint = AccentRainBlue
                            )
                            Column {
                                Text(
                                    text = com.example.ui.util.WeatherStrings.timeFormatSection(isFrench),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = com.example.ui.util.WeatherStrings.timeFormatSubtitle(isFrench),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TimeFormat.values().forEach { format ->
                                val selected = format == timeFormat
                                FilterChip(
                                    selected = selected,
                                    onClick = { onSetTimeFormat(format) },
                                    label = {
                                        Text(
                                            text = if (isFrench) format.displayNameFr else format.displayNameEn,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentRainBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.testTag("time_format_chip_${format.code}")
                                )
                            }
                        }
                    }
                }
            }

            // Background Synchronization (WorkManager) & Emergency Mode Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("background_sync_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sync Interval",
                                tint = AccentRainBlue
                            )
                            Column {
                                Text(
                                    text = com.example.ui.util.WeatherStrings.syncIntervalSection(isFrench),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = com.example.ui.util.WeatherStrings.syncIntervalSubtitle(isFrench),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // User-Defined Sync Interval Selector with standard presets and custom user definition
                        val standardIntervals = listOf(30L to "30m", 60L to "1h", 120L to "2h", 360L to "6h")
                        val isCustomSelected = syncIntervalMinutes !in listOf(30L, 60L, 120L, 360L)

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                standardIntervals.forEach { (minutes, label) ->
                                    val selected = syncIntervalMinutes == minutes
                                    FilterChip(
                                        selected = selected,
                                        onClick = { onSetSyncInterval(minutes) },
                                        label = {
                                            Text(
                                                text = label,
                                                fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                                                fontSize = 12.sp
                                            )
                                        },
                                        shape = RoundedCornerShape(16.dp),
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = AccentRainBlue,
                                            selectedLabelColor = Color.White,
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            labelColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        modifier = Modifier.testTag("sync_interval_chip_${minutes}m")
                                    )
                                }

                                // Custom option chip
                                FilterChip(
                                    selected = isCustomSelected,
                                    onClick = {
                                        customMinutesInput = syncIntervalMinutes.toString()
                                        customMinutesError = null
                                        showCustomSyncDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = if (isCustomSelected) "${syncIntervalMinutes}m" else com.example.ui.util.WeatherStrings.customSyncChip(isFrench),
                                            fontWeight = if (isCustomSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AccentRainBlue,
                                        selectedLabelColor = Color.White,
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        labelColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.testTag("sync_interval_chip_custom")
                                )
                            }

                            Text(
                                text = if (isFrench) "Intervalle actuel : toutes les $syncIntervalMinutes minutes" else "Active schedule: Every $syncIntervalMinutes minutes",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = AccentRainBlue,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Battery conservation rule banner (enforcing strict minimum 30 minutes)
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = "Battery info",
                                    tint = AccentRainBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = com.example.ui.util.WeatherStrings.syncIntervalMinNotice(isFrench),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        lineHeight = 16.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Critical / Emergency Weather Mode section
                        Text(
                            text = com.example.ui.util.WeatherStrings.emergencyModeSection(isFrench),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status Card for Emergency Mode
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = if (isEmergencyModeActive) Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(
                                1.5.dp,
                                if (isEmergencyModeActive) Color(0xFFEF4444) else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isEmergencyModeActive) Icons.Default.Warning else Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isEmergencyModeActive) Color(0xFFDC2626) else AccentRainBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (isEmergencyModeActive) {
                                            com.example.ui.util.WeatherStrings.emergencyModeActiveTitle(isFrench)
                                        } else {
                                            com.example.ui.util.WeatherStrings.emergencyModeInactiveTitle(isFrench)
                                        },
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isEmergencyModeActive) Color(0xFF991B1B) else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }

                                if (isEmergencyModeActive && !activeEmergencyReason.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = activeEmergencyReason,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFFB91C1C),
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = com.example.ui.util.WeatherStrings.emergencyModeDesc(isFrench),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                if (isEmergencyModeActive) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onStopEmergencyMode() }
                                            .testTag("stop_emergency_mode_button"),
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color(0xFFDC2626)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 11.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = com.example.ui.util.WeatherStrings.stopEmergencyBtn(isFrench),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onTriggerEmergencyMode(
                                                    if (isFrench) "Alerte orage violent avec foudre détectée"
                                                    else "Severe Thunderstorm & Lightning Warning Detected"
                                                )
                                            }
                                            .testTag("trigger_emergency_mode_button"),
                                        shape = RoundedCornerShape(14.dp),
                                        color = AccentRainBlue
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 11.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = com.example.ui.util.WeatherStrings.triggerEmergencyBtn(isFrench),
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Saved Cities Management
            if (savedCities.isNotEmpty()) {
                item {
                    Text(
                        text = com.example.ui.util.WeatherStrings.manageSavedCities(isFrench),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                items(savedCities) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCity(city) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = AccentRainBlue
                                )
                                Column {
                                    Text(
                                        text = city.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = city.country,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }

                            IconButton(onClick = { onRemoveSavedCity(city) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // About Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About",
                                tint = AccentRainBlue
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.aboutSection(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Atmosphere Weather v3.0",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = com.example.ui.util.WeatherStrings.aboutDesc(isFrench),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Notification Status Bar Widget Card (Home screen widget removed, keeps notification widget)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notification_widget_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Notification Status",
                                tint = AccentRainBlue
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.notificationWidgetSection(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = com.example.ui.util.WeatherStrings.notificationWidgetActive(isFrench),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = com.example.ui.util.WeatherStrings.notificationWidgetDesc(isFrench),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Privacy, Legal & Google Play Store Compliance Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("privacy_legal_card"),
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Privacy & Legal",
                                tint = AccentRainBlue
                            )
                            Column {
                                Text(
                                    text = com.example.ui.util.WeatherStrings.privacyLegalSection(isFrench),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = if (isFrench) "Conforme aux politiques Google Play • Zéro pistage" else "Google Play Policy Compliant • Zero Tracking",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AccentRainBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Privacy Policy Row
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    privacyDialogInitialTab = LegalTab.PRIVACY
                                    showPrivacyDialog = true
                                }
                                .testTag("open_privacy_policy_row"),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Security,
                                        contentDescription = null,
                                        tint = AccentRainBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = com.example.ui.util.WeatherStrings.privacyPolicyTitle(isFrench),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = com.example.ui.util.WeatherStrings.privacyPolicyDesc(isFrench),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Terms of Service Row
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    privacyDialogInitialTab = LegalTab.TERMS
                                    showPrivacyDialog = true
                                }
                                .testTag("open_terms_row"),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Gavel,
                                        contentDescription = null,
                                        tint = AccentRainBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = com.example.ui.util.WeatherStrings.termsTitle(isFrench),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        Text(
                                            text = com.example.ui.util.WeatherStrings.termsDesc(isFrench),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Data Attribution & Open Source
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = AccentRainBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.attributionTitle(isFrench),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.attributionDesc(isFrench),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Developer Support Contact (opens email intent)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:muzamilofficially@gmail.com?subject=Atmosphere%20Weather%20Support")
                                    }
                                    try {
                                        context.startActivity(emailIntent)
                                    } catch (e: Exception) {
                                        // Ignore if no email app installed
                                    }
                                }
                                .testTag("contact_developer_row"),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = AccentRainBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.contactSupportTitle(isFrench),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.contactSupportDesc(isFrench),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Clear Local Cache & Reset Data
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showResetConfirmDialog = true }
                                .testTag("reset_data_row"),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.clearCacheTitle(isFrench),
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.clearCacheDesc(isFrench),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 11.5.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom clearance spacer for complete scrollability above curved bottom nav
            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }

        if (showPrivacyDialog) {
            PrivacyPolicyDialog(
                onDismissRequest = { showPrivacyDialog = false },
                isFrench = isFrench,
                initialTab = privacyDialogInitialTab
            )
        }

        if (showResetConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmDialog = false },
                title = {
                    Text(
                        text = com.example.ui.util.WeatherStrings.clearCacheConfirmTitle(isFrench),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = com.example.ui.util.WeatherStrings.clearCacheConfirmDesc(isFrench),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showResetConfirmDialog = false
                            onResetAppData()
                        },
                        modifier = Modifier.testTag("confirm_reset_data_button")
                    ) {
                        Text(
                            text = com.example.ui.util.WeatherStrings.confirmResetBtn(isFrench),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showResetConfirmDialog = false },
                        modifier = Modifier.testTag("cancel_reset_data_button")
                    ) {
                        Text(text = com.example.ui.util.WeatherStrings.cancelBtn(isFrench))
                    }
                }
            )
        }

        if (showCustomSyncDialog) {
            AlertDialog(
                onDismissRequest = { showCustomSyncDialog = false },
                title = {
                    Text(
                        text = com.example.ui.util.WeatherStrings.customSyncDialogTitle(isFrench),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = com.example.ui.util.WeatherStrings.syncIntervalMinNotice(isFrench),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        OutlinedTextField(
                            value = customMinutesInput,
                            onValueChange = { input ->
                                customMinutesInput = input.filter { it.isDigit() }
                                customMinutesError = null
                            },
                            label = { Text(com.example.ui.util.WeatherStrings.customSyncMinutesLabel(isFrench)) },
                            isError = customMinutesError != null,
                            supportingText = customMinutesError?.let { { Text(it) } },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_sync_input")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(30L, 45L, 90L, 180L).forEach { mins ->
                                FilterChip(
                                    selected = customMinutesInput == mins.toString(),
                                    onClick = {
                                        customMinutesInput = mins.toString()
                                        customMinutesError = null
                                    },
                                    label = { Text("${mins}m") }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val mins = customMinutesInput.toLongOrNull()
                            if (mins == null || mins < 30L) {
                                customMinutesError = com.example.ui.util.WeatherStrings.customSyncErrorMin(isFrench)
                            } else {
                                onSetSyncInterval(mins)
                                showCustomSyncDialog = false
                            }
                        },
                        modifier = Modifier.testTag("save_custom_sync_button")
                    ) {
                        Text(
                            text = com.example.ui.util.WeatherStrings.saveBtn(isFrench),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomSyncDialog = false }) {
                        Text(text = com.example.ui.util.WeatherStrings.cancelBtn(isFrench))
                    }
                }
            )
        }
    }
}
