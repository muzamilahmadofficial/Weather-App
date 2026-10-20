package com.example.data.repository

import com.example.data.model.CityLocation
import com.example.data.model.CurrentWeather
import com.example.data.model.DailyForecast
import com.example.data.model.FullWeatherReport
import com.example.data.model.HourlyForecast
import com.example.data.model.OpenMeteoForecastResponse
import com.example.data.model.WeatherCondition
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeatherRepository {

    private val weatherApi = RetrofitClient.weatherApi
    private val geocodingApi = RetrofitClient.geocodingApi

    suspend fun fetchWeatherReport(city: CityLocation): Result<FullWeatherReport> = withContext(Dispatchers.IO) {
        try {
            val response = weatherApi.getForecast(
                latitude = city.latitude,
                longitude = city.longitude
            )
            val report = mapToFullWeatherReport(city, response)
            Result.success(report)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchCities(query: String): Result<List<CityLocation>> = withContext(Dispatchers.IO) {
        if (query.trim().length < 2) {
            return@withContext Result.success(emptyList())
        }
        try {
            val response = geocodingApi.searchLocations(query.trim())
            val cities = response.results?.map { item ->
                CityLocation(
                    name = item.name,
                    country = item.country ?: item.countryCode ?: "",
                    stateOrRegion = item.admin1,
                    latitude = item.latitude,
                    longitude = item.longitude
                )
            } ?: emptyList()
            Result.success(cities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapToFullWeatherReport(
        city: CityLocation,
        response: OpenMeteoForecastResponse
    ): FullWeatherReport {
        val currentDto = response.current
        val weatherCode = currentDto?.weatherCode ?: 0
        val condition = WeatherCondition.fromWmoCode(weatherCode)

        val current = CurrentWeather(
            temperature = currentDto?.temperature2m ?: 0.0,
            apparentTemperature = currentDto?.apparentTemperature ?: currentDto?.temperature2m ?: 0.0,
            relativeHumidity = currentDto?.relativeHumidity2m ?: 50,
            windSpeed = currentDto?.windSpeed10m ?: 0.0,
            windDirection = currentDto?.windDirection10m ?: 0,
            windGusts = currentDto?.windGusts10m ?: 0.0,
            pressure = currentDto?.pressureMsl ?: 1013.2,
            uvIndex = currentDto?.uvIndex ?: 0.0,
            weatherCode = weatherCode,
            condition = condition,
            isDay = (currentDto?.isDay ?: 1) == 1,
            time = currentDto?.time ?: "",
            precipitation = currentDto?.precipitation ?: 0.0
        )

        // Hourly: take next 24 hours starting from current time
        val hourlyList = mutableListOf<HourlyForecast>()
        val hourlyDto = response.hourly
        if (hourlyDto != null && hourlyDto.time.isNotEmpty()) {
            val times = hourlyDto.time
            val temps = hourlyDto.temperature2m
            val humidities = hourlyDto.relativeHumidity2m
            val rainProbs = hourlyDto.precipitationProbability
            val codes = hourlyDto.weatherCode
            val winds = hourlyDto.windSpeed10m
            val uvs = hourlyDto.uvIndex

            // Find index closest to current hour
            val nowIsoPrefix = SimpleDateFormat("yyyy-MM-dd'T'HH", Locale.US).format(Date())
            var startIndex = times.indexOfFirst { it.startsWith(nowIsoPrefix) }
            if (startIndex == -1) startIndex = 0

            val count = minOf(24, times.size - startIndex)
            for (i in 0 until count) {
                val idx = startIndex + i
                val rawTime = times[idx]
                val hourLabel = formatHourLabel(rawTime, i == 0)
                val code = codes.getOrElse(idx) { 0 }
                val temp = temps.getOrElse(idx) { 0.0 }
                val hum = humidities.getOrElse(idx) { 0 }
                val rain = rainProbs?.getOrNull(idx) ?: 0
                val wind = winds.getOrElse(idx) { 0.0 }
                val uv = uvs?.getOrNull(idx) ?: 0.0

                hourlyList.add(
                    HourlyForecast(
                        time = rawTime,
                        hourLabel = hourLabel,
                        temperature = temp,
                        humidity = hum,
                        precipitationProbability = rain,
                        weatherCode = code,
                        condition = WeatherCondition.fromWmoCode(code),
                        windSpeed = wind,
                        uvIndex = uv
                    )
                )
            }
        }

        // Daily forecasts (up to 7 days)
        val dailyList = mutableListOf<DailyForecast>()
        val dailyDto = response.daily
        if (dailyDto != null && dailyDto.time.isNotEmpty()) {
            val daysCount = minOf(7, dailyDto.time.size)
            for (i in 0 until daysCount) {
                val rawDate = dailyDto.time[i]
                val label = formatDayLabel(rawDate, i)
                val code = dailyDto.weatherCode.getOrElse(i) { 0 }
                val maxT = dailyDto.temperature2mMax.getOrElse(i) { 0.0 }
                val minT = dailyDto.temperature2mMin.getOrElse(i) { 0.0 }
                val rainMax = dailyDto.precipitationProbabilityMax?.getOrNull(i) ?: 0
                val windMax = dailyDto.windSpeed10mMax?.getOrNull(i) ?: 0.0
                val uvMax = dailyDto.uvIndexMax?.getOrNull(i) ?: 0.0
                val sunriseTime = dailyDto.sunrise?.getOrNull(i) ?: ""
                val sunsetTime = dailyDto.sunset?.getOrNull(i) ?: ""

                dailyList.add(
                    DailyForecast(
                        date = rawDate,
                        dayLabel = label,
                        minTemperature = minT,
                        maxTemperature = maxT,
                        weatherCode = code,
                        condition = WeatherCondition.fromWmoCode(code),
                        precipitationProbabilityMax = rainMax,
                        windSpeedMax = windMax,
                        uvIndexMax = uvMax,
                        sunrise = sunriseTime,
                        sunset = sunsetTime
                    )
                )
            }
        }

        return FullWeatherReport(
            city = city,
            current = current,
            hourly = hourlyList,
            daily = dailyList
        )
    }

    private fun formatHourLabel(isoTime: String, isFirst: Boolean): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
            val date = parser.parse(isoTime) ?: return isoTime
            if (isFirst) "Now" else SimpleDateFormat("hh a", Locale.US).format(date).lowercase()
        } catch (e: Exception) {
            isoTime
        }
    }

    private fun formatDayLabel(isoDate: String, index: Int): String {
        return when (index) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> {
                try {
                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val date = parser.parse(isoDate) ?: return isoDate
                    SimpleDateFormat("EEEE", Locale.US).format(date)
                } catch (e: Exception) {
                    isoDate
                }
            }
        }
    }
}
