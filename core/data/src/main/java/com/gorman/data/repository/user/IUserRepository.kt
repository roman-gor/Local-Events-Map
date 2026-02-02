package com.gorman.data.repository.user

import com.gorman.domainmodel.UserData
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signInAnonymously(): Result<Unit>
    suspend fun signUp(userData: UserData, password: String): Result<Unit>
    suspend fun signOut()
    suspend fun updateFavouriteEventsState(mapEventId: String): Result<Unit>
    fun getUserFavouriteEvents(): Flow<List<String>>
    suspend fun getUserData(): Flow<UserData>
    suspend fun saveTokenToUser(uid: String): Result<Unit>
    suspend fun saveTokenToUser(uid: String, token: String): Result<Unit>
}
