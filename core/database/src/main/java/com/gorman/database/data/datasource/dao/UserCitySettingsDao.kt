package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gorman.database.data.model.UserCitySettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCitySettingsDao {
    @Query("SELECT * FROM userCitySettings WHERE userId = :userId")
    fun getUserCitySettings(userId: String): Flow<UserCitySettingsEntity?>

    @Query("SELECT * FROM userCitySettings WHERE userId = :userId")
    fun getUserCitySettingsOnce(userId: String): UserCitySettingsEntity?

    @Upsert
    suspend fun saveUserCitySettings(citySettings: UserCitySettingsEntity)
}
