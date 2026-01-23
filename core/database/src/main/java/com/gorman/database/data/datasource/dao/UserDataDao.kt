package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gorman.database.data.model.UserDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUserById(uid: String): Flow<UserDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(userDataEntity: UserDataEntity)

    @Query("DELETE FROM events")
    suspend fun clearAll()
}
