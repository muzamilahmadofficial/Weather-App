package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.data.model.CityLocation
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class LocationTracker(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fun hasLocationPermission(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineLocation || coarseLocation
    }

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): CityLocation? = withContext(Dispatchers.IO) {
        if (!hasLocationPermission()) {
            return@withContext null
        }

        try {
            val location: Location? = suspendCancellableCoroutine { continuation ->
                val cts = CancellationTokenSource()
                continuation.invokeOnCancellation { cts.cancel() }

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cts.token
                ).addOnSuccessListener { loc ->
                    if (loc != null) {
                        continuation.resume(loc)
                    } else {
                        // Fallback to lastLocation if current location returned null
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                            continuation.resume(lastLoc)
                        }.addOnFailureListener {
                            continuation.resume(null)
                        }
                    }
                }.addOnFailureListener {
                    continuation.resume(null)
                }
            }

            if (location != null) {
                resolveCityFromCoordinates(location.latitude, location.longitude, isCurrentGps = true)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun resolveCityFromCoordinates(
        lat: Double,
        lon: Double,
        isCurrentGps: Boolean = false
    ): CityLocation = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                var result: CityLocation? = null
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val cityName = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "My Location"
                    val countryName = addr.countryName ?: ""
                    val adminArea = addr.adminArea
                    result = CityLocation(
                        name = cityName,
                        country = countryName,
                        stateOrRegion = adminArea,
                        latitude = lat,
                        longitude = lon,
                        isCurrentGpsLocation = isCurrentGps
                    )
                }
                result ?: fallbackCity(lat, lon, isCurrentGps)
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    val cityName = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "My Location"
                    val countryName = addr.countryName ?: ""
                    val adminArea = addr.adminArea
                    CityLocation(
                        name = cityName,
                        country = countryName,
                        stateOrRegion = adminArea,
                        latitude = lat,
                        longitude = lon,
                        isCurrentGpsLocation = isCurrentGps
                    )
                } else {
                    fallbackCity(lat, lon, isCurrentGps)
                }
            }
        } catch (e: Exception) {
            fallbackCity(lat, lon, isCurrentGps)
        }
    }

    private fun fallbackCity(lat: Double, lon: Double, isCurrentGps: Boolean): CityLocation {
        return CityLocation(
            name = "Current Location",
            country = "",
            stateOrRegion = null,
            latitude = lat,
            longitude = lon,
            isCurrentGpsLocation = isCurrentGps
        )
    }

    companion object {
        val DEFAULT_CITY = CityLocation(
            name = "Paris",
            country = "France",
            stateOrRegion = "Île-de-France",
            latitude = 48.8566,
            longitude = 2.3522,
            isCurrentGpsLocation = false
        )

        val CURATED_EXPLORE_CITIES = listOf(
            CityLocation("Paris", "France", "Île-de-France", 48.8566, 2.3522),
            CityLocation("London", "United Kingdom", "England", 51.5074, -0.1278),
            CityLocation("New York", "United States", "New York", 40.7128, -74.0060),
            CityLocation("Islamabad", "Pakistan", "Islamabad Capital Territory", 33.6844, 73.0479)
        )
    }
}
