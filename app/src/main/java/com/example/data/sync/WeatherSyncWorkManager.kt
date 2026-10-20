package com.example.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Modular helper manager for background weather synchronization via Android WorkManager.
 *
 * Implements user-defined sync intervals with a strict battery-saving rule:
 * Standard background sync will never run more frequently than 30 minutes.
 */
object WeatherSyncWorkManager {

    const val UNIQUE_WORK_NAME = "weather_periodic_sync_work"
    const val WORK_TAG = "weather_sync_worker"

    /**
     * Strict rule: Minimum sync interval allowed for normal background periodic execution is 30 minutes.
     * Prevents excessive radio wakelocks and battery drain.
     */
    const val MIN_SYNC_INTERVAL_MINUTES = 30L

    /**
     * Exact helper function to call inside Settings Activity / Controller / ViewModel
     * whenever the user changes or saves their sync interval choice.
     *
     * It enforces the 30-minute minimum rule, updates or cancels existing periodic work,
     * and enqueues the newly configured PeriodicWorkRequest.
     *
     * @param context Application context
     * @param intervalMinutes User requested sync interval in minutes (e.g. 30, 60, 120)
     * @return UUID of the newly enqueued PeriodicWorkRequest
     */
    @JvmStatic
    fun updateSyncInterval(context: Context, intervalMinutes: Long): UUID {
        // Enforce strict battery rule: minimum 30 minutes
        val safeIntervalMinutes = maxOf(intervalMinutes, MIN_SYNC_INTERVAL_MINUTES)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<WeatherSyncWorker>(
            safeIntervalMinutes, TimeUnit.MINUTES,
            // 5-minute flex window to allow the OS to batch work efficiently
            5, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(WORK_TAG)
            .build()

        // ExistingPeriodicWorkPolicy.UPDATE smoothly replaces the previous periodic worker
        // with the new interval parameters without missing cycles.
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncRequest
        )

        return syncRequest.id
    }

    /**
     * Cancels any active periodic background worker.
     */
    @JvmStatic
    fun cancelPeriodicSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    /**
     * Observes the WorkInfo status of the periodic worker.
     */
    @JvmStatic
    fun getWorkInfosFlow(context: Context): Flow<List<WorkInfo>> {
        return WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(UNIQUE_WORK_NAME)
    }
}
