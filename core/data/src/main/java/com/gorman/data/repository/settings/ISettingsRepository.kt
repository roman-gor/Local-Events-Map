package com.gorman.data.repository.settings

import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {
    fun getFiltersByUserId(userId: String): Flow<FiltersState?>
    fun getCityDataByUserId(userId: String): Flow<CityData?>
    suspend fun updateCity(userId: String, city: CityData?)
    suspend fun updateFilters(userId: String, filters: FiltersState?)
}
