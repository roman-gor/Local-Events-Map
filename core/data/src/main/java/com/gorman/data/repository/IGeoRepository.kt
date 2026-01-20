package com.gorman.data.repository

import com.gorman.common.constants.CityCoordinatesConstants
import com.gorman.common.models.CityData
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow

interface IGeoRepository {
    suspend fun getCityByPoint(location: Point): CityData?
    suspend fun getPointByCity(city: CityCoordinatesConstants): CityData?
    suspend fun getUserLocation(): Point?
    fun getSavedCity(): Flow<CityData?>
    suspend fun saveCity(cityData: CityData)
}
