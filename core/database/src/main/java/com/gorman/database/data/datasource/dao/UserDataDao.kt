package com.gorman.database.data.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.gorman.database.data.model.UserDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM users WHERE isActive = 1 LIMIT 1")
    fun getUser(): Flow<UserDataEntity?>

    @Upsert
    suspend fun saveUser(userDataEntity: UserDataEntity)

    @Transaction
    suspend fun switchActiveUser(newUid: String) {
        clearAllActiveStatus()
        setUserIsActive(newUid)
    }

    @Query("UPDATE users SET isActive = 1 WHERE uid = :uid")
    suspend fun setUserIsActive(uid: String)

    @Query("UPDATE users SET isActive = 0")
    suspend fun clearAllActiveStatus()
}
