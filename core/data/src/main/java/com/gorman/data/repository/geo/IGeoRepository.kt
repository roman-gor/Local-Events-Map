package com.gorman.data.repository.geo

import com.gorman.common.models.CityData
import com.gorman.domainmodel.PointDomain
import kotlinx.coroutines.flow.Flow

interface IGeoRepository {
    suspend fun getUserLocation(): Result<PointDomain>
    fun getDistanceFromPoints(point1: PointDomain, point2: PointDomain): Int
    fun getSavedCity(): Flow<CityData?>
    suspend fun saveCity(cityData: CityData)
}
