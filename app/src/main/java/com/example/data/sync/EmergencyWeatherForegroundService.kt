package com.example.data.sync

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.location.LocationTracker
import com.example.data.model.AlertSeverity
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherAlertDetector
import com.example.data.notification.WeatherNotificationManager
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Android Foreground Service for Critical/Emergency Weather Mode.
 *
 * Background:
 * Standard Android WorkManager background tasks cannot execute faster than
 * the 15-minute PeriodicWorkRequest limit (to enforce battery optimization).
 *
 * When a critical weather emergency occurs (thunderstorm warning, tornado, gale winds),
 * this Foreground Service runs with a persistent, high-priority notification to provide
 * instant and rapid updates (e.g., every 2 minutes) until the storm clears.
 */
class EmergencyWeatherForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var syncJob: Job? = null
    private val repository = WeatherRepository()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_EMERGENCY -> {
                stopEmergencySession()
                return START_NOT_STICKY
            }
            ACTION_START_EMERGENCY -> {
                val reason = intent.getStringExtra(EXTRA_ALERT_REASON) ?: "Severe weather warning in effect"
                val cityName = intent.getStringExtra(EXTRA_CITY_NAME) ?: "Current Area"
                startEmergencySession(reason, cityName)
                return START_STICKY
            }
            else -> {
                return START_NOT_STICKY
            }
        }
    }

    private fun startEmergencySession(alertReason: String, cityName: String) {
        _isEmergencyActive.value = true
        _activeEmergencyReason.value = alertReason
        _activeEmergencyCity.value = cityName

        // Post persistent foreground notification
        val notification = buildEmergencyNotification(alertReason, cityName, "Emergency tracking active. Polling every 2 min.")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Cancel previous job if any and start rapid polling loop
        syncJob?.cancel()
        syncJob = serviceScope.launch {
            while (isActive) {
                performEmergencyFetch(cityName)
                // Rapid emergency fetch interval: 2 minutes
                delay(EMERGENCY_POLL_INTERVAL_MILLIS)
            }
        }
    }

    private suspend fun performEmergencyFetch(cityName: String) {
        val city = LocationTracker.DEFAULT_CITY.copy(name = cityName)
        val result = repository.fetchWeatherReport(city)
        _lastEmergencySyncTime.value = System.currentTimeMillis()

        result.onSuccess { report ->
            val alerts = WeatherAlertDetector.analyzeReport(report, TemperatureUnit.CELSIUS)
            val severeAlert = alerts.firstOrNull {
                it.severity == AlertSeverity.WARNING || it.severity == AlertSeverity.EMERGENCY
            }

            if (severeAlert != null) {
                // Update persistent notification with fresh telemetry
                val statusText = "${report.current.condition.title} • Temp: ${report.current.temperature.toInt()}°C • Wind: ${report.current.windSpeed.toInt()} km/h"
                val notification = buildEmergencyNotification(
                    reason = severeAlert.description,
                    city = report.city.name,
                    statusText = statusText
                )
                val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.notify(NOTIFICATION_ID, notification)
            } else {
                // Weather cleared! Auto-stop emergency mode and revert to normal WorkManager
                stopEmergencySession()
            }
        }
    }

    private fun stopEmergencySession() {
        syncJob?.cancel()
        _isEmergencyActive.value = false
        _activeEmergencyReason.value = null
        _activeEmergencyCity.value = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    override fun onDestroy() {
        syncJob?.cancel()
        serviceScope.cancel()
        _isEmergencyActive.value = false
        super.onDestroy()
    }

    private fun buildEmergencyNotification(
        reason: String,
        city: String,
        statusText: String
    ): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            1101,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, EmergencyWeatherForegroundService::class.java).apply {
            action = ACTION_STOP_EMERGENCY
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1102,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID_EMERGENCY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🚨 Severe Weather Alert: $city")
            .setContentText(reason)
            .setSubText("Emergency Mode Active")
            .setShowWhen(false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$reason\n\n$statusText\nFrequent background polling is active until storm conditions subside.")
            )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(openPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Dismiss / Revert",
                stopPendingIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val name = "Critical Weather Emergency"
            val desc = "Persistent high-priority alerts during active thunderstorms, tornadoes, and gale events"
            val channel = NotificationChannel(
                CHANNEL_ID_EMERGENCY,
                name,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = desc
                enableVibration(true)
                setShowBadge(true)
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID_EMERGENCY = "channel_emergency_mode"
        const val NOTIFICATION_ID = 9999
        private const val EMERGENCY_POLL_INTERVAL_MILLIS = 120_000L // 2 minutes

        const val ACTION_START_EMERGENCY = "com.example.action.START_EMERGENCY"
        const val ACTION_STOP_EMERGENCY = "com.example.action.STOP_EMERGENCY"
        const val EXTRA_ALERT_REASON = "extra_alert_reason"
        const val EXTRA_CITY_NAME = "extra_city_name"

        // Observable StateFlows for UI / ViewModel
        private val _isEmergencyActive = MutableStateFlow(false)
        val isEmergencyActive = _isEmergencyActive.asStateFlow()

        private val _activeEmergencyReason = MutableStateFlow<String?>(null)
        val activeEmergencyReason = _activeEmergencyReason.asStateFlow()

        private val _activeEmergencyCity = MutableStateFlow<String?>(null)
        val activeEmergencyCity = _activeEmergencyCity.asStateFlow()

        private val _lastEmergencySyncTime = MutableStateFlow(0L)
        val lastEmergencySyncTime = _lastEmergencySyncTime.asStateFlow()

        /**
         * Clean helper function to trigger the Emergency Foreground Service.
         * Drop this into push notification listeners (e.g. FirebaseMessagingService)
         * or call it when periodic background fetch detects severe storm conditions.
         */
        fun startEmergencyMode(
            context: Context,
            alertReason: String = "Severe Thunderstorm Warning Detected",
            cityName: String = "Current Area"
        ) {
            val intent = Intent(context, EmergencyWeatherForegroundService::class.java).apply {
                action = ACTION_START_EMERGENCY
                putExtra(EXTRA_ALERT_REASON, alertReason)
                putExtra(EXTRA_CITY_NAME, cityName)
            }
            ContextCompat.startForegroundService(context, intent)
        }

        /**
         * Clean helper function to stop Emergency Mode and revert to normal schedule.
         */
        fun stopEmergencyMode(context: Context) {
            val intent = Intent(context, EmergencyWeatherForegroundService::class.java).apply {
                action = ACTION_STOP_EMERGENCY
            }
            context.startService(intent)
        }
    }
}
