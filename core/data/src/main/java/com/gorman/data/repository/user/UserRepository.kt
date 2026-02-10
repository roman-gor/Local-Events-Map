package com.gorman.data.repository.user

import android.util.Log
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.database.mappers.toDomain
import com.gorman.database.mappers.toEntity
import com.gorman.domainmodel.UserData
import com.gorman.network.data.datasource.users.IUserRemoteDataSource
import com.gorman.network.mappers.toDomain
import com.gorman.network.mappers.toRemote
import com.gorman.notifications.notificator.INotificationTokenDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserRepository @Inject constructor(
    private val userRemoteDataSource: IUserRemoteDataSource,
    private val notificationTokenDataSource: INotificationTokenDataSource,
    private val userDataDao: UserDataDao
) : IUserRepository {

    @Suppress("TooGenericExceptionThrown")
    override suspend fun refreshUserData(uid: String): Result<Unit> = runCatching {
        val remoteUser = userRemoteDataSource.getUserFromRemote(uid).firstOrNull()
        remoteUser?.let {
            userDataDao.saveUser(it.toDomain().toEntity())
            Log.d("UserRepository", "User synced from remote to local DB")
        } ?: throw Exception("User not found on server")
    }

    override suspend fun clearUserData() {
        userDataDao.clearAll()
    }

    override suspend fun getUserData(): Flow<UserData?> =
        userDataDao.getUser().map { it?.toDomain() }

    override suspend fun saveUser(userData: UserData): Result<Unit> {
        return userRemoteDataSource.saveUserToRemote(userData.toRemote())
            .map { userDataDao.saveUser(userData.toEntity()) }
            .onFailure { Log.e("UserRepository", "Remote save failed", it) }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun saveTokenToUser(uid: String): Result<Unit> = runCatching {
        userRemoteDataSource.saveTokenToUser(uid, notificationTokenDataSource.getNotificationToken())
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun saveTokenToUser(uid: String, token: String): Result<Unit> = runCatching {
        userRemoteDataSource.saveTokenToUser(uid, token)
    }
}
