package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.LocationTracker
import com.example.data.model.AlertSeverity
import com.example.data.model.AlertType
import com.example.data.model.CityLocation
import com.example.data.model.CurrentWeather
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherAlert
import com.example.data.model.WeatherAlertDetector
import com.example.data.model.WindSpeedUnit
import com.example.data.notification.WeatherNotificationManager
import com.example.data.preferences.TimeFormat
import com.example.data.preferences.WeatherPreferences
import com.example.data.repository.WeatherRepository
import com.example.data.sync.EmergencyWeatherForegroundService
import com.example.data.sync.WeatherSyncWorkManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val report: FullWeatherReport) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository()
    private val locationTracker = LocationTracker(application.applicationContext)
    private val preferences = WeatherPreferences(application.applicationContext)
    private val notificationManager = WeatherNotificationManager(application.applicationContext)

    private val notifiedAlertIds = mutableSetOf<String>()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _currentCity = MutableStateFlow(LocationTracker.DEFAULT_CITY)
    val currentCity: StateFlow<CityLocation> = _currentCity.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Real-time weather alerts state
    private val _activeAlerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val activeAlerts: StateFlow<List<WeatherAlert>> = _activeAlerts.asStateFlow()

    val alertsEnabled: StateFlow<Boolean> = preferences.alertsEnabledFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<CityLocation>>(emptyList())
    val searchResults: StateFlow<List<CityLocation>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Explore / Around country cities weather preview map
    private val _exploreWeather = MutableStateFlow<Map<String, FullWeatherReport>>(emptyMap())
    val exploreWeather: StateFlow<Map<String, FullWeatherReport>> = _exploreWeather.asStateFlow()

    val tempUnit: StateFlow<TemperatureUnit> = preferences.temperatureUnitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TemperatureUnit.CELSIUS)

    val windUnit: StateFlow<WindSpeedUnit> = preferences.windSpeedUnitFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WindSpeedUnit.KMH)

    val savedCities: StateFlow<List<CityLocation>> = preferences.savedCitiesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val themeMode: StateFlow<com.example.ui.theme.AppThemeMode> = preferences.themeModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.ui.theme.AppThemeMode.DEEP_ATMOSPHERIC)

    val appLanguage: StateFlow<com.example.data.preferences.AppLanguage> = preferences.languageFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.preferences.AppLanguage.ENGLISH)

    val syncIntervalMinutes: StateFlow<Long> = preferences.syncIntervalMinutesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeatherPreferences.DEFAULT_SYNC_INTERVAL_MINUTES)

    val timeFormat: StateFlow<TimeFormat> = preferences.timeFormatFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimeFormat.TWELVE_HOUR)

    val isEmergencyModeActive: StateFlow<Boolean> = EmergencyWeatherForegroundService.isEmergencyActive
    val activeEmergencyReason: StateFlow<String?> = EmergencyWeatherForegroundService.activeEmergencyReason

    private var searchJob: Job? = null

    // Real-time live clock inside app, synchronized every minute
    private val _currentAppTimeMillis = MutableStateFlow(System.currentTimeMillis())
    val currentAppTimeMillis: StateFlow<Long> = _currentAppTimeMillis.asStateFlow()

    // Last successful weather sync timestamp
    private val _lastSyncedMillis = MutableStateFlow(System.currentTimeMillis())
    val lastSyncedMillis: StateFlow<Long> = _lastSyncedMillis.asStateFlow()

    // Auto-sync status description message
    private val _syncStatusMessage = MutableStateFlow("Synced just now")
    val syncStatusMessage: StateFlow<String> = _syncStatusMessage.asStateFlow()

    private var homeLocationCity: CityLocation? = null

    init {
        // Load initial weather and check GPS automatically
        loadWeatherForCurrentLocation()
        loadExploreCitiesWeather()

        // Initialize background periodic sync with user-configured interval
        viewModelScope.launch {
            val savedInterval = preferences.syncIntervalMinutesFlow.first()
            WeatherSyncWorkManager.updateSyncInterval(application.applicationContext, savedInterval)
        }

        // Background ticker for live time synchronization and periodic automatic weather sync
        viewModelScope.launch {
            while (isActive) {
                delay(15_000L) // every 15 seconds
                val now = System.currentTimeMillis()
                _currentAppTimeMillis.value = now

                updateSyncStatusMessage(_lastSyncedMillis.value)

                val elapsedMinutes = (now - _lastSyncedMillis.value) / (60 * 1000L)
                // Automatically synchronize weather data if more than 15 minutes since last sync
                if (elapsedMinutes >= 15 && !_isRefreshing.value) {
                    refreshWeather(isSilent = true)
                }
            }
        }
    }

    private fun updateSyncStatusMessage(lastSyncTime: Long) {
        val now = System.currentTimeMillis()
        val elapsedMinutes = (now - lastSyncTime) / (60 * 1000L)
        val isFrench = appLanguage.value == com.example.data.preferences.AppLanguage.FRENCH
        if (!_isRefreshing.value) {
            _syncStatusMessage.value = if (isFrench) {
                when {
                    elapsedMinutes < 1 -> "Synchronisé à l'instant"
                    elapsedMinutes == 1L -> "Synchronisé il y a 1 min"
                    else -> "Synchronisé il y a ${elapsedMinutes}m"
                }
            } else {
                when {
                    elapsedMinutes < 1 -> "Synced just now"
                    elapsedMinutes == 1L -> "Synced 1 min ago"
                    else -> "Synced ${elapsedMinutes}m ago"
                }
            }
        }
    }

    fun hasGpsPermission(): Boolean = locationTracker.hasLocationPermission()

    fun canPostNotifications(): Boolean = notificationManager.canPostNotifications()

    /**
     * Automatically attempts to detect user location via GPS and loads the forecast.
     */
    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _syncStatusMessage.value = "Detecting GPS location..."
            if (locationTracker.hasLocationPermission()) {
                val gpsCity = locationTracker.getCurrentLocation()
                if (gpsCity != null) {
                    val resolvedGps = gpsCity.copy(isCurrentGpsLocation = true)
                    homeLocationCity = resolvedGps
                    _currentCity.value = resolvedGps
                    loadWeatherForCity(resolvedGps)
                    _isRefreshing.value = false
                    return@launch
                }
            }
            // If GPS is not yet granted or returned null, use previously saved home city or DEFAULT_CITY
            val target = homeLocationCity ?: LocationTracker.DEFAULT_CITY
            _currentCity.value = target
            loadWeatherForCity(target)
            _isRefreshing.value = false
        }
    }

    /**
     * Returns user directly to their Home/GPS location without getting stuck.
     */
    fun returnToHomeLocation() {
        viewModelScope.launch {
            val target = homeLocationCity ?: LocationTracker.DEFAULT_CITY
            _currentCity.value = target
            loadWeatherForCity(target)
            if (locationTracker.hasLocationPermission()) {
                val gpsCity = locationTracker.getCurrentLocation()
                if (gpsCity != null) {
                    val resolvedGps = gpsCity.copy(isCurrentGpsLocation = true)
                    homeLocationCity = resolvedGps
                    _currentCity.value = resolvedGps
                    loadWeatherForCity(resolvedGps)
                }
            }
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (granted) {
            loadWeatherForCurrentLocation()
        }
    }

    fun loadWeatherForCity(city: CityLocation) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            _currentCity.value = city
            val isFrench = appLanguage.value == com.example.data.preferences.AppLanguage.FRENCH
            _syncStatusMessage.value = if (isFrench) "Chargement météo pour ${city.name}..." else "Fetching weather for ${city.name}..."
            val result = repository.fetchWeatherReport(city)
            result.fold(
                onSuccess = { report ->
                    _uiState.value = WeatherUiState.Success(report)
                    _lastSyncedMillis.value = System.currentTimeMillis()
                    updateSyncStatusMessage(_lastSyncedMillis.value)
                    evaluateAlerts(report)
                    notificationManager.updateLiveWeatherNotification(report, tempUnit.value, isFrench, timeFormat.value)
                },
                onFailure = { error ->
                    _uiState.value = WeatherUiState.Error(
                        error.localizedMessage ?: if (isFrench) "Impossible de charger la météo en direct." else "Unable to fetch live weather data. Please check connection."
                    )
                    _syncStatusMessage.value = if (isFrench) "Échec de synchronisation • Réessayer" else "Sync failed • Tap to retry"
                }
            )
        }
    }

    fun refreshWeather(isSilent: Boolean = false) {
        viewModelScope.launch {
            _isRefreshing.value = true
            val isFrench = appLanguage.value == com.example.data.preferences.AppLanguage.FRENCH
            _syncStatusMessage.value = if (isFrench) "Synchronisation météo..." else "Syncing weather data..."
            val city = _currentCity.value
            val result = repository.fetchWeatherReport(city)
            result.fold(
                onSuccess = { report ->
                    _uiState.value = WeatherUiState.Success(report)
                    _lastSyncedMillis.value = System.currentTimeMillis()
                    updateSyncStatusMessage(_lastSyncedMillis.value)
                    evaluateAlerts(report)
                    notificationManager.updateLiveWeatherNotification(report, tempUnit.value, isFrench, timeFormat.value)
                },
                onFailure = { error ->
                    if (!isSilent) {
                        _uiState.value = WeatherUiState.Error(
                            error.localizedMessage ?: if (isFrench) "Échec de la mise à jour." else "Failed to refresh weather."
                        )
                    }
                    _syncStatusMessage.value = if (isFrench) "Échec de synchronisation • Réessayer" else "Sync failed • Tap to retry"
                }
            )
            _isRefreshing.value = false
        }
    }

    private fun evaluateAlerts(report: FullWeatherReport) {
        val detected = WeatherAlertDetector.analyzeReport(report, tempUnit.value)
        _activeAlerts.value = detected

        if (alertsEnabled.value) {
            detected.forEach { alert ->
                if (!notifiedAlertIds.contains(alert.id)) {
                    notificationManager.postWeatherAlert(alert)
                    notifiedAlertIds.add(alert.id)
                }
            }
        }
    }

    fun setAlertsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAlertsEnabled(enabled)
        }
    }

    /**
     * Triggers a push notification test for the user's current city.
     */
    fun triggerTestNotification(type: AlertType = AlertType.STORM) {
        val city = _currentCity.value.name
        val testAlert = when (type) {
            AlertType.STORM -> WeatherAlert(
                id = "test_storm_${System.currentTimeMillis()}",
                title = "⛈️ Severe Weather Alert: Thunderstorm Warning",
                severity = AlertSeverity.WARNING,
                alertType = AlertType.STORM,
                description = "Severe thunderstorm with active cloud-to-ground lightning and torrential rain reported in $city.",
                precautions = listOf(
                    "Seek shelter indoors immediately.",
                    "Stay clear of electrical appliances and plumbing fixtures.",
                    "Never drive through standing or moving flood water."
                ),
                cityName = city
            )
            AlertType.HEAT -> WeatherAlert(
                id = "test_heat_${System.currentTimeMillis()}",
                title = "🔥 Extreme Heat Warning: High Temperature Alert",
                severity = AlertSeverity.WARNING,
                alertType = AlertType.HEAT,
                description = "Dangerous heat index exceeding 38°C (100°F) in $city. Heat exhaustion likely with prolonged exposure.",
                precautions = listOf(
                    "Drink water frequently before feeling thirsty.",
                    "Stay inside shaded or air-conditioned environments.",
                    "Check on vulnerable neighbors and pets."
                ),
                cityName = city
            )
            else -> WeatherAlert(
                id = "test_wind_${System.currentTimeMillis()}",
                title = "💨 High Wind Warning: Gale Force Gusts",
                severity = AlertSeverity.WARNING,
                alertType = AlertType.WIND,
                description = "Damaging wind gusts of 75 km/h detected in $city. Expect travel disruptions.",
                precautions = listOf(
                    "Secure patio furniture and trash bins.",
                    "Watch for fallen power lines and tree branches."
                ),
                cityName = city
            )
        }

        // Add to active alerts for in-app display and dispatch notification
        val currentList = _activeAlerts.value
        _activeAlerts.value = listOf(testAlert) + currentList.filterNot { it.id == testAlert.id }
        notificationManager.postWeatherAlert(testAlert)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(350)
            _isSearching.value = true
            val result = repository.searchCities(query)
            result.fold(
                onSuccess = { cities ->
                    _searchResults.value = cities
                    _isSearching.value = false
                },
                onFailure = {
                    _searchResults.value = emptyList()
                    _isSearching.value = false
                }
            )
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    fun selectCityFromSearch(city: CityLocation) {
        loadWeatherForCity(city)
        clearSearch()
    }

    fun toggleSaveCity(city: CityLocation) {
        viewModelScope.launch {
            val isSaved = savedCities.value.any { it.latitude == city.latitude && it.longitude == city.longitude }
            if (isSaved) {
                preferences.removeCity(city)
            } else {
                preferences.saveCity(city)
            }
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            preferences.setTemperatureUnit(unit)
        }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            preferences.setWindSpeedUnit(unit)
        }
    }

    fun setThemeMode(mode: com.example.ui.theme.AppThemeMode) {
        viewModelScope.launch {
            preferences.setAppThemeMode(mode)
        }
    }

    fun setLanguage(language: com.example.data.preferences.AppLanguage) {
        viewModelScope.launch {
            preferences.setLanguage(language)
            val isFrench = (language == com.example.data.preferences.AppLanguage.FRENCH)
            val currentReport = (_uiState.value as? WeatherUiState.Success)?.report
            if (currentReport != null) {
                notificationManager.updateLiveWeatherNotification(currentReport, tempUnit.value, isFrench, timeFormat.value)
            }
            updateSyncStatusMessage(_lastSyncedMillis.value)
        }
    }

    fun setSyncInterval(intervalMinutes: Long) {
        viewModelScope.launch {
            preferences.setSyncIntervalMinutes(intervalMinutes)
            WeatherSyncWorkManager.updateSyncInterval(getApplication(), intervalMinutes)
        }
    }

    fun setTimeFormat(format: TimeFormat) {
        viewModelScope.launch {
            preferences.setTimeFormat(format)
            val isFrench = (appLanguage.value == com.example.data.preferences.AppLanguage.FRENCH)
            val currentReport = (_uiState.value as? WeatherUiState.Success)?.report
            if (currentReport != null) {
                notificationManager.updateLiveWeatherNotification(currentReport, tempUnit.value, isFrench, format)
            }
        }
    }

    fun triggerEmergencyMode(reason: String = "Severe Thunderstorm Warning") {
        val cityName = (_uiState.value as? WeatherUiState.Success)?.report?.city?.name ?: _currentCity.value.name
        EmergencyWeatherForegroundService.startEmergencyMode(
            context = getApplication(),
            alertReason = reason,
            cityName = cityName
        )
    }

    fun stopEmergencyMode() {
        EmergencyWeatherForegroundService.stopEmergencyMode(getApplication())
        viewModelScope.launch {
            val interval = preferences.syncIntervalMinutesFlow.first()
            WeatherSyncWorkManager.updateSyncInterval(getApplication(), interval)
        }
    }

    fun resetAppData() {
        viewModelScope.launch {
            preferences.clearAllData()
            WeatherSyncWorkManager.updateSyncInterval(
                getApplication(),
                com.example.data.preferences.WeatherPreferences.DEFAULT_SYNC_INTERVAL_MINUTES
            )
            loadWeatherForCurrentLocation()
            loadExploreCitiesWeather()
        }
    }

    private fun loadExploreCitiesWeather() {
        viewModelScope.launch {
            val resultMap = mutableMapOf<String, FullWeatherReport>()
            LocationTracker.CURATED_EXPLORE_CITIES.forEach { city ->
                val res = repository.fetchWeatherReport(city)
                res.getOrNull()?.let { report ->
                    resultMap[city.name] = report
                }
            }
            _exploreWeather.value = resultMap
        }
    }
}
