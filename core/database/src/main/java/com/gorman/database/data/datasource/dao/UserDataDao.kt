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
    suspend fun updateUser(userDataEntity: UserDataEntity)

    @Query(
        """
        UPDATE users 
        SET isActive = (CASE WHEN uid = :newUid THEN 1 ELSE 0 END)
    """
    )
    suspend fun setActiveUserAtomic(newUid: String)

    @Transaction
    suspend fun saveUser(newUid: String, userDataEntity: UserDataEntity) {
        updateUser(userDataEntity)
        setActiveUserAtomic(newUid)
    }

    @Query("UPDATE users SET isActive = 0")
    suspend fun setAllUsersInactive()
}
