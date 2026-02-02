package com.gorman.data.repository.geo

import com.gorman.cache.data.DataStoreManager
import com.gorman.common.data.LocationProvider
import com.gorman.common.models.CityData
import com.gorman.data.repository.geo.IGeoRepository
import com.yandex.mapkit.geometry.Geo
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GeoRepository @Inject constructor(
    private val locationProvider: LocationProvider,
    private val dataStoreManager: DataStoreManager
) : IGeoRepository {
    override suspend fun getUserLocation(): Result<Point> =
        locationProvider.getLastKnownLocation()

    override fun getDistanceFromPoints(point1: Point, point2: Point): Int =
        Geo.distance(point1, point2).toInt() / 1000

    override fun getSavedCity(): Flow<CityData?> = dataStoreManager.savedCity

    override suspend fun saveCity(cityData: CityData) {
        dataStoreManager.saveCity(cityData)
    }
}
