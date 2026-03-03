package com.gorman.data.repository.settings

import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState
import com.gorman.database.data.datasource.dao.SettingsDao
import com.gorman.database.data.model.SettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao
) : ISettingsRepository {
    override fun getFiltersByUserId(userId: String): Flow<FiltersState?> =
        settingsDao.getFiltersByUserId(userId)

    override fun getCityDataByUserId(userId: String): Flow<CityData?> =
        settingsDao.getCityDataByUserId(userId)

    override suspend fun updateCity(userId: String, city: CityData?) {
        val currentSettings = settingsDao.getSettingsSnapshot(userId)
            ?: SettingsEntity(userId = userId)

        val updated = currentSettings.copy(cityData = city)
        settingsDao.saveSettings(updated)
    }

    override suspend fun updateFilters(userId: String, filters: FiltersState?) {
        val currentSettings = settingsDao.getSettingsSnapshot(userId)
            ?: SettingsEntity(userId = userId)

        val updated = currentSettings.copy(filtersState = filters)
        settingsDao.saveSettings(updated)
    }
}
