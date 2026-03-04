package com.gorman.data.repository.settings

import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState
import com.gorman.database.data.datasource.dao.UserCitySettingsDao
import com.gorman.database.data.datasource.dao.UserFiltersDao
import com.gorman.database.mappers.toDomain
import com.gorman.database.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserSettingsRepository @Inject constructor(
    private val userFiltersDao: UserFiltersDao,
    private val userCitySettingsDao: UserCitySettingsDao
) : IUserSettingsRepository {
    override fun getFiltersByUserId(userId: String): Flow<FiltersState?> =
        userFiltersDao.getUserFilters(userId).map { it?.toDomain() }

    override fun getCityDataByUserId(userId: String): Flow<CityData?> =
        userCitySettingsDao.getUserCitySettings(userId).map { it?.toDomain() }

    override suspend fun updateCity(userId: String, city: CityData?) {
        val cityToSave = city ?: CityData()

        val entity = cityToSave.toEntity(userId)

        userCitySettingsDao.saveUserCitySettings(entity)
    }

    override suspend fun updateFilters(userId: String, filters: FiltersState?) {
        val filtersToSave = filters ?: FiltersState()

        val entity = filtersToSave.toEntity(userId)

        userFiltersDao.saveUserFilters(entity)
    }
}
