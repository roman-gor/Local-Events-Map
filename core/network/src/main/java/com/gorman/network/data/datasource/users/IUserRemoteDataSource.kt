package com.gorman.network.data.datasource.users

import com.gorman.network.data.models.UserDataRemote
import kotlinx.coroutines.flow.Flow

interface IUserRemoteDataSource {
    suspend fun saveUserToRemote(user: UserDataRemote): Result<Unit>
    fun getUserFromRemote(uid: String): Flow<UserDataRemote?>
    suspend fun saveTokenToUser(uid: String, token: String): Result<Unit>
}
