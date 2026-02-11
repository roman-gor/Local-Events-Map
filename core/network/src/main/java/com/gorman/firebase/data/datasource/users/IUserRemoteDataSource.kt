package com.gorman.firebase.data.datasource.users

import com.gorman.firebase.data.models.UserDataRemote
import kotlinx.coroutines.flow.Flow

interface IUserRemoteDataSource {
    suspend fun saveUserToRemote(user: UserDataRemote): Result<Unit>
    fun getUserFromRemote(uid: String): Flow<UserDataRemote?>
    suspend fun updateFavouriteEventsState(uid: String, mapEventId: String)
    fun getUserFavouriteEvents(uid: String): Flow<List<String>>
}
