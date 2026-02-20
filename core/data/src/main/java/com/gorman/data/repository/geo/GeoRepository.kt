package com.gorman.data.repository.geo

import com.gorman.common.data.LocationProvider
import com.gorman.common.models.CityData
import com.gorman.data.cache.IPreferencesDataSource
import com.gorman.domainmodel.PointDomain
import com.gorman.map.search.IMapSearchManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GeoRepository @Inject constructor(
    private val locationProvider: LocationProvider,
    private val mapSearchManager: IMapSearchManager,
    private val cacheRepository: IPreferencesDataSource
) : IGeoRepository {
    override suspend fun getUserLocation(): Result<PointDomain> =
        locationProvider.getLastKnownLocation()

    override fun getDistanceFromPoints(point1: PointDomain, point2: PointDomain): Int =
        mapSearchManager.calculatingDistance(point1, point2)

    override fun getSavedCity(): Flow<CityData?> = cacheRepository.savedCity

    override suspend fun saveCity(cityData: CityData) {
        cacheRepository.saveCity(cityData)
    }
}
