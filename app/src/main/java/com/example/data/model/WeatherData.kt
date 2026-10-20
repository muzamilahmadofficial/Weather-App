package com.example.data.model

/**
 * Representation of a weather condition based on WMO Weather interpretation codes.
 */
enum class WeatherCondition(
    val title: String,
    val titleFr: String,
    val iconType: String
) {
    CLEAR_SKY("Clear Sky", "Ciel dégagé", "sunny"),
    MAINLY_CLEAR("Mainly Clear", "Plutôt dégagé", "partly_cloudy"),
    PARTLY_CLOUDY("Partly Cloudy", "Partiellement nuageux", "partly_cloudy"),
    OVERCAST("Overcast", "Couvert", "cloudy"),
    FOG("Foggy", "Brouillard", "fog"),
    DEPOSITING_RIME_FOG("Freezing Fog", "Brouillard givrant", "fog"),
    DRIZZLE_LIGHT("Light Drizzle", "Bruine légère", "rain"),
    DRIZZLE_MODERATE("Moderate Drizzle", "Bruine modérée", "rain"),
    DRIZZLE_DENSE("Dense Drizzle", "Bruine dense", "rain"),
    RAIN_SLIGHT("Slight Rain", "Pluie légère", "rain"),
    RAIN_MODERATE("Moderate Rain", "Pluie modérée", "rain"),
    RAIN_HEAVY("Heavy Rain", "Forte pluie", "heavy_rain"),
    SNOW_SLIGHT("Slight Snow", "Neige légère", "snow"),
    SNOW_MODERATE("Moderate Snow", "Neige modérée", "snow"),
    SNOW_HEAVY("Heavy Snow", "Forte neige", "snow"),
    THUNDERSTORM("Thunderstorm", "Orage", "storm"),
    THUNDERSTORM_HAIL("Thunderstorm with Hail", "Orage avec grêle", "storm"),
    UNKNOWN("Unknown", "Inconnu", "cloudy");

    fun localizedTitle(isFrench: Boolean): String = if (isFrench) titleFr else title

    companion object {
        fun fromWmoCode(code: Int): WeatherCondition {
            return when (code) {
                0 -> CLEAR_SKY
                1 -> MAINLY_CLEAR
                2 -> PARTLY_CLOUDY
                3 -> OVERCAST
                45, 48 -> FOG
                51, 53, 55 -> DRIZZLE_LIGHT
                56, 57 -> DRIZZLE_MODERATE
                61 -> RAIN_SLIGHT
                63 -> RAIN_MODERATE
                65 -> RAIN_HEAVY
                66, 67 -> RAIN_MODERATE
                71 -> SNOW_SLIGHT
                73 -> SNOW_MODERATE
                75 -> SNOW_HEAVY
                77 -> SNOW_SLIGHT
                80, 81 -> RAIN_SLIGHT
                82 -> RAIN_HEAVY
                85, 86 -> SNOW_MODERATE
                95 -> THUNDERSTORM
                96, 99 -> THUNDERSTORM_HAIL
                else -> UNKNOWN
            }
        }
    }
}

data class CurrentWeather(
    val temperature: Double,
    val apparentTemperature: Double, // Feels like
    val relativeHumidity: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val windGusts: Double,
    val pressure: Double,
    val uvIndex: Double,
    val weatherCode: Int,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val time: String,
    val precipitation: Double
)

data class HourlyForecast(
    val time: String,
    val hourLabel: String,
    val temperature: Double,
    val humidity: Int,
    val precipitationProbability: Int,
    val weatherCode: Int,
    val condition: WeatherCondition,
    val windSpeed: Double,
    val uvIndex: Double
)

data class DailyForecast(
    val date: String,
    val dayLabel: String,
    val minTemperature: Double,
    val maxTemperature: Double,
    val weatherCode: Int,
    val condition: WeatherCondition,
    val precipitationProbabilityMax: Int,
    val windSpeedMax: Double,
    val uvIndexMax: Double,
    val sunrise: String,
    val sunset: String
)

data class FullWeatherReport(
    val city: CityLocation,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val fetchedAtMillis: Long = System.currentTimeMillis()
)

data class CityLocation(
    val name: String,
    val country: String,
    val stateOrRegion: String? = null,
    val latitude: Double,
    val longitude: Double,
    val isCurrentGpsLocation: Boolean = false
) {
    val displayTitle: String
        get() = if (!stateOrRegion.isNullOrBlank() && stateOrRegion != name) {
            "$name, $country"
        } else {
            if (country.isNotBlank()) "$name, $country" else name
        }
}

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F");

    fun convert(celsius: Double): Double {
        return if (this == FAHRENHEIT) (celsius * 9.0 / 5.0) + 32.0 else celsius
    }

    fun format(celsius: Double): String {
        return "${Math.round(convert(celsius))}$symbol"
    }
}

enum class WindSpeedUnit(val label: String) {
    KMH("km/h"),
    MPH("mph"),
    MS("m/s");

    fun convert(kmh: Double): Double {
        return when (this) {
            KMH -> kmh
            MPH -> kmh * 0.621371
            MS -> kmh / 3.6
        }
    }

    fun format(kmh: Double): String {
        return "${"%.1f".format(convert(kmh))} $label"
    }
}
