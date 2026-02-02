package com.gorman.data.repository.user

import android.util.Log
import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.messaging.FirebaseMessaging
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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class UserRepository @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRemoteDataSource: IUserRemoteDataSource,
    private val userDataDao: UserDataDao,
    private val dataStoreManager: DataStoreManager
) : IUserRepository {
    override suspend fun signIn(email: String, password: String): Result<Unit> {
        val result = authRepository.signIn(email, password)
        return result.fold(
            onSuccess = { user ->
                Log.d("UserAuth", user.uid)
                dataStoreManager.saveUserId(user.uid)
                saveTokenToUser(user.uid)
                getUserFromRemote(user.uid)
            },
            onFailure = { e ->
                Log.e("UserRepository", "Sign In Failed: ${e.message}")
                Result.failure(e)
            }
        )
    }

    override suspend fun signInAnonymously(): Result<Unit> {
        return try {
            authRepository.signInAnonymously().fold(
                onSuccess = {
                    dataStoreManager.saveUserId(it.uid)
                    userDataDao.saveUser(it.toEntity())
                    saveTokenToUser(it.uid)
                    Result.success(Unit)
                },
                onFailure = { e ->
                    Result.failure(Exception(e))
                }
            )
        } catch (e: FirebaseAuthException) {
            Result.failure(Exception(e))
        } catch (e: IllegalStateException) {
            Result.failure(Exception(e))
        }
    }

    private suspend fun getUserFromRemote(uid: String): Result<Unit> {
        return try {
            val remoteUser = userRemoteDataSource.getUserFromRemote(uid).firstOrNull()
            remoteUser?.let {
                userDataDao.saveUser(it.toDomain().toEntity())
                Log.d("UserRepository", "User synced from remote to local DB")
            }
            Result.success(Unit)
        } catch (e: FirebaseException) {
            Log.e("UserRepository", "Network error during sync", e)
            Result.failure(e)
        } catch (e: FirebaseApiNotAvailableException) {
            Result.failure(e)
        } catch (e: FirebaseTooManyRequestsException) {
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(userData: UserData, password: String): Result<Unit> {
        val email = userData.email ?: error("Email is null")
        return authRepository.signUp(email, password).fold(
            onSuccess = { authResult ->
                val uid = authResult.uid

                val newUser = userData.copy(uid = uid)

                val saveResult = userRemoteDataSource.saveUserToRemote(newUser.toRemote())
                    .map { userDataDao.saveUser(userData.toEntity()) }
                    .onFailure { Log.e("UserRepository", "Remote save failed", it) }

                if (saveResult.isFailure) {
                    return Result.failure(saveResult.exceptionOrNull() ?: Exception("Save failed"))
                }

                dataStoreManager.saveUserId(uid)
                saveTokenToUser(uid)

                Result.success(Unit)
            },
            onFailure = { e ->
                Result.failure(e)
            }
        )
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

    @Suppress("TooGenericExceptionCaught")
    override suspend fun saveTokenToUser(uid: String): Result<Unit> {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            userRemoteDataSource.saveTokenToUser(uid, token)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to fetch or save FCM token", e)
            Result.failure(e)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun saveTokenToUser(uid: String, token: String): Result<Unit> {
        return try {
            userRemoteDataSource.saveTokenToUser(uid, token)
        } catch (e: Exception) {
            Log.e("UserRepository", "Failed to fetch or save FCM token", e)
            Result.failure(e)
        }
    }
}
