package com.example.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.CityLocation
import com.example.data.model.TemperatureUnit
import com.example.data.model.WindSpeedUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "weather_settings")

class WeatherPreferences(private val context: Context) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val cityListType = Types.newParameterizedType(List::class.java, CityLocationDto::class.java)
    private val adapter = moshi.adapter<List<CityLocationDto>>(cityListType)

    val temperatureUnitFlow: Flow<TemperatureUnit> = context.dataStore.data.map { preferences ->
        val name = preferences[TEMP_UNIT_KEY] ?: TemperatureUnit.CELSIUS.name
        try {
            TemperatureUnit.valueOf(name)
        } catch (e: Exception) {
            TemperatureUnit.CELSIUS
        }
    }

    val windSpeedUnitFlow: Flow<WindSpeedUnit> = context.dataStore.data.map { preferences ->
        val name = preferences[WIND_UNIT_KEY] ?: WindSpeedUnit.KMH.name
        try {
            WindSpeedUnit.valueOf(name)
        } catch (e: Exception) {
            WindSpeedUnit.KMH
        }
    }

    val savedCitiesFlow: Flow<List<CityLocation>> = context.dataStore.data.map { preferences ->
        val json = preferences[SAVED_CITIES_KEY] ?: ""
        if (json.isBlank()) {
            emptyList()
        } else {
            try {
                val dtoList = adapter.fromJson(json) ?: emptyList()
                dtoList.map { it.toDomain() }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { preferences ->
            preferences[TEMP_UNIT_KEY] = unit.name
        }
    }

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        context.dataStore.edit { preferences ->
            preferences[WIND_UNIT_KEY] = unit.name
        }
    }

    suspend fun saveCity(city: CityLocation) {
        context.dataStore.edit { preferences ->
            val json = preferences[SAVED_CITIES_KEY] ?: ""
            val current = try {
                if (json.isNotBlank()) adapter.fromJson(json) ?: emptyList() else emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            if (current.none { it.latitude == city.latitude && it.longitude == city.longitude }) {
                val updated = current + city.toDto()
                preferences[SAVED_CITIES_KEY] = adapter.toJson(updated)
            }
        }
    }

    suspend fun removeCity(city: CityLocation) {
        context.dataStore.edit { preferences ->
            val json = preferences[SAVED_CITIES_KEY] ?: ""
            val current = try {
                if (json.isNotBlank()) adapter.fromJson(json) ?: emptyList() else emptyList()
            } catch (e: Exception) {
                emptyList()
            }
            val updated = current.filterNot { it.latitude == city.latitude && it.longitude == city.longitude }
            preferences[SAVED_CITIES_KEY] = adapter.toJson(updated)
        }
    }

    val alertsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ALERTS_ENABLED_KEY] ?: true
    }

    suspend fun setAlertsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ALERTS_ENABLED_KEY] = enabled
        }
    }

    val themeModeFlow: Flow<com.example.ui.theme.AppThemeMode> = context.dataStore.data.map { preferences ->
        val name = preferences[THEME_MODE_KEY] ?: com.example.ui.theme.AppThemeMode.DEEP_ATMOSPHERIC.name
        try {
            com.example.ui.theme.AppThemeMode.valueOf(name)
        } catch (e: Exception) {
            com.example.ui.theme.AppThemeMode.DEEP_ATMOSPHERIC
        }
    }

    suspend fun setAppThemeMode(mode: com.example.ui.theme.AppThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { preferences ->
        val name = preferences[LANGUAGE_KEY] ?: AppLanguage.ENGLISH.name
        try {
            AppLanguage.valueOf(name)
        } catch (e: Exception) {
            AppLanguage.ENGLISH
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.name
        }
    }

    val syncIntervalMinutesFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[SYNC_INTERVAL_KEY] ?: DEFAULT_SYNC_INTERVAL_MINUTES
    }

    suspend fun setSyncIntervalMinutes(intervalMinutes: Long) {
        val safeInterval = maxOf(intervalMinutes, MIN_SYNC_INTERVAL_MINUTES)
        context.dataStore.edit { preferences ->
            preferences[SYNC_INTERVAL_KEY] = safeInterval
        }
    }

    val timeFormatFlow: Flow<TimeFormat> = context.dataStore.data.map { preferences ->
        val name = preferences[TIME_FORMAT_KEY] ?: TimeFormat.TWELVE_HOUR.name
        try {
            TimeFormat.valueOf(name)
        } catch (e: Exception) {
            TimeFormat.TWELVE_HOUR
        }
    }

    suspend fun setTimeFormat(format: TimeFormat) {
        context.dataStore.edit { preferences ->
            preferences[TIME_FORMAT_KEY] = format.name
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        const val MIN_SYNC_INTERVAL_MINUTES = 30L
        const val DEFAULT_SYNC_INTERVAL_MINUTES = 60L

        private val TEMP_UNIT_KEY = stringPreferencesKey("temperature_unit")
        private val WIND_UNIT_KEY = stringPreferencesKey("wind_unit")
        private val SAVED_CITIES_KEY = stringPreferencesKey("saved_cities_json")
        private val ALERTS_ENABLED_KEY = booleanPreferencesKey("alerts_enabled")
        private val THEME_MODE_KEY = stringPreferencesKey("app_theme_mode")
        private val LANGUAGE_KEY = stringPreferencesKey("app_language")
        private val SYNC_INTERVAL_KEY = androidx.datastore.preferences.core.longPreferencesKey("sync_interval_minutes")
        private val TIME_FORMAT_KEY = stringPreferencesKey("time_format")
    }
}

enum class TimeFormat(val code: String, val displayNameEn: String, val displayNameFr: String) {
    TWELVE_HOUR("12h", "12-Hour (AM / PM)", "12 Heures (AM / PM)"),
    TWENTY_FOUR_HOUR("24h", "24-Hour (00:00 - 23:59)", "24 Heures (00:00 - 23:59)")
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    FRENCH("fr", "Français")
}

data class CityLocationDto(
    val name: String,
    val country: String,
    val stateOrRegion: String?,
    val latitude: Double,
    val longitude: Double
) {
    fun toDomain() = CityLocation(name, country, stateOrRegion, latitude, longitude)
}

fun CityLocation.toDto() = CityLocationDto(name, country, stateOrRegion, latitude, longitude)
