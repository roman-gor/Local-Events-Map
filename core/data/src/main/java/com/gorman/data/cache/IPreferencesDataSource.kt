package com.gorman.data.cache

import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState
import kotlinx.coroutines.flow.Flow

interface IPreferencesDataSource {
    val lastSyncTimestamp: Flow<Long?>
    val savedCity: Flow<CityData?>
    val savedFilters: Flow<FiltersState?>
    suspend fun saveSyncTimestamp(timestamp: Long)
    suspend fun saveCity(cityData: CityData)
    suspend fun saveFiltersState(state: FiltersState)
}
