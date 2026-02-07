package com.gorman.data.repository.user

import com.gorman.domainmodel.UserData
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun refreshUserData(uid: String): Result<Unit>
    suspend fun getUserData(): Flow<UserData?>
    suspend fun saveUser(userData: UserData): Result<Unit>
    suspend fun clearUserData()
    suspend fun saveTokenToUser(uid: String): Result<Unit>
    suspend fun saveTokenToUser(uid: String, token: String): Result<Unit>
}
