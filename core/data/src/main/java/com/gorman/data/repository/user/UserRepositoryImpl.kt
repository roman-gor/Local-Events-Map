package com.gorman.data.repository.user

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.gorman.auth.data.IAuthRepository
import com.gorman.cache.data.DataStoreManager
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.database.mappers.toDomain
import com.gorman.database.mappers.toEntity
import com.gorman.domainmodel.UserData
import com.gorman.firebase.data.datasource.users.IUserRemoteDataSource
import com.gorman.firebase.mappers.toDomain
import com.gorman.firebase.mappers.toRemote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRemoteDataSource: IUserRemoteDataSource,
    private val userDataDao: UserDataDao,
    private val dataStoreManager: DataStoreManager
) : IUserRepository {
    override suspend fun signIn(email: String, password: String): Flow<UserData> {
        val result = authRepository.signIn(email, password)
        return result.fold(
            onSuccess = { user ->
                Log.d("UserAuth", user.uid)
                dataStoreManager.saveUserId(user.uid)
                getUserFromRemote(user.uid)
            },
            onFailure = { e ->
                Log.e("UserRepository", "Sign In Failed: ${e.message}")
                throw e
            }
        )
    }

    private suspend fun getUserFromRemote(uid: String): Flow<UserData> {
        try {
            val remoteUser = userRemoteDataSource.getUserFromRemote(uid).firstOrNull()
            remoteUser?.let {
                userDataDao.saveUser(it.toDomain().toEntity())
                Log.d("UserRepository", "User synced from remote to local DB")
            }
        } catch (e: FirebaseException) {
            Log.e("UserRepository", "Network error during sync", e)
        }
        return userDataDao.getUserById(uid).map { it.toDomain() }
    }

    override suspend fun signUp(userData: UserData, password: String): Result<UserData> {
        return try {
            val email = userData.email ?: error("Email is null")
            val authResult = authRepository.signUp(email, password).getOrThrow()
            val uid = authResult.uid

            val newUser = userData.copy(uid = uid)

            saveUser(newUser)

            dataStoreManager.saveUserId(uid)

            Result.success(newUser)
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        authRepository.signOut()
        dataStoreManager.saveUserId("")
        userDataDao.clearAll()
    }

    override suspend fun updateFavouriteEventsState(mapEventId: String): Result<Unit> {
        val uid = dataStoreManager.savedUserId.first() ?: return Result.failure(Exception("No UID"))
        Log.d("Data", "$uid/$mapEventId")
        return try {
            val currentUser = userDataDao.getUserById(uid).first()
            val currentList = currentUser.favouriteEventsIds.toMutableList()

            val isAdding = !currentList.contains(mapEventId)
            if (isAdding) currentList.add(mapEventId) else currentList.remove(mapEventId)

            val updatedUser = currentUser.copy(favouriteEventsIds = currentList)
            userDataDao.saveUser(updatedUser)

            userRemoteDataSource.updateFavouriteEventsState(uid, mapEventId)
            Result.success(Unit)
        } catch (e: IllegalStateException) {
            Log.e("UserRepository", "Failed to sync with remote", e)
            Result.success(Unit)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getUserFavouriteEvents(): Flow<List<String>> {
        return dataStoreManager.savedUserId
            .flatMapLatest { uid ->
                if (uid != null) {
                    userDataDao.getUserById(uid).map { entity ->
                        entity.favouriteEventsIds
                    }
                } else {
                    flowOf(emptyList())
                }
            }
    }

    override suspend fun getUserData(): Flow<UserData> {
        val uid = dataStoreManager.savedUserId.first() ?: error("No UID")
        return userDataDao.getUserById(uid).map { it.toDomain() }
    }

    private suspend fun saveUser(userData: UserData) {
        userRemoteDataSource.saveUserToRemote(userData.toRemote())
            .first()
            .onSuccess {
                userDataDao.saveUser(userData.toEntity())
            }
            .onFailure { e ->
                Log.e("UserRepository", "Remote save failed", e)
                throw e
            }
    }
}
