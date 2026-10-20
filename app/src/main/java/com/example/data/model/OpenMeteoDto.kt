package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenMeteoForecastResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "current") val current: OpenMeteoCurrent?,
    @Json(name = "hourly") val hourly: OpenMeteoHourly?,
    @Json(name = "daily") val daily: OpenMeteoDaily?
)

@JsonClass(generateAdapter = true)
data class OpenMeteoCurrent(
    @Json(name = "time") val time: String,
    @Json(name = "temperature_2m") val temperature2m: Double,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Int = 0,
    @Json(name = "apparent_temperature") val apparentTemperature: Double = 0.0,
    @Json(name = "is_day") val isDay: Int = 1,
    @Json(name = "precipitation") val precipitation: Double = 0.0,
    @Json(name = "weather_code") val weatherCode: Int = 0,
    @Json(name = "cloud_cover") val cloudCover: Int = 0,
    @Json(name = "pressure_msl") val pressureMsl: Double = 1013.2,
    @Json(name = "surface_pressure") val surfacePressure: Double = 1013.2,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double = 0.0,
    @Json(name = "wind_direction_10m") val windDirection10m: Int = 0,
    @Json(name = "wind_gusts_10m") val windGusts10m: Double = 0.0,
    @Json(name = "uv_index") val uvIndex: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class OpenMeteoHourly(
    @Json(name = "time") val time: List<String> = emptyList(),
    @Json(name = "temperature_2m") val temperature2m: List<Double> = emptyList(),
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: List<Int> = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>? = null,
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "wind_speed_10m") val windSpeed10m: List<Double> = emptyList(),
    @Json(name = "uv_index") val uvIndex: List<Double>? = null
)

@JsonClass(generateAdapter = true)
data class OpenMeteoDaily(
    @Json(name = "time") val time: List<String> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double> = emptyList(),
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double> = emptyList(),
    @Json(name = "apparent_temperature_max") val apparentTemperatureMax: List<Double>? = null,
    @Json(name = "apparent_temperature_min") val apparentTemperatureMin: List<Double>? = null,
    @Json(name = "sunrise") val sunrise: List<String>? = null,
    @Json(name = "sunset") val sunset: List<String>? = null,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>? = null,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double>? = null,
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int>? = null,
    @Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double>? = null
)

@JsonClass(generateAdapter = true)
data class OpenMeteoGeocodingResponse(
    @Json(name = "results") val results: List<GeocodingResultItem>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResultItem(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "elevation") val elevation: Double? = null,
    @Json(name = "feature_code") val featureCode: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    @Json(name = "country") val country: String? = null,
    @Json(name = "admin1") val admin1: String? = null,
    @Json(name = "timezone") val timezone: String? = null
)
