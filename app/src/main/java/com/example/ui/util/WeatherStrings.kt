package com.example.ui.util

import com.example.data.preferences.AppLanguage

object WeatherStrings {

    fun navHome(isFrench: Boolean) = if (isFrench) "Accueil" else "Home"
    fun navExplore(isFrench: Boolean) = if (isFrench) "Explorer" else "Explore"
    fun navAtmosphere(isFrench: Boolean) = if (isFrench) "Atmosphère" else "Atmosphere"
    fun navSettings(isFrench: Boolean) = if (isFrench) "Paramètres" else "Settings"

    // Home
    fun syncingWeather(isFrench: Boolean) = if (isFrench) "Synchronisation météo..." else "Synchronizing weather..."
    fun updatingAtmosphere(isFrench: Boolean) = if (isFrench) "Mise à jour de l'atmosphère..." else "Updating atmosphere..."
    fun updatingAtmosphereSubtitle(isFrench: Boolean) = if (isFrench) "Mise à jour de la température, de l'atmosphère et des prévisions radar" else "Updating temperature, atmosphere, and radar forecast"
    fun useAutoGps(isFrench: Boolean) = if (isFrench) "Utiliser la localisation automatique" else "Use automatic location"
    fun useAutoGpsSub(isFrench: Boolean) = if (isFrench) "Appuyez pour afficher la météo locale en direct" else "Tap to show live local weather at your location"
    fun currentGpsLocation(isFrench: Boolean) = if (isFrench) "Position GPS actuelle" else "Current GPS Location"
    fun returnToGps(isFrench: Boolean) = if (isFrench) "Retour à ma position" else "Return to My Location"
    fun feelsLike(isFrench: Boolean) = if (isFrench) "Ressenti" else "Feels like"
    fun highShort(isFrench: Boolean) = if (isFrench) "Max" else "H"
    fun lowShort(isFrench: Boolean) = if (isFrench) "Min" else "L"
    fun hourlyTrends(isFrench: Boolean) = if (isFrench) "Tendances horaires 24h" else "Hourly 24h Trends"
    fun dailyForecast(isFrench: Boolean) = if (isFrench) "Prévisions sur 7 jours" else "7-Day Daily Forecast"
    fun weatherUnavailable(isFrench: Boolean) = if (isFrench) "Météo indisponible" else "Weather Unavailable"
    fun retryWeather(isFrench: Boolean) = if (isFrench) "Réessayer" else "Retry Weather"

    // Metrics
    fun humidity(isFrench: Boolean) = if (isFrench) "Humidité" else "Humidity"
    fun wind(isFrench: Boolean) = if (isFrench) "Vent" else "Wind"
    fun uvIndex(isFrench: Boolean) = if (isFrench) "Indice UV" else "UV Index"
    fun pressure(isFrench: Boolean) = if (isFrench) "Pression" else "Pressure"
    fun precipitation(isFrench: Boolean) = if (isFrench) "Précipitations" else "Precipitation"
    fun cloudCover(isFrench: Boolean) = if (isFrench) "Couverture nuageuse" else "Cloud Cover"

    // Explore
    fun majorCities(isFrench: Boolean) = if (isFrench) "Villes principales" else "Major Cities"
    fun searchCityPlaceholder(isFrench: Boolean) = if (isFrench) "Rechercher une ville..." else "Search for a city..."
    fun searchResults(isFrench: Boolean) = if (isFrench) "Résultats de recherche" else "Search Results"
    fun savedLocations(isFrench: Boolean) = if (isFrench) "Emplacements enregistrés" else "Saved Locations"

    // Atmosphere
    fun atmosphericDetails(isFrench: Boolean) = if (isFrench) "Détails atmosphériques" else "Atmospheric Details"
    fun liveTelemetry(isFrench: Boolean) = if (isFrench) "Télémétrie de haute précision en direct" else "Live high-precision telemetry"
    fun windSpeedTitle(isFrench: Boolean) = if (isFrench) "VITESSE ET DIRECTION DU VENT" else "WIND SPEED & DIRECTION"
    fun uvSafetyTitle(isFrench: Boolean) = if (isFrench) "INDICE UV ET SOLEIL" else "UV INDEX & SUN SAFETY"
    fun humidityTitle(isFrench: Boolean) = if (isFrench) "HUMIDITÉ ET POINT DE ROSÉE" else "HUMIDITY & DEW POINT"
    fun pressureTitle(isFrench: Boolean) = if (isFrench) "PRESSION BAROMÉTRIQUE" else "BAROMETRIC PRESSURE"
    fun sunCycleTitle(isFrench: Boolean) = if (isFrench) "LEVER ET COUCHER DU SOLEIL" else "SUNRISE & SUNSET"
    fun sunrise(isFrench: Boolean) = if (isFrench) "Lever du soleil" else "Sunrise"
    fun sunset(isFrench: Boolean) = if (isFrench) "Coucher du soleil" else "Sunset"

    // Settings
    fun settingsTitle(isFrench: Boolean) = if (isFrench) "Paramètres" else "Settings"
    fun settingsSubtitle(isFrench: Boolean) = if (isFrench) "Préférences et alertes météo" else "Atmosphere Preferences & Alerts"
    fun languageSection(isFrench: Boolean) = if (isFrench) "LANGUE" else "LANGUAGE"
    fun tempUnitSection(isFrench: Boolean) = if (isFrench) "UNITÉ DE TEMPÉRATURE" else "TEMPERATURE UNIT"
    fun windUnitSection(isFrench: Boolean) = if (isFrench) "UNITÉ DE VITESSE DU VENT" else "WIND SPEED UNIT"
    fun visualThemeSection(isFrench: Boolean) = if (isFrench) "THÈME VISUEL" else "VISUAL THEME"
    fun themeDeep(isFrench: Boolean) = if (isFrench) "Atmosphérique profond" else "Deep Atmospheric"
    fun themeLight(isFrench: Boolean) = if (isFrench) "Ciel clair" else "Clear Sky Light"
    fun severeAlertsSection(isFrench: Boolean) = if (isFrench) "ALERTES MÉTÉO SÉVÈRES" else "SEVERE WEATHER ALERTS"
    fun severeAlertsTitle(isFrench: Boolean) = if (isFrench) "Alertes en direct activées" else "Live Severe Alerts"
    fun severeAlertsSub(isFrench: Boolean) = if (isFrench) "Recevez des notifications immédiates lors d'orages violents, canicule ou tempêtes." else "Receive instant push notifications for storms, extreme heat, and dangerous conditions."
    fun testNotificationsTitle(isFrench: Boolean) = if (isFrench) "TEST DE NOTIFICATION" else "TEST NOTIFICATIONS"
    fun testNotificationsSub(isFrench: Boolean) = if (isFrench) "Déclenchez un test d'alerte pour vérifier les notifications." else "Simulate a severe weather event to verify sound, banner, and lock-screen visibility."
    fun stormAlertBtn(isFrench: Boolean) = if (isFrench) "⛈️ Alerte Orage" else "⛈️ Storm Alert"
    fun heatAlertBtn(isFrench: Boolean) = if (isFrench) "🔥 Alerte Canicule" else "🔥 Heat Alert"
    fun windAlertBtn(isFrench: Boolean) = if (isFrench) "💨 Alerte Vent" else "💨 Wind Alert"
    fun notificationWidgetSection(isFrench: Boolean) = if (isFrench) "WIDGET DE BARRE DE NOTIFICATION" else "NOTIFICATION STATUS BAR WIDGET"
    fun notificationWidgetActive(isFrench: Boolean) = if (isFrench) "Synchronisation en direct active" else "Live Fresh Synchronization Active"
    fun notificationWidgetDesc(isFrench: Boolean) = if (isFrench) {
        "La température et les conditions météo en direct sont continuellement synchronisées dans votre barre de notification avec des données fraîches."
    } else {
        "Live weather temperature and atmospheric conditions are automatically synchronized in your notification bar with fresh meteorological data."
    }
    fun manageSavedCities(isFrench: Boolean) = if (isFrench) "Gérer les villes enregistrées" else "Manage Saved Cities"
    fun aboutSection(isFrench: Boolean) = if (isFrench) "À PROPOS D'ATMOSPHERE" else "ABOUT ATMOSPHERE"
    fun aboutDesc(isFrench: Boolean) = if (isFrench) {
        "Météo mondiale en temps réel propulsée par les modèles météorologiques haute résolution ECMWF et NOAA. Sans compte requis."
    } else {
        "Real-time global weather powered by high-resolution ECMWF & NOAA meteorological models. No account required."
    }

    // Time Format (12h vs 24h)
    fun timeFormatSection(isFrench: Boolean) = if (isFrench) "FORMAT DE L'HEURE" else "TIME FORMAT"
    fun timeFormatSubtitle(isFrench: Boolean) = if (isFrench) "Affichage de l'horloge et des prévisions horaires" else "Clock display and hourly forecast intervals"
    fun time12h(isFrench: Boolean) = if (isFrench) "12 Heures (AM/PM)" else "12-Hour (AM/PM)"
    fun time24h(isFrench: Boolean) = if (isFrench) "24 Heures (00:00 - 23:59)" else "24-Hour (00:00 - 23:59)"

    // Background Sync & Emergency Mode
    fun syncIntervalSection(isFrench: Boolean) = if (isFrench) "SYNCHRONISATION EN ARRIÈRE-PLAN (WORKMANAGER)" else "BACKGROUND SYNCHRONIZATION (WORKMANAGER)"
    fun syncIntervalSubtitle(isFrench: Boolean) = if (isFrench) "Fréquence de mise à jour périodique des données météo" else "Periodic background weather update frequency"
    fun syncIntervalMinNotice(isFrench: Boolean) = if (isFrench) {
        "Règle de batterie stricte : le minimum est fixé à 30 minutes en fonctionnement normal afin de préserver l'autonomie de la batterie."
    } else {
        "Strict battery rule: normal background operations enforce a 30-minute minimum interval to preserve battery health and comply with OS limits."
    }
    fun emergencyModeSection(isFrench: Boolean) = if (isFrench) "MODE URGENCE MÉTÉO (SERVICE EN PREMIER PLAN)" else "CRITICAL EMERGENCY WEATHER MODE (FOREGROUND SERVICE)"
    fun emergencyModeActiveTitle(isFrench: Boolean) = if (isFrench) "🚨 MODE URGENCE ACTIF" else "🚨 EMERGENCY WEATHER MODE ACTIVE"
    fun emergencyModeInactiveTitle(isFrench: Boolean) = if (isFrench) "Mode normal (WorkManager planifié)" else "Normal Mode (WorkManager Periodic Sync)"
    fun emergencyModeDesc(isFrench: Boolean) = if (isFrench) {
        "En cas d'intempéries sévères (orage violent, tornade, canicule extrême), le mode Urgence contourne la limite des 15 min de WorkManager en lançant un service de premier plan persistant qui interroge les données toutes les 2 minutes."
    } else {
        "During severe weather events (thunderstorms, tornado alerts, gale winds), Emergency Mode overrides normal limits using an Android Foreground Service with a persistent notification to fetch instant updates every 2 minutes."
    }
    fun triggerEmergencyBtn(isFrench: Boolean) = if (isFrench) "Activer Mode Urgence (Suivi 2-min)" else "Activate Emergency Mode (2-min Sync)"
    fun stopEmergencyBtn(isFrench: Boolean) = if (isFrench) "Désactiver Mode Urgence & Reprendre Planificateur" else "Deactivate Emergency Mode & Revert to WorkManager"

    // Background Sync Custom Dialog
    fun customSyncChip(isFrench: Boolean) = if (isFrench) "Personnalisé..." else "Custom..."
    fun customSyncDialogTitle(isFrench: Boolean) = if (isFrench) "Définir l'intervalle personnalisé" else "Set Custom Sync Interval"
    fun customSyncMinutesLabel(isFrench: Boolean) = if (isFrench) "Intervalle en minutes (min. 30)" else "Interval in minutes (min. 30)"
    fun customSyncErrorMin(isFrench: Boolean) = if (isFrench) {
        "Veuillez entrer une valeur d'au moins 30 minutes pour respecter les règles de batterie d'Android."
    } else {
        "Please enter a value of at least 30 minutes to comply with Android battery optimization policies."
    }
    fun saveBtn(isFrench: Boolean) = if (isFrench) "Enregistrer" else "Save"

    // Privacy & Legal
    fun privacyLegalSection(isFrench: Boolean) = if (isFrench) "CONFIDENTIALITÉ ET CONFORMITÉ GOOGLE PLAY" else "PRIVACY & GOOGLE PLAY COMPLIANCE"
    fun privacyPolicyTitle(isFrench: Boolean) = if (isFrench) "Politique de confidentialité" else "Privacy Policy"
    fun privacyPolicyDesc(isFrench: Boolean) = if (isFrench) "Consulter notre politique complète conforme à Google Play" else "View our complete Google Play compliant privacy policy"
    fun termsTitle(isFrench: Boolean) = if (isFrench) "Conditions d'utilisation & Avis météo" else "Terms of Service & Weather Disclaimer"
    fun termsDesc(isFrench: Boolean) = if (isFrench) "Modèles météorologiques et consignes de sécurité" else "Meteorological model attributions and civil defense notices"
    fun attributionTitle(isFrench: Boolean) = if (isFrench) "Attribution des données & Open Source" else "Data Attribution & Open Source"
    fun attributionDesc(isFrench: Boolean) = if (isFrench) "Données météo propulsées par Open-Meteo sous licence CC BY 4.0" else "Weather forecasts powered by Open-Meteo under CC BY 4.0 license"
    fun contactSupportTitle(isFrench: Boolean) = if (isFrench) "Contact développeur et assistance" else "Developer Contact & Support"
    fun contactSupportDesc(isFrench: Boolean) = if (isFrench) "muzamilofficially@gmail.com • Retour et assistance" else "muzamilofficially@gmail.com • Feedback & Inquiries"
    fun clearCacheTitle(isFrench: Boolean) = if (isFrench) "Effacer le cache et réinitialiser" else "Clear Local Cache & Reset Data"
    fun clearCacheDesc(isFrench: Boolean) = if (isFrench) "Supprime toutes les villes enregistrées et réinitialise les préférences" else "Wipes saved cities and restores default preferences"
    fun clearCacheConfirmTitle(isFrench: Boolean) = if (isFrench) "Réinitialiser les données locales ?" else "Reset Local Data?"
    fun clearCacheConfirmDesc(isFrench: Boolean) = if (isFrench) {
        "Cette action effacera vos villes enregistrées et réinitialisera les préférences. Les données météo locales seront rechargées."
    } else {
        "This will clear all saved cities, search history, and reset your units and preferences to default. Live weather will reload."
    }
    fun confirmResetBtn(isFrench: Boolean) = if (isFrench) "Réinitialiser" else "Reset Data"
    fun cancelBtn(isFrench: Boolean) = if (isFrench) "Annuler" else "Cancel"

    fun dayLabel(dayLabel: String, isFrench: Boolean): String {
        if (!isFrench) return dayLabel
        return when (dayLabel.trim().uppercase()) {
            "TODAY" -> "Aujourd'hui"
            "MON", "MONDAY" -> "Lun"
            "TUE", "TUESDAY" -> "Mar"
            "WED", "WEDNESDAY" -> "Mer"
            "THU", "THURSDAY" -> "Jeu"
            "FRI", "FRIDAY" -> "Ven"
            "SAT", "SATURDAY" -> "Sam"
            "SUN", "SUNDAY" -> "Dim"
            else -> dayLabel
        }
    }
}
