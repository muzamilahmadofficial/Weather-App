# Privacy Policy for Atmosphere Weather

**Last Updated:** October 19, 2026  
**Developer Contact:** [muzamilofficially@gmail.com](mailto:muzamilofficially@gmail.com)  
**Application Name:** Atmosphere Weather  
**Package / Application ID:** `com.aistudio.weather.vkywpa`

---

## 1. Introduction

Atmosphere Weather ("we", "our", or "the App") is committed to protecting your privacy. This Privacy Policy details how Atmosphere accesses, handles, and protects user data in accordance with the Google Play Developer Program Policies, the General Data Protection Regulation (GDPR), and the California Consumer Privacy Act (CCPA).

Atmosphere is built with privacy-first architecture: **We do not track you, we do not require account creation, we do not sell your personal data, and we do not serve behavioral advertisements.**

---

## 2. Information We Collect and Access

### A. Location Information (GPS & Approximate Coordinates)
- **What is accessed:** When you grant permission, the App accesses your device's geographical coordinates (`ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION`).
- **Why it is accessed:** To query and display local real-time meteorological conditions, hourly trends, 7-day weather forecasts, and active severe storm warnings.
- **How it is processed:** Coordinates are used in transient memory only and sent securely over encrypted HTTPS directly to the Open-Meteo meteorological API.
- **Zero Storage on Remote Servers:** Your coordinates are never stored on any remote database, never logged, and never connected to your personal identity.
- **User Control:** You may deny location access at any time. Atmosphere remains fully functional without GPS by allowing you to manually search and save any city worldwide.

### B. Device-Local Preferences
- **What is stored:** Saved cities, temperature scale (°C or °F), wind speed unit (km/h, mph, m/s), time format (12-hour or 24-hour), visual themes, and background sync intervals.
- **Storage Location:** Stored solely on your physical device using Android Jetpack DataStore. This data never leaves your device.

---

## 3. Android Permissions Declared & Justifications

| Permission | Purpose | Optional / Mandatory |
| :--- | :--- | :--- |
| `android.permission.INTERNET` | Retrieve weather forecast and geocoding data over secure HTTPS. | Mandatory for live data |
| `android.permission.ACCESS_NETWORK_STATE` | Check connectivity status before issuing network calls to preserve battery. | Mandatory |
| `android.permission.ACCESS_FINE_LOCATION` | Detect high-precision local weather at user's current GPS position. | Optional |
| `android.permission.ACCESS_COARSE_LOCATION` | Approximate location detection for city-level weather. | Optional |
| `android.permission.POST_NOTIFICATIONS` | Deliver live status bar weather updates and severe weather emergency warnings. | Optional |
| `android.permission.FOREGROUND_SERVICE` | Maintain rapid weather monitoring during active severe storm emergencies. | Triggered during active weather emergencies |
| `android.permission.FOREGROUND_SERVICE_DATA_SYNC` | Enforce compliance with Android 14+ foreground service data synchronization rules. | Required for emergency sync service |

---

## 4. Third-Party Services & Data Sharing

Atmosphere utilizes open meteorological models provided by **Open-Meteo** (incorporating data from ECMWF, NOAA, and DWD).
- Communication is conducted strictly over encrypted **HTTPS (TLS 1.3)**.
- Queries contain only geographical coordinates (`latitude` and `longitude`), without device identifiers, names, phone numbers, or account IDs.
- **Zero Advertising SDKs:** We do not include any advertisement SDKs (e.g., AdMob, Unity, AppLovin).
- **Zero Analytics Trackers:** We do not embed third-party analytics trackers (e.g., Facebook SDK, Adjust, AppsFlyer).

---

## 5. Data Retention & User Deletion Rights

Because Atmosphere does not operate a remote user account database, all user preferences reside exclusively on your local device storage.
- You can clear all cached data, search history, and saved locations instantly from inside the App under **Settings > Clear Local Cache & Reset Data**.
- Uninstalling the App immediately removes all stored DataStore files from your device.

---

## 6. Children's Privacy (COPPA & Families Policy)

Atmosphere complies with the Children's Online Privacy Protection Act (COPPA) and Google Play's Families Policy. We do not knowingly collect personal identifiable information from children under the age of 13.

---

## 7. Changes to This Privacy Policy

We may update this Privacy Policy from time to time. Any changes will be posted within the App and reflected with a revised "Last Updated" date.

---

## 8. Contact Us

If you have any questions, feedback, or concerns regarding this Privacy Policy or your data privacy, please contact the developer at:

**Email:** [muzamilofficially@gmail.com](mailto:muzamilofficially@gmail.com)  
**Developer:** Atmosphere Weather Development Team
