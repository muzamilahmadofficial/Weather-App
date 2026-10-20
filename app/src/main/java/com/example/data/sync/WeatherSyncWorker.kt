package com.example.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.location.LocationTracker
import com.example.data.model.AlertSeverity
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherAlertDetector
import com.example.data.notification.WeatherNotificationManager
import com.example.data.preferences.WeatherPreferences
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.flow.first

/**
 * Modular Periodic WorkManager Worker for background weather updates.
 *
 * Runs on the user-selected interval (enforcing a minimum of 30 minutes to preserve battery).
 * If severe thunderstorm or tornado warnings are detected during normal background sync,
 * it automatically promotes the app to Emergency Mode by starting the EmergencyWeatherForegroundService.
 */
class WeatherSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val repository = WeatherRepository()
    private val notificationManager = WeatherNotificationManager(applicationContext)
    private val preferences = WeatherPreferences(applicationContext)

    override suspend fun doWork(): Result {
        return try {
            // Retrieve current saved preferences
            val tempUnit = preferences.temperatureUnitFlow.first()
            val isAlertsEnabled = preferences.alertsEnabledFlow.first()
            val language = preferences.languageFlow.first()
            val isFrench = language == com.example.data.preferences.AppLanguage.FRENCH
            val timeFormat = preferences.timeFormatFlow.first()

            // Fetch latest telemetry for current city
            val targetCity = LocationTracker.DEFAULT_CITY
            val weatherResult = repository.fetchWeatherReport(targetCity)

            if (weatherResult.isSuccess) {
                val report = weatherResult.getOrThrow()

                // 1. Update ongoing status bar notification (no duplicate time)
                notificationManager.updateLiveWeatherNotification(
                    report = report,
                    tempUnit = tempUnit,
                    isFrench = isFrench,
                    timeFormat = timeFormat
                )

                // 2. Check for severe emergency alerts
                if (isAlertsEnabled) {
                    val alerts = WeatherAlertDetector.analyzeReport(report, tempUnit)
                    val emergencyAlert = alerts.firstOrNull {
                        it.severity == AlertSeverity.WARNING || it.severity == AlertSeverity.EMERGENCY
                    }

                    if (emergencyAlert != null) {
                        // Immediately promote to Emergency Foreground Service for rapid updates
                        EmergencyWeatherForegroundService.startEmergencyMode(
                            context = applicationContext,
                            alertReason = emergencyAlert.description,
                            cityName = report.city.name
                        )
                    }
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
