package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HourlyForecast
import com.example.data.model.TemperatureUnit
import com.example.data.preferences.TimeFormat
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentRainBlue

private fun formatHourDisplay(item: HourlyForecast, timeFormat: TimeFormat): String {
    if (item.hourLabel.equals("Now", ignoreCase = true)) return "Now"
    return try {
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm", java.util.Locale.US)
        val date = parser.parse(item.time) ?: return item.hourLabel
        if (timeFormat == TimeFormat.TWENTY_FOUR_HOUR) {
            java.text.SimpleDateFormat("HH:mm", java.util.Locale.US).format(date)
        } else {
            java.text.SimpleDateFormat("h a", java.util.Locale.US).format(date).lowercase()
        }
    } catch (e: Exception) {
        item.hourLabel
    }
}

@Composable
fun HourlyTemperatureGraph(
    hourlyList: List<HourlyForecast>,
    tempUnit: TemperatureUnit,
    isFrench: Boolean = false,
    timeFormat: TimeFormat = TimeFormat.TWELVE_HOUR,
    modifier: Modifier = Modifier
) {
    if (hourlyList.isEmpty()) return

    var selectedIndex by remember { mutableIntStateOf(0) }
    val selectedItem = hourlyList.getOrNull(selectedIndex) ?: hourlyList.first()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 16.dp)
        ) {
            // Header with Selected Hour details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFrench) "Aujourd'hui" else "Today",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = formatHourDisplay(selectedItem, timeFormat),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tempUnit.format(selectedItem.temperature),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        if (selectedItem.precipitationProbability > 0) {
                            Text(
                                text = "💧 ${selectedItem.precipitationProbability}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable 24-hour graph & timeline
            val scrollState = rememberScrollState()
            val stepWidth = 72.dp
            val totalGraphWidth = stepWidth * hourlyList.size
            val graphHeight = 120.dp

            val minTemp = (hourlyList.minOfOrNull { it.temperature } ?: 15.0) - 2.0
            val maxTemp = (hourlyList.maxOfOrNull { it.temperature } ?: 30.0) + 2.0
            val tempRange = if (maxTemp > minTemp) (maxTemp - minTemp).toFloat() else 1f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column(modifier = Modifier.width(totalGraphWidth)) {
                    // Continuous Bezier Curve Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(graphHeight)
                            .pointerInput(hourlyList) {
                                detectTapGestures { offset ->
                                    val idx = (offset.x / stepWidth.toPx()).toInt()
                                    if (idx in hourlyList.indices) {
                                        selectedIndex = idx
                                    }
                                }
                            }
                    ) {
                        val widthPx = size.width
                        val heightPx = size.height
                        val stepPx = stepWidth.toPx()

                        // Calculate points for each hourly item
                        val points = hourlyList.mapIndexed { i, item ->
                            val x = i * stepPx + stepPx / 2
                            val normalizedY = ((item.temperature - minTemp) / tempRange).toFloat()
                            val y = (heightPx * 0.82f) - (normalizedY * heightPx * 0.58f)
                            Offset(x, y)
                        }

                        // Build smooth Bezier path
                        val curvePath = Path()
                        val fillPath = Path()

                        if (points.isNotEmpty()) {
                            curvePath.moveTo(points[0].x, points[0].y)
                            fillPath.moveTo(points[0].x, heightPx)
                            fillPath.lineTo(points[0].x, points[0].y)

                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val cx = (p0.x + p1.x) / 2
                                curvePath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                                fillPath.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                            }

                            fillPath.lineTo(points.last().x, heightPx)
                            fillPath.close()

                            // Draw Soft Translucent Gradient Fill Under the Curve
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        AccentCyan.copy(alpha = 0.28f),
                                        AccentCyan.copy(alpha = 0.02f)
                                    ),
                                    startY = 0f,
                                    endY = heightPx
                                )
                            )

                            // Draw Smooth Bezier Curve Line
                            drawPath(
                                path = curvePath,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(AccentCyan, AccentRainBlue)
                                ),
                                style = Stroke(
                                    width = 3.5.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )

                            // Draw Circles & Indicators
                            points.forEachIndexed { i, pt ->
                                val isSelected = i == selectedIndex

                                if (isSelected) {
                                    // Vertical dashed guide line
                                    drawLine(
                                        color = AccentRainBlue.copy(alpha = 0.45f),
                                        start = Offset(pt.x, pt.y),
                                        end = Offset(pt.x, heightPx),
                                        strokeWidth = 2.dp.toPx(),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    )

                                    // Outer ring
                                    drawCircle(
                                        color = AccentRainBlue.copy(alpha = 0.3f),
                                        radius = 9.dp.toPx(),
                                        center = pt
                                    )
                                    // Solid inner dot
                                    drawCircle(
                                        color = AccentRainBlue,
                                        radius = 5.dp.toPx(),
                                        center = pt
                                    )
                                } else {
                                    // Unselected small point
                                    drawCircle(
                                        color = Color.White,
                                        radius = 3.dp.toPx(),
                                        center = pt
                                    )
                                    drawCircle(
                                        color = AccentCyan,
                                        radius = 3.dp.toPx(),
                                        center = pt,
                                        style = Stroke(width = 1.5.dp.toPx())
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hourly row with mini weather icon, temperature, and hour label
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        hourlyList.forEachIndexed { index, item ->
                            val isSelected = index == selectedIndex
                            Column(
                                modifier = Modifier
                                    .width(stepWidth)
                                    .clickable { selectedIndex = index }
                                    .padding(vertical = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                WeatherIconView(
                                    condition = item.condition,
                                    modifier = Modifier.padding(bottom = 4.dp),
                                    size = 24.dp
                                )

                                Text(
                                    text = tempUnit.format(item.temperature),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp
                                    )
                                )

                                Text(
                                    text = formatHourDisplay(item, timeFormat),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
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
