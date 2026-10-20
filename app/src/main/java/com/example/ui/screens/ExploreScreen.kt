package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.location.LocationTracker
import com.example.data.model.CityLocation
import com.example.data.model.FullWeatherReport
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.ui.components.WeatherIconView
import com.example.ui.theme.AccentRainBlue
import com.example.ui.theme.weatherBackgroundBrush

@Composable
fun ExploreScreen(
    searchQuery: String,
    searchResults: List<CityLocation>,
    isSearching: Boolean,
    exploreWeatherMap: Map<String, FullWeatherReport>,
    savedCities: List<CityLocation>,
    tempUnit: TemperatureUnit,
    onQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onCitySelected: (CityLocation) -> Unit,
    isFrench: Boolean = false,
    bottomBarPadding: Dp = 144.dp,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val handleCitySelected: (CityLocation) -> Unit = { city ->
        keyboardController?.hide()
        focusManager.clearFocus()
        onCitySelected(city)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = weatherBackgroundBrush())
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("explore_screen_list"),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = bottomBarPadding),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Screen Header: Major Cities
            item {
                Text(
                    text = com.example.ui.util.WeatherStrings.majorCities(isFrench),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 28.sp
                    )
                )
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("city_search_input"),
                    placeholder = {
                        Text(
                            text = com.example.ui.util.WeatherStrings.searchCityPlaceholder(isFrench),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = AccentRainBlue
                        )
                    },
                    trailingIcon = {
                        if (isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = AccentRainBlue
                            )
                        } else if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                                onClearSearch()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(26.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = AccentRainBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    singleLine = true
                )
            }

            // Search Results dropdown
            if (searchResults.isNotEmpty()) {
                item {
                    Text(
                        text = com.example.ui.util.WeatherStrings.searchResults(isFrench),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                items(searchResults) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { handleCitySelected(city) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationCity,
                                    contentDescription = null,
                                    tint = AccentRainBlue
                                )
                                Column {
                                    Text(
                                        text = city.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = listOfNotNull(city.stateOrRegion, city.country).joinToString(", "),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Select",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }

            // Saved Cities section if any exist
            if (savedCities.isNotEmpty() && searchQuery.isEmpty()) {
                item {
                    Text(
                        text = com.example.ui.util.WeatherStrings.savedLocations(isFrench),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                items(savedCities) { city ->
                    val weatherReport = exploreWeatherMap[city.name]
                    CityWeatherCard(
                        city = city,
                        report = weatherReport,
                        tempUnit = tempUnit,
                        isFrench = isFrench,
                        onClick = { handleCitySelected(city) }
                    )
                }
            }

            // Curated Major Cities
            if (searchQuery.isEmpty()) {
                item {
                    Text(
                        text = com.example.ui.util.WeatherStrings.majorCities(isFrench),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                items(LocationTracker.CURATED_EXPLORE_CITIES) { city ->
                    val weatherReport = exploreWeatherMap[city.name]
                    CityWeatherCard(
                        city = city,
                        report = weatherReport,
                        tempUnit = tempUnit,
                        isFrench = isFrench,
                        onClick = { handleCitySelected(city) }
                    )
                }
            }

            // Bottom clearance spacer for complete scrollability above curved bottom nav
            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun CityWeatherCard(
    city: CityLocation,
    report: FullWeatherReport?,
    tempUnit: TemperatureUnit,
    isFrench: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("explore_city_${city.name}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Big Temperature
                val tempText = if (report != null) {
                    tempUnit.format(report.current.temperature)
                } else {
                    "--"
                }

                Text(
                    text = tempText,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 38.sp
                    )
                )

                // Condition Title
                val conditionText = report?.current?.condition?.localizedTitle(isFrench)
                    ?: if (isFrench) "Chargement..." else "Loading..."
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$conditionText >",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // City and Country
                Text(
                    text = city.displayTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                // High / Low temp
                val daily = report?.daily?.firstOrNull()
                if (daily != null) {
                    Text(
                        text = "${tempUnit.format(daily.minTemperature)} ~ ${tempUnit.format(daily.maxTemperature)}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            // Weather Illustration
            val condition = report?.current?.condition ?: WeatherCondition.PARTLY_CLOUDY
            WeatherIconView(
                condition = condition,
                isDay = report?.current?.isDay ?: true,
                size = 76.dp
            )
        }
    }
}
