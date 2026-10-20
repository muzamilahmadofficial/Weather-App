# Google Play Store Listing & Visual Assets Package

This document contains all visual assets, metadata, descriptions, and the complete 30-second YouTube promo video storyboard and script ready for your Google Play Console submission.

---

## 1. Visual Assets Summary & File Locations

All visual graphics have been generated and validated to match Google Play Store submission requirements:

| Asset | File Name / Path | Dimensions | Format & Size | Status |
| :--- | :--- | :--- | :--- | :--- |
| **App Icon** | `play_store_assets/store_app_icon_512x512.png` | **512 x 512 px** | PNG32 (50 KB) | Ready for Upload |
| **Feature Graphic** | `play_store_assets/store_feature_graphic_1024x500.png` | **1024 x 500 px** | PNG32 (65 KB) | Ready for Upload |
| **Phone Screenshot 1** | `play_store_assets/screenshot_1_live_forecast.png` | **1080 x 1920 px (9:16)** | PNG32 (126 KB) | Ready for Upload |
| **Phone Screenshot 2** | `play_store_assets/screenshot_2_hourly_forecast.png` | **1080 x 1920 px (9:16)** | PNG32 (126 KB) | Ready for Upload |
| **Phone Screenshot 3** | `play_store_assets/screenshot_3_severe_alerts.png` | **1080 x 1920 px (9:16)** | PNG32 (141 KB) | Ready for Upload |
| **Phone Screenshot 4** | `play_store_assets/screenshot_4_sync_privacy.png` | **1080 x 1920 px (9:16)** | PNG32 (155 KB) | Ready for Upload |

> **Google Play Console Upload Instructions:**
> 1. Go to **Google Play Console** > select your app > **Grow** > **Store presence** > **Main store listing**.
> 2. Under **App icon**, upload `store_app_icon_512x512.png`.
> 3. Under **Feature graphic**, upload `store_feature_graphic_1024x500.png`.
> 4. Under **Phone screenshots**, upload `screenshot_1_live_forecast.png`, `screenshot_2_hourly_forecast.png`, `screenshot_3_severe_alerts.png`, and `screenshot_4_sync_privacy.png` in sequence.

---

## 2. Store Listing Text Copy

### App Title (30 characters max)
```
Atmosphere: Real-Time Weather
```
*(29 characters — fully compliant with Google Play title policy, no buzzwords, no ALL CAPS)*

### Short Description (80 characters max)
```
Real-time weather forecasts, hourly trends, severe storm alerts & smart sync.
```
*(77 characters — concise, action-oriented, keyword-optimized)*

### Full Description (Up to 4,000 characters)
```text
Experience precision meteorological intelligence with Atmosphere Weather — a modern, fast, and privacy-first weather companion built with Android Jetpack Compose and Material Design 3.

Whether you are checking daily conditions, planning your week with smooth hourly graphs, or tracking extreme storm warnings, Atmosphere delivers real-time updates directly to your fingertips and your notification bar.

🌟 KEY FEATURES

• REAL-TIME WEATHER TELEMETRY
Instant temperature, feels-like temperatures, wind speed, wind gusts, atmospheric pressure, humidity, and UV indices. Powered by open numerical weather prediction models (ECMWF, NOAA, DWD) via Open-Meteo.

• INTERACTIVE HOURLY CURVE GRAPHS
Visualize temperature trends with smooth cubic Bézier graphs. Switch effortlessly between 12-hour (AM/PM) and 24-hour clock formats based on your personal preference.

• 7-DAY EXTENDED FORECASTS
Detailed daily breakdowns including daily highs, lows, precipitation probabilities, and sunrise/sunset times.

• CRITICAL EMERGENCY WEATHER MODE (FOREGROUND SERVICE)
When dangerous conditions arise (thunderstorms, tornado advisories, gale winds), Atmosphere can engage Emergency Mode using an Android Foreground Service with a persistent status notification to deliver rapid updates every 2 minutes.

• CUSTOM WORKMANAGER BACKGROUND SYNCHRONIZATION
Take complete control of background updates. Choose your sync frequency (30 min, 1 hour, 2 hours, 6 hours) or define custom intervals. A strict 30-minute minimum rule is enforced during normal operations to safeguard your device's battery life.

• CLEAN NOTIFICATION STATUS WIDGET
Stay updated without opening the app. View real-time conditions directly from your notification drawer with a clean single-timestamp header.

• ZERO TRACKING & PRIVACY-FIRST COMMITMENT
Your privacy is our priority. Atmosphere does not track you, does not sell your information, and contains zero third-party advertising or analytics SDKs. Location coordinates are transmitted over secure HTTPS (TLS 1.3) solely for meteorological lookups and are never saved on external servers.

• FULL MULTI-LANGUAGE SUPPORT
Complete, native localization in English and French.

PERMISSIONS EXPLAINED:
• ACCESS_FINE_LOCATION & ACCESS_COARSE_LOCATION: Used solely to detect your current local weather. You can also search cities manually without granting GPS access.
• POST_NOTIFICATIONS: Used to show live status updates and critical severe weather advisories.
• FOREGROUND_SERVICE & DATA_SYNC: Used exclusively during active Emergency Weather Mode for continuous storm monitoring.

Open Data Attribution: Weather forecasts powered by Open-Meteo under Creative Commons Attribution 4.0 International (CC BY 4.0).
Developer Support: muzamilofficially@gmail.com
```

---

## 3. YouTube Promo Video Details & 30-Second Production Script

In Google Play Console, enter your YouTube URL in the format:
```
https://www.youtube.com/watch?v=<YOUR_VIDEO_ID>
```
*(Note: Google Play requires that the video is Public or Unlisted, with ads turned OFF, and not age-restricted).*

### 30-Second Promo Video Storyboard & Script

| Time | Visual / Video Scene | On-Screen Text Callout | Voiceover / Audio Track |
| :--- | :--- | :--- | :--- |
| **0:00 - 0:05** | Smooth cinematic zoom into a phone running Atmosphere on deep midnight blue theme. Current temperature (21°C) glows with smooth cloud animation. | **ATMOSPHERE WEATHER**<br>Precision in Every Degree | *Upbeat ambient synth music rises.*<br>**Voiceover:** "Meet Atmosphere — the next-generation weather app built for speed, beauty, and precision." |
| **0:05 - 0:11** | Swipe down revealing the 24-hour temperature curve graph. Finger taps to switch between 12-hour and 24-hour modes. | **HOURLY TRENDS & 7-DAY FORECAST**<br>12h / 24h Clock Formats | **Voiceover:** "Track fluid hourly temperature curves and 7-day forecasts with flexible 12-hour or 24-hour formats." |
| **0:11 - 0:18** | Screen shifts to high-contrast red emergency warning banner: "Severe Thunderstorm Warning". Phone status bar displays 2-minute foreground sync notification. | **CRITICAL EMERGENCY MODE**<br>Rapid 2-Minute Sync | **Voiceover:** "When severe storms strike, Emergency Mode kicks in with rapid 2-minute updates to keep you and your family safe." |
| **0:18 - 0:24** | Transition to Settings screen showing sync intervals (30m, 1h, 2h, Custom) and the Google Play Privacy & Data Safety card. | **SMART BACKGROUND SYNC**<br>Strict Battery Saver • Zero Tracking | **Voiceover:** "Customize your background sync with strict battery protection and 100% zero-tracking privacy." |
| **0:24 - 0:30** | Phone centers on screen with the 512x512 glowing app icon and Google Play badge. | **ATMOSPHERE**<br>Download Today on Google Play | *Music finishes on a warm, resolved chime.*<br>**Voiceover:** "Atmosphere Weather. Download now on Google Play." |

---

## 4. Google Play Console Data Safety Checklist

When completing the **Data safety** section in Google Play Console:

1. **Does your app collect or share any of the required user data types?**
   - Select **Yes** (for Location data).
2. **Location data breakdown:**
   - **Approximate location**: Collected? **Yes**. Shared with third parties? **No**. Ephemeral? **Yes**.
   - **Precise location**: Collected? **Yes** (when permission granted). Shared? **No**. Ephemeral? **Yes**.
   - **Purpose**: App functionality (Local weather forecast lookup).
3. **Is all data encrypted in transit?**
   - Select **Yes** (All API calls use HTTPS / TLS 1.3).
4. **Do you provide a way for users to request that their data is deleted?**
   - Select **Yes** (Users can reset local cache directly in Settings via the "Clear Local Cache & Reset Data" button).
5. **Target Audience & Content:**
   - Target age: **13+ or All ages** (complies with COPPA and Families policy).
