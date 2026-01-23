package com.gorman.firebase.data.datasource.users

import com.gorman.firebase.data.models.UserDataRemote
import kotlinx.coroutines.flow.Flow

interface IUserRemoteDataSource {
    fun saveUserToRemote(user: UserDataRemote): Flow<Result<Unit>>
    fun getUserFromRemote(uuid: String): Flow<UserDataRemote?>
}
