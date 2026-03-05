package com.gorman.data.repository.geo

import android.Manifest
import androidx.annotation.RequiresPermission
import com.gorman.cache.data.IPreferencesDataSource
import com.gorman.common.data.LocationProvider
import com.gorman.common.models.CityData
import com.gorman.data.repository.settings.IUserSettingsRepository
import com.gorman.domainmodel.PointDomain
import com.gorman.map.search.IMapSearchManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class GeoRepository @Inject constructor(
    private val locationProvider: LocationProvider,
    private val mapSearchManager: IMapSearchManager,
    private val preferencesDataSource: IPreferencesDataSource,
    private val settingsRepository: IUserSettingsRepository
) : IGeoRepository {

    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ]
    )
    override suspend fun getUserLocation(): Result<PointDomain> =
        locationProvider.getLastKnownLocation()

    override fun getDistanceFromPoints(point1: PointDomain, point2: PointDomain): Int =
        mapSearchManager.calculatingDistance(point1, point2)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSavedCity(): Flow<CityData?> =
        preferencesDataSource.currentUid
            .flatMapLatest { uid ->
                uid?.let {
                    settingsRepository.getCityDataByUserId(it)
                } ?: flowOf(null)
            }

    override suspend fun saveCity(cityData: CityData) {
        val uid = preferencesDataSource.currentUid.firstOrNull()
        uid?.let { settingsRepository.updateCity(userId = uid, city = cityData) }
    }
}
