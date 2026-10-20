package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.AccentRainBlue

enum class WeatherNavDestination(
    val title: String,
    val titleFr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", "Accueil", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    EXPLORE("Explore", "Explorer", Icons.Filled.Search, Icons.Outlined.Search, "nav_explore"),
    STATS("Atmosphere", "Atmosphère", Icons.Filled.Air, Icons.Outlined.Air, "nav_stats"),
    SETTINGS("Settings", "Paramètres", Icons.Filled.Settings, Icons.Outlined.Settings, "nav_settings")
}

@Composable
fun CurvedBottomNav(
    selectedDestination: WeatherNavDestination,
    onDestinationSelected: (WeatherNavDestination) -> Unit,
    isFrench: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(34.dp), spotColor = Color(0x330284C7)),
            shape = RoundedCornerShape(34.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherNavDestination.values().forEach { destination ->
                    val isSelected = destination == selectedDestination
                    val iconTint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

                    Box(
                        modifier = Modifier
                            .testTag(destination.testTag)
                            .size(54.dp)
                            .clip(CircleShape)
                            .clickable {
                                onDestinationSelected(destination)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .shadow(6.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        }

                        Icon(
                            imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                            contentDescription = if (isFrench) destination.titleFr else destination.title,
                            tint = iconTint,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}
