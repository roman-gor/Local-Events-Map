package com.gorman.data.repository.geo

import com.gorman.cache.data.DataStoreManager
import com.gorman.common.data.LocationProvider
import com.gorman.common.models.CityData
import com.gorman.domainmodel.PointDomain
import com.gorman.map.search.IMapSearchManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GeoRepository @Inject constructor(
    private val locationProvider: LocationProvider,
    private val mapSearchManager: IMapSearchManager,
    private val dataStoreManager: DataStoreManager
) : IGeoRepository {
    override suspend fun getUserLocation(): Result<PointDomain> =
        locationProvider.getLastKnownLocation()

    override fun getDistanceFromPoints(point1: PointDomain, point2: PointDomain): Int =
        mapSearchManager.calculatingDistance(point1, point2)

    override fun getSavedCity(): Flow<CityData?> = dataStoreManager.savedCity

    override suspend fun saveCity(cityData: CityData) {
        dataStoreManager.saveCity(cityData)
    }
}
