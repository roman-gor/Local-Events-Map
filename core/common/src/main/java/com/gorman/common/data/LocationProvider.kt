package com.gorman.common.data

import android.content.Context
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.geometry.Point
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getLastKnownLocation(): Result<Point> {
        return try {
            val lastLoc = fusedLocationClient.lastLocation.await()
            if (lastLoc != null) Result.success(Point(lastLoc.latitude, lastLoc.longitude))

            val currentLoc = fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
            Log.e(
                "LocationProvider",
                "Coordinates: ${currentLoc.latitude} / ${currentLoc.longitude}"
            )
            val point = currentLoc?.let { Point(it.latitude, it.longitude) }
            if (point != null) {
                Result.success(point)
            } else {
                Result.failure(Exception("Point Location is null"))
            }
        } catch (e: ApiException) {
            Log.e("LocationProvider", "GMS API error: ${e.statusCode} - ${e.message}")
            Result.failure(e)
        } catch (e: SecurityException) {
            Log.e("LocationProvider", "Permission denied: ${e.message}")
            Result.failure(e)
        }
    }
}
