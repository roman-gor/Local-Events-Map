package com.gorman.cache.data

import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState
import com.gorman.data.cache.IPreferencesDataSource
import javax.inject.Inject

internal class PreferencesDataSource @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : IPreferencesDataSource {
    override val savedCity = dataStoreManager.savedCity

    override val lastSyncTimestamp = dataStoreManager.lastSyncTimestamp

    override val savedFilters = dataStoreManager.savedFilters

    override suspend fun saveSyncTimestamp(timestamp: Long) {
        dataStoreManager.saveSyncTimestamp(timestamp)
    }

    override suspend fun saveCity(cityData: CityData) {
        dataStoreManager.saveCity(cityData)
    }

    override suspend fun saveFiltersState(state: FiltersState) {
        dataStoreManager.saveFiltersState(state)
    }
}
