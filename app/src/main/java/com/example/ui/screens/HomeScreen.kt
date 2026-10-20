package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import com.example.data.model.AlertType
import com.example.data.model.CityLocation
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherAlert
import com.example.data.model.WindSpeedUnit
import com.example.ui.components.DailyForecastCard
import com.example.ui.components.HourlyTemperatureGraph
import com.example.ui.components.WeatherAlertBanner
import com.example.ui.components.WeatherIconView
import com.example.ui.components.WeatherMetricCard
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRainBlue
import com.example.ui.theme.AccentSunOrange
import com.example.ui.theme.AccentSunYellow
import com.example.ui.theme.weatherBackgroundBrush
import com.example.ui.viewmodel.WeatherUiState
import com.example.data.preferences.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: WeatherUiState,
    currentCity: CityLocation,
    tempUnit: TemperatureUnit,
    windUnit: WindSpeedUnit,
    alerts: List<WeatherAlert>,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean = false,
    currentTimeMillis: Long = System.currentTimeMillis(),
    lastSyncedMillis: Long = System.currentTimeMillis(),
    syncStatusMessage: String = "Synced just now",
    onRequestGpsLocation: () -> Unit,
    onTriggerTestNotification: (AlertType) -> Unit = {},
    hasLocationPermission: Boolean,
    isFrench: Boolean = false,
    timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
    bottomBarPadding: Dp = 176.dp,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier
            .fillMaxSize()
            .background(brush = weatherBackgroundBrush())
            .statusBarsPadding(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                color = AccentRainBlue,
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    ) {
        when (uiState) {
            is WeatherUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = AccentRainBlue,
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = if (isFrench) "Mise à jour de l'atmosphère..." else "Updating atmosphere...",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            is WeatherUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WeatherIconView(
                            condition = com.example.data.model.WeatherCondition.RAIN_HEAVY,
                            size = 80.dp
                        )
                        Text(
                            text = if (isFrench) "Météo indisponible" else "Weather Unavailable",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(
                            onClick = onRefresh,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentRainBlue),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("retry_button")
                        ) {
                            Text(
                                if (isFrench) "Réessayer" else "Retry Weather",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            is WeatherUiState.Success -> {
                WeatherContent(
                    report = uiState.report,
                    tempUnit = tempUnit,
                    windUnit = windUnit,
                    alerts = alerts,
                    isSaved = isSaved,
                    onToggleSave = onToggleSave,
                    onRefresh = onRefresh,
                    isRefreshing = isRefreshing,
                    currentTimeMillis = currentTimeMillis,
                    lastSyncedMillis = lastSyncedMillis,
                    syncStatusMessage = syncStatusMessage,
                    onRequestGpsLocation = onRequestGpsLocation,
                    onTriggerTestNotification = onTriggerTestNotification,
                    hasLocationPermission = hasLocationPermission,
                    isFrench = isFrench,
                    timeFormat = timeFormat,
                    bottomBarPadding = bottomBarPadding
                )
            }
        }
    }
}

@Composable
private fun WeatherContent(
    report: FullWeatherReport,
    tempUnit: TemperatureUnit,
    windUnit: WindSpeedUnit,
    alerts: List<WeatherAlert>,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    currentTimeMillis: Long,
    lastSyncedMillis: Long,
    syncStatusMessage: String,
    onRequestGpsLocation: () -> Unit,
    onTriggerTestNotification: (AlertType) -> Unit,
    hasLocationPermission: Boolean,
    isFrench: Boolean,
    timeFormat: TimeFormat,
    bottomBarPadding: Dp
) {
    val current = report.current
    val todayForecast = report.daily.firstOrNull()

    val liveClockFormatted = remember(currentTimeMillis, timeFormat, isFrench) {
        val pattern = if (timeFormat == TimeFormat.TWENTY_FOUR_HOUR) {
            "EEE, MMM d • HH:mm"
        } else {
            "EEE, MMM d • hh:mm a"
        }
        val locale = if (isFrench) Locale.FRANCE else Locale.getDefault()
        SimpleDateFormat(pattern, locale).format(Date(currentTimeMillis))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "refresh_spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_weather_list"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = bottomBarPadding),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Visual Syncing Banner / Loading indicator when refreshing or background operation is ongoing
        item {
            AnimatedVisibility(
                visible = isRefreshing,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("syncing_operation_banner"),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, AccentRainBlue.copy(alpha = 0.5f)),
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp,
                            color = AccentRainBlue
                        )
                        Column {
                            Text(
                                text = com.example.ui.util.WeatherStrings.syncingWeather(isFrench),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.updatingAtmosphereSubtitle(isFrench),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Active Severe Weather Alert Banners
        if (alerts.isNotEmpty()) {
            items(alerts) { alert ->
                WeatherAlertBanner(alert = alert)
            }
        }
        // GPS permission banner if not yet enabled
        if (!hasLocationPermission) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onRequestGpsLocation),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(AccentRainBlue.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Auto GPS",
                                tint = AccentRainBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = com.example.ui.util.WeatherStrings.useAutoGps(isFrench),
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.useAutoGpsSub(isFrench),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // Top App Bar / Location Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = report.city.displayTitle,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 21.sp
                            ),
                            maxLines = 1
                        )
                        if (report.city.isCurrentGpsLocation) {
                            Text(
                                text = com.example.ui.util.WeatherStrings.currentGpsLocation(isFrench),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        } else {
                            Surface(
                                onClick = onRequestGpsLocation,
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .testTag("return_home_location_badge")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = com.example.ui.util.WeatherStrings.returnToGps(isFrench),
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = com.example.ui.util.WeatherStrings.returnToGps(isFrench),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!report.city.isCurrentGpsLocation) {
                        IconButton(
                            onClick = onRequestGpsLocation,
                            modifier = Modifier
                                .testTag("return_home_gps_button")
                                .size(42.dp)
                                .background(MaterialTheme.colorScheme.surface, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Return to my location",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onToggleSave,
                        modifier = Modifier
                            .testTag("save_city_button")
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save city",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onRefresh,
                        enabled = !isRefreshing,
                        modifier = Modifier
                            .testTag("refresh_weather_button")
                            .size(42.dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = if (isRefreshing) AccentRainBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(22.dp)
                                .rotate(if (isRefreshing) rotationAngle else 0f)
                        )
                    }
                }
            }
        }

        // Hero Weather Section (Matching Screenshot)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Big Current Temperature
                    Text(
                        text = tempUnit.format(current.temperature),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 64.sp
                        )
                    )

                    // Condition Title
                    Text(
                        text = current.condition.localizedTitle(isFrench),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 20.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // High/Low and Feels Like
                    val minT = todayForecast?.minTemperature ?: (current.temperature - 3.0)
                    val maxT = todayForecast?.maxTemperature ?: (current.temperature + 4.0)

                    Text(
                        text = "${tempUnit.format(maxT)} / ${tempUnit.format(minT)}  •  ${com.example.ui.util.WeatherStrings.feelsLike(isFrench)} ${tempUnit.format(current.apparentTemperature)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Live Synchronized Time & Status Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(
                                    if (isRefreshing) AccentRainBlue else Color(0xFF10B981),
                                    CircleShape
                                )
                        )
                        Text(
                            text = "$liveClockFormatted  •  $syncStatusMessage",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // Stylized 3D Illustration
                WeatherIconView(
                    condition = current.condition,
                    isDay = current.isDay,
                    size = 120.dp,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // Weather Metrics Horizontal Carousel (UV, Humidity, Wind, Pressure, Visibility)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // UV Index Pill
                item {
                    val uvDesc = when {
                        current.uvIndex <= 2.0 -> if (isFrench) "Faible" else "Low"
                        current.uvIndex <= 5.0 -> if (isFrench) "Modéré" else "Moderate"
                        current.uvIndex <= 7.0 -> if (isFrench) "Élevé" else "High"
                        else -> if (isFrench) "Très élevé" else "Very High"
                    }
                    WeatherMetricCard(
                        title = com.example.ui.util.WeatherStrings.uvIndex(isFrench),
                        value = uvDesc,
                        subtitle = "%.1f".format(current.uvIndex),
                        icon = Icons.Default.WbSunny,
                        iconTint = AccentSunYellow,
                        testTag = "metric_uv_index"
                    )
                }

                // Humidity Pill
                item {
                    WeatherMetricCard(
                        title = com.example.ui.util.WeatherStrings.humidity(isFrench),
                        value = "${current.relativeHumidity}%",
                        icon = Icons.Default.WaterDrop,
                        iconTint = AccentCyan,
                        testTag = "metric_humidity"
                    )
                }

                // Wind Speed Pill
                item {
                    WeatherMetricCard(
                        title = com.example.ui.util.WeatherStrings.wind(isFrench),
                        value = windUnit.format(current.windSpeed),
                        icon = Icons.Default.Air,
                        iconTint = AccentRainBlue,
                        testTag = "metric_wind_speed"
                    )
                }

                // Air Pressure Pill
                item {
                    WeatherMetricCard(
                        title = com.example.ui.util.WeatherStrings.pressure(isFrench),
                        value = "${current.pressure.toInt()} hPa",
                        icon = Icons.Default.Compress,
                        iconTint = AccentSunOrange,
                        testTag = "metric_pressure"
                    )
                }

                // Feels like
                item {
                    WeatherMetricCard(
                        title = com.example.ui.util.WeatherStrings.feelsLike(isFrench),
                        value = tempUnit.format(current.apparentTemperature),
                        icon = Icons.Default.Thermostat,
                        iconTint = AccentSunOrange,
                        testTag = "metric_feels_like"
                    )
                }
            }
        }

        // Hourly Temperature Curve Graph ("Today" Bezier Line Chart)
        item {
            HourlyTemperatureGraph(
                hourlyList = report.hourly,
                tempUnit = tempUnit,
                isFrench = isFrench,
                timeFormat = timeFormat
            )
        }

        // 7-Day Daily Forecast Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = com.example.ui.util.WeatherStrings.dailyForecast(isFrench),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(report.daily) { day ->
                        DailyForecastCard(
                            forecast = day,
                            tempUnit = tempUnit,
                            isFrench = isFrench
                        )
                    }
                }
            }
        }

        // Additional bottom clearance spacer ensuring last card is fully visible above bottom nav on tall screens
        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
