package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertSeverity
import com.example.data.model.WeatherAlert

@Composable
fun WeatherAlertBanner(
    alert: WeatherAlert,
    onTestNotification: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val themeMode = com.example.ui.theme.LocalThemeMode.current
    val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDark = when (themeMode) {
        com.example.ui.theme.AppThemeMode.DEEP_ATMOSPHERIC,
        com.example.ui.theme.AppThemeMode.ROYAL_AZURE -> true
        com.example.ui.theme.AppThemeMode.CLEAN_LIGHT -> false
        com.example.ui.theme.AppThemeMode.SYSTEM -> isSystemDark
    }

    val bannerBg = if (isDark) {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFF3B0707) // Deep crimson dark container
            AlertSeverity.WATCH -> Color(0xFF381A03) // Deep amber dark container
            AlertSeverity.ADVISORY -> Color(0xFF032617) // Deep forest dark container
        }
    } else {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFFFEF2F2) // Crisp light rose
            AlertSeverity.WATCH -> Color(0xFFFFFBEB) // Crisp light warm amber
            AlertSeverity.ADVISORY -> Color(0xFFF0FDF4) // Crisp light mint
        }
    }

    val bannerBorder = if (isDark) {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFFF87171) // Luminous coral red
            AlertSeverity.WATCH -> Color(0xFFFBBF24) // Luminous golden amber
            AlertSeverity.ADVISORY -> Color(0xFF34D399) // Luminous emerald
        }
    } else {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFFDC2626) // Deep crimson
            AlertSeverity.WATCH -> Color(0xFFD97706) // Deep amber
            AlertSeverity.ADVISORY -> Color(0xFF059669) // Deep emerald
        }
    }

    val primaryTextColor = if (isDark) {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFFFFFFFF) // Pure white for perfect readability
            AlertSeverity.WATCH -> Color(0xFFFEF3C7) // Bright golden white
            AlertSeverity.ADVISORY -> Color(0xFFD1FAE5) // Bright mint white
        }
    } else {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFF7F1D1D) // High contrast blood red
            AlertSeverity.WATCH -> Color(0xFF78350F) // High contrast dark amber
            AlertSeverity.ADVISORY -> Color(0xFF064E3B) // High contrast dark green
        }
    }

    val secondaryTextColor = if (isDark) {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFFFECACA) // Crisp bright pink
            AlertSeverity.WATCH -> Color(0xFFFDE68A) // Crisp bright warm yellow
            AlertSeverity.ADVISORY -> Color(0xFFA7F3D0) // Crisp bright light mint
        }
    } else {
        when (alert.severity) {
            AlertSeverity.EMERGENCY,
            AlertSeverity.WARNING -> Color(0xFF991B1B)
            AlertSeverity.WATCH -> Color(0xFF92400E)
            AlertSeverity.ADVISORY -> Color(0xFF065F46)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_alert_banner")
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bannerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(bannerBorder.copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = bannerBorder,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = bannerBorder
                        ) {
                            Text(
                                text = alert.severity.displayName.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor,
                                fontSize = 15.sp
                            )
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = primaryTextColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = alert.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = secondaryTextColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            // Expanded precautions and action
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    if (alert.precautions.isNotEmpty()) {
                        Text(
                            text = "SAFETY INSTRUCTIONS:",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = primaryTextColor,
                                fontSize = 11.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        alert.precautions.forEach { instruction ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "•",
                                    color = bannerBorder,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = instruction,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = secondaryTextColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
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
