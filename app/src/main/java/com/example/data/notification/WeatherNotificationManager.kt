package com.example.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.AlertSeverity
import com.example.data.model.WeatherAlert

class WeatherNotificationManager(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val alertName = "Severe Weather Alerts"
            val alertDesc = "Real-time push notifications for severe storms, extreme heat, and dangerous weather conditions"
            val alertChannel = NotificationChannel(CHANNEL_ID, alertName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = alertDesc
                enableVibration(true)
                setShowBadge(true)
            }
            manager.createNotificationChannel(alertChannel)

            val liveName = "Live Weather Status"
            val liveDesc = "Displays ongoing real-time weather and temperature in the notification bar"
            val liveChannel = NotificationChannel(CHANNEL_ID_LIVE, liveName, NotificationManager.IMPORTANCE_LOW).apply {
                description = liveDesc
                setShowBadge(false)
            }
            manager.createNotificationChannel(liveChannel)
        }
    }

    fun canPostNotifications(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            notificationManager.areNotificationsEnabled()
        }
    }

    fun updateLiveWeatherNotification(
        report: com.example.data.model.FullWeatherReport,
        tempUnit: com.example.data.model.TemperatureUnit,
        isFrench: Boolean = false,
        timeFormat: com.example.data.preferences.TimeFormat = com.example.data.preferences.TimeFormat.TWELVE_HOUR
    ) {
        if (!canPostNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val today = report.daily.firstOrNull()
        val high = today?.maxTemperature ?: report.current.temperature
        val low = today?.minTemperature ?: report.current.temperature
        val conditionTitle = report.current.condition.localizedTitle(isFrench)

        val title = "${report.city.name}  ${tempUnit.format(report.current.temperature)}  •  $conditionTitle"
        val text = if (isFrench) {
            "Ressenti ${tempUnit.format(report.current.apparentTemperature)}  •  Max: ${tempUnit.format(high)} / Min: ${tempUnit.format(low)}"
        } else {
            "Feels like ${tempUnit.format(report.current.apparentTemperature)}  •  H: ${tempUnit.format(high)} / L: ${tempUnit.format(low)}"
        }
        val subText = if (isFrench) "Météo en direct" else "Live Weather"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_LIVE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setSubText(subText)
            .setShowWhen(false) // Prevents duplicate system time in the notification header
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_LIVE, notification)
        } catch (e: SecurityException) {
            // Permission revoked
        }
    }

    fun postWeatherAlert(alert: WeatherAlert) {
        if (!canPostNotifications()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val fullText = buildString {
            append(alert.description)
            if (alert.precautions.isNotEmpty()) {
                append("\n\nSafety recommendations:\n")
                alert.precautions.forEach { append("• $it\n") }
            }
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(alert.title)
            .setContentText(alert.description)
            .setShowWhen(false)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(fullText)
                    .setBigContentTitle(alert.title)
                    .setSummaryText(alert.cityName)
            )
            .setPriority(
                if (alert.severity == AlertSeverity.WARNING || alert.severity == AlertSeverity.EMERGENCY) {
                    NotificationCompat.PRIORITY_MAX
                } else {
                    NotificationCompat.PRIORITY_HIGH
                }
            )
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        try {
            notificationManager.notify(alert.id.hashCode(), notification)
        } catch (e: SecurityException) {
            // Permission revoked
        }
    }

    companion object {
        const val CHANNEL_ID = "channel_severe_weather_alerts"
        const val CHANNEL_ID_LIVE = "channel_live_weather_status"
        const val NOTIFICATION_ID_LIVE = 1001
    }
}
