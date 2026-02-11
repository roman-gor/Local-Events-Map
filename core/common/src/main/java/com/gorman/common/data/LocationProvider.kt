package com.gorman.common.data

import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.gorman.domainmodel.PointDomain
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

    @androidx.annotation.RequiresPermission(
        allOf = [
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    suspend fun getLastKnownLocation(): Result<PointDomain> = runCatching {
        val lastLoc = fusedLocationClient.lastLocation.await()
        if (lastLoc != null) Result.success(PointDomain(lastLoc.latitude, lastLoc.longitude))

        val currentLoc = fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).await()
        Log.e(
            "LocationProvider",
            "Coordinates: ${currentLoc.latitude} / ${currentLoc.longitude}"
        )
        val point = currentLoc?.let { PointDomain(lastLoc.latitude, lastLoc.longitude) }
        return if (point != null) {
            Result.success(point)
        } else {
            Result.failure(Exception("Point Location is null"))
        }
    }
}
