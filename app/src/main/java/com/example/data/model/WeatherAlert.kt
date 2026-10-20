package com.example.data.model

enum class AlertSeverity(val displayName: String, val levelOrder: Int) {
    ADVISORY("Advisory", 1),
    WATCH("Watch", 2),
    WARNING("Severe Warning", 3),
    EMERGENCY("Emergency Alert", 4)
}

enum class AlertType {
    STORM,
    HEAT,
    WIND,
    RAIN,
    FREEZE,
    UV
}

data class WeatherAlert(
    val id: String,
    val title: String,
    val severity: AlertSeverity,
    val alertType: AlertType,
    val description: String,
    val precautions: List<String>,
    val cityName: String,
    val timestamp: Long = System.currentTimeMillis()
)

object WeatherAlertDetector {

    fun analyzeReport(report: FullWeatherReport, tempUnit: TemperatureUnit): List<WeatherAlert> {
        val alerts = mutableListOf<WeatherAlert>()
        val current = report.current
        val today = report.daily.firstOrNull()
        val nextHours = report.hourly.take(12)
        val city = report.city.name

        // 1. Storms / Thunderstorms
        val stormInHours = nextHours.firstOrNull { it.weatherCode in listOf(95, 96, 99) }
        if (current.weatherCode in listOf(95, 96, 99) || stormInHours != null) {
            val isCurrent = current.weatherCode in listOf(95, 96, 99)
            alerts.add(
                WeatherAlert(
                    id = "storm_${report.city.latitude}_${report.city.longitude}",
                    title = if (isCurrent) "⛈️ Severe Thunderstorm Warning" else "⛈️ Thunderstorm Approaching",
                    severity = AlertSeverity.WARNING,
                    alertType = AlertType.STORM,
                    description = if (isCurrent) {
                        "Severe thunderstorms with active lightning and heavy downpours detected in $city."
                    } else {
                        "Thunderstorms expected in $city around ${stormInHours?.hourLabel ?: "upcoming hours"}."
                    },
                    precautions = listOf(
                        "Move indoors immediately and stay away from windows.",
                        "Unplug sensitive electrical equipment.",
                        "Avoid open water, high ground, and isolated tall trees."
                    ),
                    cityName = city
                )
            )
        }

        // 2. High Temperature / Extreme Heat Warning
        val maxTempToday = today?.maxTemperature ?: current.temperature
        if (current.temperature >= 35.0 || maxTempToday >= 36.0) {
            val isSevere = current.temperature >= 38.0 || maxTempToday >= 39.0
            alerts.add(
                WeatherAlert(
                    id = "heat_${report.city.latitude}_${report.city.longitude}",
                    title = if (isSevere) "🔥 Extreme Heat Warning" else "☀️ Heat Advisory",
                    severity = if (isSevere) AlertSeverity.WARNING else AlertSeverity.ADVISORY,
                    alertType = AlertType.HEAT,
                    description = "Dangerous high temperatures reaching ${tempUnit.format(maxTempToday)} in $city (Feels like ${tempUnit.format(current.apparentTemperature)}).",
                    precautions = listOf(
                        "Drink plenty of water and stay in air-conditioned areas.",
                        "Never leave children or pets in unattended vehicles.",
                        "Limit strenuous outdoor activities during peak afternoon hours."
                    ),
                    cityName = city
                )
            )
        }

        // 3. Gale-Force Wind Warning
        if (current.windSpeed >= 45.0 || current.windGusts >= 60.0) {
            alerts.add(
                WeatherAlert(
                    id = "wind_${report.city.latitude}_${report.city.longitude}",
                    title = "💨 High Wind Warning",
                    severity = AlertSeverity.WARNING,
                    alertType = AlertType.WIND,
                    description = "Strong winds of ${"%.1f".format(current.windSpeed)} km/h with gusts exceeding ${"%.1f".format(current.windGusts)} km/h recorded in $city.",
                    precautions = listOf(
                        "Secure loose outdoor objects and furniture.",
                        "Watch for falling tree branches and flying debris.",
                        "Exercise caution while driving high-profile vehicles."
                    ),
                    cityName = city
                )
            )
        }

        // 4. Heavy Rain & Flash Flood Risk
        val heavyRainInHours = nextHours.firstOrNull { it.weatherCode in listOf(65, 82) }
        if (current.weatherCode in listOf(65, 82) || heavyRainInHours != null) {
            alerts.add(
                WeatherAlert(
                    id = "rain_${report.city.latitude}_${report.city.longitude}",
                    title = "🌧️ Heavy Rain & Flood Advisory",
                    severity = AlertSeverity.WATCH,
                    alertType = AlertType.RAIN,
                    description = "Torrential rainfall expected in $city with precipitation rates exceeding safety margins.",
                    precautions = listOf(
                        "Avoid driving through flooded roadways or low-lying underpasses.",
                        "Clear local drainage paths and gutters.",
                        "Stay tuned to local safety emergency updates."
                    ),
                    cityName = city
                )
            )
        }

        // 5. Extreme UV Radiation Warning
        if (current.uvIndex >= 8.0) {
            alerts.add(
                WeatherAlert(
                    id = "uv_${report.city.latitude}_${report.city.longitude}",
                    title = "☀️ Extreme UV Radiation Alert",
                    severity = AlertSeverity.ADVISORY,
                    alertType = AlertType.UV,
                    description = "Very high UV index (%.1f) detected in $city. Unprotected skin can burn within minutes.".format(current.uvIndex),
                    precautions = listOf(
                        "Apply broad-spectrum SPF 30+ sunscreen generously.",
                        "Wear UV-blocking sunglasses and wide-brim hats.",
                        "Seek shade between 10:00 AM and 4:00 PM."
                    ),
                    cityName = city
                )
            )
        }

        // 6. Freezing Temperature Warning
        if (current.temperature <= 0.0) {
            alerts.add(
                WeatherAlert(
                    id = "freeze_${report.city.latitude}_${report.city.longitude}",
                    title = "❄️ Freeze Warning",
                    severity = AlertSeverity.ADVISORY,
                    alertType = AlertType.FREEZE,
                    description = "Sub-freezing temperatures (${tempUnit.format(current.temperature)}) present in $city with black ice risk.",
                    precautions = listOf(
                        "Wrap exposed water pipes to prevent freezing.",
                        "Drive with extreme caution on bridges and overpasses.",
                        "Keep outdoor pets sheltered indoors."
                    ),
                    cityName = city
                )
            )
        }

        return alerts
    }
}
