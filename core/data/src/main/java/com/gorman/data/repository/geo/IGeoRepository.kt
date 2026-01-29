package com.gorman.data.repository.geo

import com.gorman.common.models.CityData
import com.yandex.mapkit.geometry.Point
import kotlinx.coroutines.flow.Flow

interface IGeoRepository {
    suspend fun getUserLocation(): Result<Point>
    fun getDistanceFromPoints(point1: Point, point2: Point): Int
    fun getSavedCity(): Flow<CityData?>
    suspend fun saveCity(cityData: CityData)
}
