package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.WeatherCondition
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherIconView(
    condition: WeatherCondition,
    isDay: Boolean = true,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Box(modifier = modifier.size(size)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            when (condition) {
                WeatherCondition.CLEAR_SKY -> {
                    if (isDay) drawSunnySun(this) else drawMoonNight(this)
                }
                WeatherCondition.MAINLY_CLEAR,
                WeatherCondition.PARTLY_CLOUDY -> {
                    drawPartlyCloudy(this, isDay)
                }
                WeatherCondition.OVERCAST -> {
                    drawOvercastCloud(this)
                }
                WeatherCondition.DRIZZLE_LIGHT,
                WeatherCondition.DRIZZLE_MODERATE,
                WeatherCondition.DRIZZLE_DENSE,
                WeatherCondition.RAIN_SLIGHT,
                WeatherCondition.RAIN_MODERATE -> {
                    drawRainy(this, isHeavy = false)
                }
                WeatherCondition.RAIN_HEAVY -> {
                    drawRainy(this, isHeavy = true)
                }
                WeatherCondition.THUNDERSTORM,
                WeatherCondition.THUNDERSTORM_HAIL -> {
                    drawThunderstorm(this)
                }
                WeatherCondition.SNOW_SLIGHT,
                WeatherCondition.SNOW_MODERATE,
                WeatherCondition.SNOW_HEAVY -> {
                    drawSnowy(this)
                }
                WeatherCondition.FOG,
                WeatherCondition.DEPOSITING_RIME_FOG -> {
                    drawFoggy(this)
                }
                else -> {
                    drawOvercastCloud(this)
                }
            }
        }
    }
}

private fun drawSunnySun(drawScope: DrawScope) {
    val w = drawScope.size.width
    val h = drawScope.size.height
    val center = Offset(w * 0.5f, h * 0.5f)
    val radius = w * 0.30f

    // Ambient glow
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x66FDE047), Color(0x00FDE047)),
            center = center,
            radius = radius * 1.55f
        ),
        radius = radius * 1.55f,
        center = center
    )

    // Sun rays
    val rayCount = 8
    val rayInner = radius * 1.15f
    val rayOuter = radius * 1.38f
    for (i in 0 until rayCount) {
        val angle = (i * (360.0 / rayCount) * Math.PI / 180.0)
        val start = Offset(
            x = center.x + (rayInner * cos(angle)).toFloat(),
            y = center.y + (rayInner * sin(angle)).toFloat()
        )
        val end = Offset(
            x = center.x + (rayOuter * cos(angle)).toFloat(),
            y = center.y + (rayOuter * sin(angle)).toFloat()
        )
        drawScope.drawLine(
            color = Color(0xFFFBBF24),
            start = start,
            end = end,
            strokeWidth = w * 0.065f,
            cap = StrokeCap.Round
        )
    }

    // 3D Sun Body
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFEF08A), Color(0xFFFBBF24), Color(0xFFEA580C)),
            center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
            radius = radius * 1.2f
        ),
        radius = radius,
        center = center
    )
}

private fun drawMoonNight(drawScope: DrawScope) {
    val w = drawScope.size.width
    val h = drawScope.size.height
    val center = Offset(w * 0.5f, h * 0.5f)
    val radius = w * 0.32f

    // Moon Glow
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x44E0F2FE), Color(0x000284C7)),
            center = center,
            radius = radius * 1.45f
        ),
        radius = radius * 1.45f,
        center = center
    )

    // Crescent Moon path
    val path = Path().apply {
        addOval(androidx.compose.ui.geometry.Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
    }
    drawScope.drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFF1F5F9), Color(0xFF93C5FD)),
            start = Offset(center.x - radius, center.y - radius),
            end = Offset(center.x + radius, center.y + radius)
        )
    )

    // Shadow cutout for crescent
    drawScope.drawCircle(
        color = Color(0xFF1E293B),
        radius = radius * 0.85f,
        center = Offset(center.x + radius * 0.38f, center.y - radius * 0.25f)
    )
}

private fun drawPartlyCloudy(drawScope: DrawScope, isDay: Boolean) {
    val w = drawScope.size.width
    val h = drawScope.size.height

    // Sun in the background
    val sunCenter = Offset(w * 0.68f, h * 0.34f)
    val sunRadius = w * 0.22f

    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = if (isDay) {
                listOf(Color(0xFFFEF08A), Color(0xFFFBBF24), Color(0xFFF59E0B))
            } else {
                listOf(Color(0xFFE2E8F0), Color(0xFF93C5FD))
            },
            center = Offset(sunCenter.x - sunRadius * 0.2f, sunCenter.y - sunRadius * 0.2f),
            radius = sunRadius * 1.1f
        ),
        radius = sunRadius,
        center = sunCenter
    )

    // Foreground Cloud
    drawPuffyCloud(
        drawScope = drawScope,
        baseOffset = Offset(w * 0.12f, h * 0.38f),
        scale = 0.78f
    )
}

private fun drawOvercastCloud(drawScope: DrawScope) {
    val w = drawScope.size.width
    val h = drawScope.size.height

    // Secondary back cloud
    drawPuffyCloud(
        drawScope = drawScope,
        baseOffset = Offset(w * 0.22f, h * 0.20f),
        scale = 0.65f,
        tint = Color(0xFFCBD5E1)
    )

    // Primary front cloud
    drawPuffyCloud(
        drawScope = drawScope,
        baseOffset = Offset(w * 0.10f, h * 0.34f),
        scale = 0.82f
    )
}

private fun drawRainy(drawScope: DrawScope, isHeavy: Boolean) {
    val w = drawScope.size.width
    val h = drawScope.size.height

    // Back dark cloud
    drawPuffyCloud(
        drawScope = drawScope,
        baseOffset = Offset(w * 0.10f, h * 0.20f),
        scale = 0.80f,
        tint = Color(0xFF94A3B8)
    )

    // Front cloud
    drawPuffyCloud(
        drawScope = drawScope,
        baseOffset = Offset(w * 0.08f, h * 0.24f),
        scale = 0.78f
    )

    // Raindrops (capsules falling at an angle)
    val dropCount = if (isHeavy) 5 else 3
    val startX = w * 0.22f
    val spacing = (w * 0.52f) / dropCount
    for (i in 0 until dropCount) {
        val dx = startX + i * spacing
        val dy = h * 0.72f + (if (i % 2 == 0) 0f else h * 0.06f)
        drawScope.drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                start = Offset(dx, dy),
                end = Offset(dx - w * 0.06f, dy + h * 0.18f)
            ),
            start = Offset(dx, dy),
            end = Offset(dx - w * 0.06f, dy + h * 0.18f),
            strokeWidth = w * 0.045f,
            cap = StrokeCap.Round
        )
    }
}

private fun drawThunderstorm(drawScope: DrawScope) {
    val w = drawScope.size.width
    val h = drawScope.size.height

    // Dark Storm Cloud
    drawPuffyCloud(
        drawScope = drawScope,
        baseOffset = Offset(w * 0.08f, h * 0.20f),
        scale = 0.80f,
        tint = Color(0xFF64748B)
    )

    // Golden Sharp Lightning Bolt
    val bolt = Path().apply {
        moveTo(w * 0.56f, h * 0.54f)
        lineTo(w * 0.44f, h * 0.72f)
        lineTo(w * 0.52f, h * 0.72f)
        lineTo(w * 0.38f, h * 0.96f)
        lineTo(w * 0.48f, h * 0.76f)
        lineTo(w * 0.40f, h * 0.76f)
        close()
    }

    // Glow around lightning
    drawScope.drawPath(
        path = bolt,
        color = Color(0x66FEF08A),
        style = Stroke(width = w * 0.04f, cap = StrokeCap.Round)
    )

    // Filled bolt
    drawScope.drawPath(
        path = bolt,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFEF08A), Color(0xFFF59E0B)),
            start = Offset(w * 0.56f, h * 0.54f),
            end = Offset(w * 0.38f, h * 0.96f)
        )
    )

    // Raindrops around the bolt
    val dropX1 = w * 0.24f
    val dropY1 = h * 0.74f
    drawScope.drawLine(
        color = Color(0xFF38BDF8),
        start = Offset(dropX1, dropY1),
        end = Offset(dropX1 - w * 0.05f, dropY1 + h * 0.14f),
        strokeWidth = w * 0.04f,
        cap = StrokeCap.Round
    )

    val dropX2 = w * 0.72f
    val dropY2 = h * 0.72f
    drawScope.drawLine(
        color = Color(0xFF38BDF8),
        start = Offset(dropX2, dropY2),
        end = Offset(dropX2 - w * 0.05f, dropY2 + h * 0.14f),
        strokeWidth = w * 0.04f,
        cap = StrokeCap.Round
    )
}

private fun drawSnowy(drawScope: DrawScope) {
    val w = drawScope.size.width
    val h = drawScope.size.height

    // Cold blue-grey cloud
    drawPuffyCloud(
        drawScope = drawScope,
        baseOffset = Offset(w * 0.08f, h * 0.22f),
        scale = 0.80f,
        tint = Color(0xFFCBD5E1)
    )

    // Snow dots
    val snowPoints = listOf(
        Offset(w * 0.28f, h * 0.75f),
        Offset(w * 0.46f, h * 0.82f),
        Offset(w * 0.65f, h * 0.76f),
        Offset(w * 0.38f, h * 0.90f),
        Offset(w * 0.58f, h * 0.91f)
    )
    for (pt in snowPoints) {
        drawScope.drawCircle(
            color = Color.White,
            radius = w * 0.038f,
            center = pt
        )
    }
}

private fun drawFoggy(drawScope: DrawScope) {
    val w = drawScope.size.width
    val h = drawScope.size.height

    val barH = h * 0.09f
    val barColor = Color(0xCC94A3B8)

    // 3 sleek rounded mist bands
    drawScope.drawRoundRect(
        color = barColor,
        topLeft = Offset(w * 0.15f, h * 0.35f),
        size = Size(w * 0.70f, barH),
        cornerRadius = CornerRadius(barH / 2, barH / 2)
    )

    drawScope.drawRoundRect(
        color = barColor,
        topLeft = Offset(w * 0.10f, h * 0.52f),
        size = Size(w * 0.80f, barH),
        cornerRadius = CornerRadius(barH / 2, barH / 2)
    )

    drawScope.drawRoundRect(
        color = barColor,
        topLeft = Offset(w * 0.22f, h * 0.69f),
        size = Size(w * 0.56f, barH),
        cornerRadius = CornerRadius(barH / 2, barH / 2)
    )
}

/**
 * Draws a 3D puffy cumulus cloud using layered circles and base rounded rectangle.
 */
private fun drawPuffyCloud(
    drawScope: DrawScope,
    baseOffset: Offset,
    scale: Float = 1.0f,
    tint: Color = Color.White
) {
    val w = drawScope.size.width * scale
    val h = drawScope.size.height * scale
    val ox = baseOffset.x
    val oy = baseOffset.y

    val shadowColor = if (tint == Color.White) Color(0xFFD6E4F0) else tint.copy(alpha = 0.8f)
    val bodyColor = tint

    // Cloud Circles (Left, Center Big, Right)
    val rLeft = w * 0.22f
    val cLeft = Offset(ox + w * 0.28f, oy + h * 0.38f)

    val rCenter = w * 0.32f
    val cCenter = Offset(ox + w * 0.52f, oy + h * 0.26f)

    val rRight = w * 0.24f
    val cRight = Offset(ox + w * 0.75f, oy + h * 0.38f)

    // Base rounded pill
    val baseRectTop = oy + h * 0.32f
    val baseRectHeight = h * 0.28f
    val baseRectWidth = w * 0.72f
    val baseRectLeft = ox + w * 0.14f

    // 1. Ambient bottom shadow
    drawScope.drawRoundRect(
        color = shadowColor,
        topLeft = Offset(baseRectLeft, baseRectTop + h * 0.03f),
        size = Size(baseRectWidth, baseRectHeight),
        cornerRadius = CornerRadius(baseRectHeight / 2, baseRectHeight / 2)
    )

    // 2. Base rounded pill
    drawScope.drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(bodyColor, shadowColor),
            startY = baseRectTop,
            endY = baseRectTop + baseRectHeight
        ),
        topLeft = Offset(baseRectLeft, baseRectTop),
        size = Size(baseRectWidth, baseRectHeight),
        cornerRadius = CornerRadius(baseRectHeight / 2, baseRectHeight / 2)
    )

    // 3. Left puff
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(bodyColor, shadowColor),
            center = Offset(cLeft.x - rLeft * 0.2f, cLeft.y - rLeft * 0.2f),
            radius = rLeft * 1.1f
        ),
        radius = rLeft,
        center = cLeft
    )

    // 4. Center puff
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(bodyColor, shadowColor),
            center = Offset(cCenter.x - rCenter * 0.25f, cCenter.y - rCenter * 0.25f),
            radius = rCenter * 1.15f
        ),
        radius = rCenter,
        center = cCenter
    )

    // 5. Right puff
    drawScope.drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(bodyColor, shadowColor),
            center = Offset(cRight.x - rRight * 0.2f, cRight.y - rRight * 0.2f),
            radius = rRight * 1.1f
        ),
        radius = rRight,
        center = cRight
    )
}
