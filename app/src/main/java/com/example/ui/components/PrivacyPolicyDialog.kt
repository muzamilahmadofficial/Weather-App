package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.AccentRainBlue

enum class LegalTab {
    PRIVACY,
    TERMS
}

@Composable
fun PrivacyPolicyDialog(
    onDismissRequest: () -> Unit,
    isFrench: Boolean = false,
    initialTab: LegalTab = LegalTab.PRIVACY
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.86f)
                .testTag("privacy_policy_dialog"),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
            ) {
                // Header with icon, title, and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AccentRainBlue.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (selectedTab == LegalTab.PRIVACY) Icons.Default.Security else Icons.Default.Gavel,
                                contentDescription = null,
                                tint = AccentRainBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (selectedTab == LegalTab.PRIVACY) {
                                    if (isFrench) "Politique de confidentialité" else "Privacy Policy"
                                } else {
                                    if (isFrench) "Conditions d'utilisation" else "Terms of Service"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Atmosphere Weather • Google Play Ready",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("close_privacy_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher (Privacy Policy / Terms of Service)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == LegalTab.PRIVACY,
                        onClick = { selectedTab = LegalTab.PRIVACY },
                        label = {
                            Text(
                                text = if (isFrench) "Confidentialité" else "Privacy Policy",
                                fontWeight = if (selectedTab == LegalTab.PRIVACY) FontWeight.Bold else FontWeight.SemiBold
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentRainBlue,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    FilterChip(
                        selected = selectedTab == LegalTab.TERMS,
                        onClick = { selectedTab = LegalTab.TERMS },
                        label = {
                            Text(
                                text = if (isFrench) "Conditions & Avis" else "Terms & Disclaimer",
                                fontWeight = if (selectedTab == LegalTab.TERMS) FontWeight.Bold else FontWeight.SemiBold
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentRainBlue,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (selectedTab == LegalTab.PRIVACY) {
                        PrivacyPolicyContent(isFrench)
                    } else {
                        TermsContent(isFrench)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Action
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = AccentRainBlue,
                    onClick = onDismissRequest
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isFrench) "Fermer et continuer" else "Close & Return to App",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacyPolicyContent(isFrench: Boolean) {
    if (isFrench) {
        PolicySectionTitle("1. Introduction et engagement")
        PolicyParagraph("L'application Atmosphere respecte pleinement votre vie privée. Cette politique de confidentialité détaille la manière dont nous traitons vos données personnelles dans le cadre des directives de sécurité de Google Play.")

        PolicySectionTitle("2. Données collectées et finalités")
        PolicyBullet("Données de géolocalisation (GPS) :", "Avec votre accord explicite, Atmosphere accède aux coordonnées de latitude et longitude de votre appareil exclusivement pour interroger les prévisions météorologiques locales en temps réel.")
        PolicyBullet("Zéro profilage ni traçage :", "Vos coordonnées GPS ne sont jamais enregistrées sur des serveurs distants, ne sont jamais liées à votre identité et ne sont jamais vendues à des courtiers de données.")
        PolicyBullet("Préférences locales :", "Vos villes enregistrées, unités de mesure (°C/°F, km/h, mph), thème et formats d'heure sont conservés strictement sur votre appareil via Jetpack DataStore.")

        PolicySectionTitle("3. Autorisations requises (Android)")
        PolicyBullet("ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION :", "Requises pour détecter votre météo locale actuelle. Vous pouvez refuser cette autorisation et choisir manuellement vos villes sans restriction.")
        PolicyBullet("POST_NOTIFICATIONS :", "Requise pour afficher la météo en direct dans la barre d'état et recevoir des alertes lors d'orages violents ou canicules.")
        PolicyBullet("FOREGROUND_SERVICE & DATA_SYNC :", "Utilisées en mode Urgence pour surveiller les intempéries majeures et actualiser les alertes critiques en continu.")

        PolicySectionTitle("4. Fournisseurs tiers et transmission chiffrée")
        PolicyParagraph("Les données météo sont fournies par l'API Open-Meteo. Toutes les requêtes réseau sont chiffrées selon le protocole sécurisé HTTPS (TLS). Aucun SDK publicitaire, d'analyse comportementale ou de suivi tiers n'est inclus dans l'application.")

        PolicySectionTitle("5. Conservation et suppression des données")
        PolicyParagraph("Vous pouvez à tout moment effacer toutes vos données locales et réinitialiser vos paramètres via le bouton dédié dans l'écran Paramètres de l'application.")

        PolicySectionTitle("6. Contact et support développeur")
        PolicyParagraph("Pour toute question ou demande concernant cette politique, contactez-nous à : muzamilofficially@gmail.com")
    } else {
        PolicySectionTitle("1. Commitment to User Privacy")
        PolicyParagraph("Atmosphere is designed with privacy-first principles. We do not track you, sell your information, or build behavioral advertising profiles. This Privacy Policy outlines how your data is accessed and handled in full accordance with Google Play Developer Policies.")

        PolicySectionTitle("2. Information We Access & Purpose")
        PolicyBullet("Precise & Coarse Location :", "When permitted, your device's latitude and longitude are accessed solely to fetch accurate local meteorological forecasts, radar trends, and severe weather advisories.")
        PolicyBullet("Zero Data Brokerage :", "Location coordinates are processed in transient memory and transmitted over secure HTTPS to open meteorological servers. They are never stored on external databases or linked to any personal identifier.")
        PolicyBullet("Device-Local Preferences :", "Saved locations, temperature units (°C / °F), wind speed units, visual themes, and time formats (12h / 24h) are stored strictly on-device using Android Jetpack DataStore.")

        PolicySectionTitle("3. Android Permissions Declared")
        PolicyBullet("ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION :", "Used exclusively for automatic local weather detection. Optional: you can deny location access and search any city manually.")
        PolicyBullet("POST_NOTIFICATIONS :", "Used to show ongoing live temperature in the status bar and push urgent warnings during extreme weather.")
        PolicyBullet("FOREGROUND_SERVICE & FOREGROUND_SERVICE_DATA_SYNC :", "Used only in active Emergency Weather Mode to ensure timely 2-minute updates during severe storms.")

        PolicySectionTitle("4. Third-Party Services & Encryption")
        PolicyParagraph("Weather data is retrieved from Open-Meteo open weather models. All network communication is encrypted using industry-standard HTTPS (TLS 1.3). Atmosphere contains zero third-party advertisement SDKs and zero analytics trackers.")

        PolicySectionTitle("5. Data Retention & User Control")
        PolicyParagraph("Because all data is stored locally on your device, you maintain complete sovereignty over your records. You can reset all cache and remove saved locations anytime directly from Settings.")

        PolicySectionTitle("6. Children's Privacy Compliance")
        PolicyParagraph("Atmosphere complies with COPPA and the Google Play Families Policy. We do not knowingly collect personal identifiable information from children.")

        PolicySectionTitle("7. Contact & Developer Information")
        PolicyParagraph("For support or privacy inquiries, contact: muzamilofficially@gmail.com")
        PolicyParagraph("Last updated: October 2026")
    }
}

@Composable
private fun TermsContent(isFrench: Boolean) {
    if (isFrench) {
        PolicySectionTitle("1. Conditions d'utilisation")
        PolicyParagraph("En utilisant Atmosphere Weather, vous acceptez les présentes conditions d'utilisation.")

        PolicySectionTitle("2. Avertissement météorologique")
        PolicyParagraph("Les prévisions, indices UV, vitesses de vent et alertes sont générés à partir de modèles numériques globaux (ECMWF, NOAA, DWD). Bien que nous fassions tout notre possible pour garantir l'exactitude des données, la météo reste soumise à des variations naturelles imprévisibles.")

        PolicySectionTitle("3. Urgences et sécurité civile")
        PolicyParagraph("Cette application ne remplace pas les consignes de sécurité des autorités locales ou de la protection civile. En cas d'intempéries sévères, foudre, inondation ou tornade, suivez toujours prioritairement les alertes officielles d'urgence.")

        PolicySectionTitle("4. Attribution des données ouvertes")
        PolicyParagraph("Données météorologiques propulsées par Open-Meteo sous licence CC BY 4.0.")
    } else {
        PolicySectionTitle("1. Acceptance of Terms")
        PolicyParagraph("By installing or using Atmosphere Weather, you agree to these Terms of Service.")

        PolicySectionTitle("2. Meteorological Disclaimer")
        PolicyParagraph("Atmospheric forecasts, UV indices, precipitation forecasts, and weather warnings are derived from advanced global numerical models (ECMWF, NOAA, DWD). While we strive for maximum accuracy, meteorological predictions cannot be guaranteed without error.")

        PolicySectionTitle("3. Civil Defense & Life Safety")
        PolicyParagraph("Atmosphere is an informational weather utility. In the event of severe weather emergencies (tornadoes, hurricanes, lightning storms, flash floods), always heed the instructions and mandatory evacuation orders of local emergency management agencies and civil protection authorities.")

        PolicySectionTitle("4. Open Data Attribution")
        PolicyParagraph("Meteorological telemetry is powered by Open-Meteo under the Creative Commons Attribution 4.0 International License (CC BY 4.0).")
    }
}

@Composable
private fun PolicySectionTitle(title: String) {
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.5.sp
        )
    )
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun PolicyParagraph(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
    )
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun PolicyBullet(title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "•",
            color = AccentRainBlue,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = buildString {
                append(title)
                append(" ")
                append(desc)
            },
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        )
    }
}
