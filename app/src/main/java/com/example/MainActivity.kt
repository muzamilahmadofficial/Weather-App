package com.example

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CurvedBottomNav
import com.example.ui.components.WeatherNavDestination
import com.example.ui.screens.AtmosphereDetailsScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
            MyApplicationTheme(themeMode = themeMode) {
                WeatherApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WeatherApp(viewModel: WeatherViewModel) {
    var selectedTab by remember { mutableStateOf(WeatherNavDestination.HOME) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentCity by viewModel.currentCity.collectAsStateWithLifecycle()
    val tempUnit by viewModel.tempUnit.collectAsStateWithLifecycle()
    val windUnit by viewModel.windUnit.collectAsStateWithLifecycle()
    val savedCities by viewModel.savedCities.collectAsStateWithLifecycle()
    val activeAlerts by viewModel.activeAlerts.collectAsStateWithLifecycle()
    val alertsEnabled by viewModel.alertsEnabled.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val exploreWeatherMap by viewModel.exploreWeather.collectAsStateWithLifecycle()

    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val currentAppTimeMillis by viewModel.currentAppTimeMillis.collectAsStateWithLifecycle()
    val lastSyncedMillis by viewModel.lastSyncedMillis.collectAsStateWithLifecycle()
    val syncStatusMessage by viewModel.syncStatusMessage.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isFrench = (appLanguage == com.example.data.preferences.AppLanguage.FRENCH)
    val syncIntervalMinutes by viewModel.syncIntervalMinutes.collectAsStateWithLifecycle()
    val timeFormat by viewModel.timeFormat.collectAsStateWithLifecycle()
    val isEmergencyModeActive by viewModel.isEmergencyModeActive.collectAsStateWithLifecycle()
    val activeEmergencyReason by viewModel.activeEmergencyReason.collectAsStateWithLifecycle()

    // Location and Notification Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onLocationPermissionResult(isGranted)
    }

    // Automatically prompt location & notification permission on initial app launch
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    // Back handling: if on secondary tab, back button returns to HOME
    if (selectedTab != WeatherNavDestination.HOME) {
        BackHandler {
            selectedTab = WeatherNavDestination.HOME
        }
    }

    val isCurrentCitySaved = savedCities.any {
        it.latitude == currentCity.latitude && it.longitude == currentCity.longitude
    }

    val fullReport = (uiState as? WeatherUiState.Success)?.report

    val handleRequestGps: () -> Unit = {
        if (viewModel.hasGpsPermission()) {
            viewModel.loadWeatherForCurrentLocation()
        } else {
            val perms = mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                perms.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionLauncher.launch(perms.toTypedArray())
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            CurvedBottomNav(
                selectedDestination = selectedTab,
                onDestinationSelected = { destination ->
                    if (destination == WeatherNavDestination.HOME) {
                        if (selectedTab != WeatherNavDestination.HOME) {
                            selectedTab = WeatherNavDestination.HOME
                            if (!currentCity.isCurrentGpsLocation) {
                                viewModel.returnToHomeLocation()
                            }
                        } else {
                            if (!currentCity.isCurrentGpsLocation) {
                                viewModel.returnToHomeLocation()
                            } else {
                                viewModel.refreshWeather()
                            }
                        }
                    } else {
                        selectedTab = destination
                    }
                },
                isFrench = isFrench
            )
        }
    ) { innerPadding ->
        // Generous bottom clearance ensuring the floating navigation bar does not cover content on any device
        val bottomBarPadding = maxOf(innerPadding.calculateBottomPadding() + 44.dp, 180.dp)

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                WeatherNavDestination.HOME -> {
                    HomeScreen(
                        uiState = uiState,
                        currentCity = currentCity,
                        tempUnit = tempUnit,
                        windUnit = windUnit,
                        alerts = activeAlerts,
                        isSaved = isCurrentCitySaved,
                        onToggleSave = { viewModel.toggleSaveCity(currentCity) },
                        onRefresh = { viewModel.refreshWeather() },
                        isRefreshing = isRefreshing,
                        currentTimeMillis = currentAppTimeMillis,
                        lastSyncedMillis = lastSyncedMillis,
                        syncStatusMessage = syncStatusMessage,
                        onRequestGpsLocation = {
                            if (viewModel.hasGpsPermission()) {
                                viewModel.loadWeatherForCurrentLocation()
                            } else {
                                viewModel.returnToHomeLocation()
                                handleRequestGps()
                            }
                        },
                        hasLocationPermission = viewModel.hasGpsPermission(),
                        isFrench = isFrench,
                        timeFormat = timeFormat,
                        bottomBarPadding = bottomBarPadding
                    )
                }

                WeatherNavDestination.EXPLORE -> {
                    ExploreScreen(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        isSearching = isSearching,
                        exploreWeatherMap = exploreWeatherMap,
                        savedCities = savedCities,
                        tempUnit = tempUnit,
                        onQueryChange = { viewModel.onSearchQueryChanged(it) },
                        onClearSearch = { viewModel.clearSearch() },
                        onCitySelected = { city ->
                            viewModel.selectCityFromSearch(city)
                            selectedTab = WeatherNavDestination.HOME
                        },
                        isFrench = isFrench,
                        bottomBarPadding = bottomBarPadding
                    )
                }

                WeatherNavDestination.STATS -> {
                    AtmosphereDetailsScreen(
                        report = fullReport,
                        tempUnit = tempUnit,
                        windUnit = windUnit,
                        alerts = activeAlerts,
                        isFrench = isFrench,
                        timeFormat = timeFormat,
                        bottomBarPadding = bottomBarPadding
                    )
                }

                WeatherNavDestination.SETTINGS -> {
                    SettingsScreen(
                        tempUnit = tempUnit,
                        windUnit = windUnit,
                        savedCities = savedCities,
                        hasGpsPermission = viewModel.hasGpsPermission(),
                        alertsEnabled = alertsEnabled,
                        onToggleAlertsEnabled = { viewModel.setAlertsEnabled(it) },
                        onSetTempUnit = { viewModel.setTemperatureUnit(it) },
                        onSetWindUnit = { viewModel.setWindSpeedUnit(it) },
                        onRequestGpsLocation = handleRequestGps,
                        onRemoveSavedCity = { viewModel.toggleSaveCity(it) },
                        onSelectCity = { city ->
                            viewModel.loadWeatherForCity(city)
                            selectedTab = WeatherNavDestination.HOME
                        },
                        themeMode = themeMode,
                        onSetThemeMode = { viewModel.setThemeMode(it) },
                        isFrench = isFrench,
                        appLanguage = appLanguage,
                        onSetLanguage = { viewModel.setLanguage(it) },
                        syncIntervalMinutes = syncIntervalMinutes,
                        onSetSyncInterval = { viewModel.setSyncInterval(it) },
                        timeFormat = timeFormat,
                        onSetTimeFormat = { viewModel.setTimeFormat(it) },
                        isEmergencyModeActive = isEmergencyModeActive,
                        activeEmergencyReason = activeEmergencyReason,
                        onTriggerEmergencyMode = { viewModel.triggerEmergencyMode(it) },
                        onStopEmergencyMode = { viewModel.stopEmergencyMode() },
                        onResetAppData = { viewModel.resetAppData() },
                        bottomBarPadding = bottomBarPadding
                    )
                }
            }
        }
    }
}
