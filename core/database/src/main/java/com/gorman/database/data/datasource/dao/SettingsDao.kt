package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gorman.common.models.CityData
import com.gorman.common.models.FiltersState
import com.gorman.database.data.model.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT filtersState FROM settings WHERE userId = :userId")
    fun getFiltersByUserId(userId: String): Flow<FiltersState?>

    @Query("SELECT cityData FROM settings WHERE userId = :userId")
    fun getCityDataByUserId(userId: String): Flow<CityData?>

    @Query("SELECT * FROM settings WHERE userId = :userId")
    suspend fun getSettingsSnapshot(userId: String?): SettingsEntity?

    @Upsert
    suspend fun saveSettings(settingsEntity: SettingsEntity)
}
