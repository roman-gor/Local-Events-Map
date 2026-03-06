package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gorman.database.data.model.UserFiltersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserFiltersDao {
    @Query("SELECT * FROM userFilters WHERE userId = :userId")
    fun getUserFilters(userId: String): Flow<UserFiltersEntity?>

    @Query("SELECT * FROM userFilters WHERE userId = :userId")
    suspend fun getFiltersOnce(userId: String): UserFiltersEntity?

    @Upsert
    suspend fun saveUserFilters(filters: UserFiltersEntity)
}
