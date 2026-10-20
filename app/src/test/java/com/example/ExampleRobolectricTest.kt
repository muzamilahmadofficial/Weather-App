package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Atmosphere", appName)
  }

  @Test
  fun `verify weather condition WMO code conversion`() {
    val sunny = com.example.data.model.WeatherCondition.fromWmoCode(0)
    assertEquals(com.example.data.model.WeatherCondition.CLEAR_SKY, sunny)

    val rain = com.example.data.model.WeatherCondition.fromWmoCode(65)
    assertEquals(com.example.data.model.WeatherCondition.RAIN_HEAVY, rain)

    val storm = com.example.data.model.WeatherCondition.fromWmoCode(95)
    assertEquals(com.example.data.model.WeatherCondition.THUNDERSTORM, storm)
  }

  @Test
  fun `verify temperature unit conversions`() {
    val celsius = 25.0
    val fahrenheit = com.example.data.model.TemperatureUnit.FAHRENHEIT.convert(celsius)
    assertEquals(77.0, fahrenheit, 0.1)
  }

  @Test
  fun `verify storm alert detection logic`() {
    val city = com.example.data.model.CityLocation("Rome", "Italy", null, 41.9, 12.5)
    val stormCurrent = com.example.data.model.CurrentWeather(
        temperature = 22.0,
        apparentTemperature = 22.0,
        relativeHumidity = 85,
        windSpeed = 25.0,
        windDirection = 180,
        windGusts = 40.0,
        pressure = 1008.0,
        uvIndex = 1.0,
        weatherCode = 95, // Thunderstorm
        condition = com.example.data.model.WeatherCondition.THUNDERSTORM,
        isDay = true,
        time = "2026-10-17T14:00",
        precipitation = 12.0
    )
    val report = com.example.data.model.FullWeatherReport(
        city = city,
        current = stormCurrent,
        hourly = emptyList(),
        daily = emptyList()
    )
    val alerts = com.example.data.model.WeatherAlertDetector.analyzeReport(report, com.example.data.model.TemperatureUnit.CELSIUS)
    org.junit.Assert.assertTrue(alerts.any { it.alertType == com.example.data.model.AlertType.STORM })
  }

  @Test
  fun `verify high temperature warning detection logic`() {
    val city = com.example.data.model.CityLocation("Milan", "Italy", null, 45.4, 9.1)
    val heatCurrent = com.example.data.model.CurrentWeather(
        temperature = 39.5, // Extreme heat > 38°C
        apparentTemperature = 43.0,
        relativeHumidity = 45,
        windSpeed = 10.0,
        windDirection = 90,
        windGusts = 15.0,
        pressure = 1012.0,
        uvIndex = 8.5,
        weatherCode = 0,
        condition = com.example.data.model.WeatherCondition.CLEAR_SKY,
        isDay = true,
        time = "2026-10-17T15:00",
        precipitation = 0.0
    )
    val report = com.example.data.model.FullWeatherReport(
        city = city,
        current = heatCurrent,
        hourly = emptyList(),
        daily = emptyList()
    )
    val alerts = com.example.data.model.WeatherAlertDetector.analyzeReport(report, com.example.data.model.TemperatureUnit.CELSIUS)
    org.junit.Assert.assertTrue(alerts.any { it.alertType == com.example.data.model.AlertType.HEAT })
  }
}
