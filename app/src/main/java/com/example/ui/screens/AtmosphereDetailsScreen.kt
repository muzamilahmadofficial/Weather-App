package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlertType
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherAlert
import com.example.data.model.WindSpeedUnit
import com.example.data.preferences.TimeFormat
import com.example.ui.components.WeatherAlertBanner
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRainBlue
import com.example.ui.theme.AccentSunOrange
import com.example.ui.theme.AccentSunYellow
import com.example.ui.theme.weatherBackgroundBrush

@Composable
fun AtmosphereDetailsScreen(
    report: FullWeatherReport?,
    tempUnit: TemperatureUnit,
    windUnit: WindSpeedUnit,
    alerts: List<WeatherAlert> = emptyList(),
    onTriggerTestNotification: (AlertType) -> Unit = {},
    isFrench: Boolean = false,
    timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
    bottomBarPadding: Dp = 144.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = weatherBackgroundBrush())
            .statusBarsPadding()
    ) {
        if (report == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (isFrench) "Aucun rapport météo chargé." else "No weather report loaded.",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            return@Box
        }

        val current = report.current
        val today = report.daily.firstOrNull()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("atmosphere_details_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = bottomBarPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = com.example.ui.util.WeatherStrings.atmosphericDetails(isFrench),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = "${report.city.name} • ${com.example.ui.util.WeatherStrings.liveTelemetry(isFrench)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            // Active Alerts Banner if present
            if (alerts.isNotEmpty()) {
                items(alerts) { alert ->
                    WeatherAlertBanner(alert = alert)
                }
            }

            // Wind Speed & Direction Card
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Air,
                                    contentDescription = "Wind",
                                    tint = AccentRainBlue
                                )
                                Text(
                                    text = com.example.ui.util.WeatherStrings.windSpeedTitle(isFrench),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = "Wind direction",
                                tint = AccentRainBlue,
                                modifier = Modifier
                                    .size(24.dp)
                                    .rotate(current.windDirection.toFloat())
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = windUnit.format(current.windSpeed),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = if (isFrench) {
                                "Rafales jusqu'à ${windUnit.format(current.windGusts)} • Direction ${current.windDirection}°"
                            } else {
                                "Gusts up to ${windUnit.format(current.windGusts)} • Direction ${current.windDirection}°"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Humidity & Comfort Gauge Card
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
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = "Humidity",
                                tint = AccentCyan
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.humidityTitle(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${current.relativeHumidity}%",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { current.relativeHumidity / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = AccentCyan,
                            trackColor = AccentCyan.copy(alpha = 0.2f),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val humidityDesc = when {
                            current.relativeHumidity < 30 -> if (isFrench) "Air sec : hydratez-vous régulièrement." else "Dry air: moisturize and stay hydrated."
                            current.relativeHumidity <= 60 -> if (isFrench) "Plage de confort idéale." else "Optimal comfort range."
                            current.relativeHumidity <= 80 -> if (isFrench) "Humide : humidité perceptible dans l'air." else "Humid: moisture in the air is palpable."
                            else -> if (isFrench) "Humidité très élevée : atmosphère moite." else "Very high humidity: sticky and humid atmosphere."
                        }

                        Text(
                            text = humidityDesc,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // UV Index Card
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
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "UV",
                                tint = AccentSunYellow
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.uvSafetyTitle(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "%.1f".format(current.uvIndex),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { (current.uvIndex / 11f).coerceIn(0.0, 1.0).toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = AccentSunYellow,
                            trackColor = AccentSunYellow.copy(alpha = 0.2f),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val uvAdvice = when {
                            current.uvIndex <= 2.0 -> if (isFrench) "Risque faible. Aucune protection requise." else "Low risk. No protection required."
                            current.uvIndex <= 5.0 -> if (isFrench) "Risque modéré. Portez de la crème solaire et des lunettes." else "Moderate risk. Wear sunscreen and sunglasses."
                            current.uvIndex <= 7.0 -> if (isFrench) "Risque élevé. Protection indispensable ; cherchez l'ombre à midi." else "High risk. Protection essential; seek shade around midday."
                            else -> if (isFrench) "Risque très élevé à extrême. Prenez des précautions maximales." else "Very High to Extreme risk. Take full protective precautions."
                        }

                        Text(
                            text = uvAdvice,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Sun & Moon Times Card
            if (today?.sunrise?.isNotBlank() == true || today?.sunset?.isNotBlank() == true) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = "Sunrise",
                                    tint = AccentSunOrange,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = com.example.ui.util.WeatherStrings.sunrise(isFrench),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = formatSunTime(today?.sunrise, timeFormat),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.NightsStay,
                                    contentDescription = "Sunset",
                                    tint = AccentRainBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = com.example.ui.util.WeatherStrings.sunset(isFrench),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = formatSunTime(today?.sunset, timeFormat),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Pressure & Air Density Card
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
                                imageVector = Icons.Default.Compress,
                                contentDescription = "Pressure",
                                tint = AccentRainBlue
                            )
                            Text(
                                text = com.example.ui.util.WeatherStrings.pressureTitle(isFrench),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "${current.pressure.toInt()} hPa",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Text(
                            text = if (isFrench) {
                                "La pression atmosphérique normale au niveau de la mer est de 1013 hPa."
                            } else {
                                "Standard sea-level atmospheric pressure is 1013 hPa."
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            // Bottom clearance spacer for complete scrollability above curved bottom nav
            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

private fun formatSunTime(isoTime: String?, timeFormat: TimeFormat): String {
    if (isoTime.isNullOrBlank()) return "--:--"
    return try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm", java.util.Locale.US)
        val date = parser.parse(isoTime) ?: return isoTime
        val pattern = if (timeFormat == TimeFormat.TWENTY_FOUR_HOUR) "HH:mm" else "hh:mm a"
        java.text.SimpleDateFormat(pattern, java.util.Locale.US).format(date)
    } catch (e: Exception) {
        isoTime
    }
}
