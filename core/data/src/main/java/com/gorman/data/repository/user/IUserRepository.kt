package com.gorman.data.repository.user

import com.gorman.domainmodel.UserData
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun signIn(email: String, password: String): Flow<UserData>
    suspend fun signUp(userData: UserData, password: String): Result<UserData>
    suspend fun signOut()
    suspend fun updateFavouriteEventsState(mapEventId: String): Result<Unit>
    fun getUserFavouriteEvents(): Flow<List<String>>
    suspend fun getUserData(): Flow<UserData>
}
