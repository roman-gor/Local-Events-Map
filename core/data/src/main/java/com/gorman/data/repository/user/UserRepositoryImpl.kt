package com.gorman.data.repository.user

import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

internal class UserRepositoryImpl @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRemoteDataSource: IUserRemoteDataSource,
    private val userDataDao: UserDataDao,
    private val dataStoreManager: DataStoreManager
) : IUserRepository {
    override suspend fun signIn(email: String, password: String): Flow<UserData> = flow {
        val result = authRepository.signIn(email, password)
        result.fold(
            onSuccess = { user ->
                dataStoreManager.saveUserId(user.uid)
                emitAll(getUser(user.uid))
            },
            onFailure = { e ->
                Log.e("UserRepository", "Sign In Failed: ${e.message}")
                throw e
            }
        )
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
        val uid = dataStoreManager.savedUserId.first()
        return try {
            if (uid != null) {
                userRemoteDataSource.updateFavouriteEventsState(uid, mapEventId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("UId is null"))
            }
        } catch (e: IllegalStateException) {
            Result.failure(e)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getUserFavouriteEvents(): Flow<List<String>> {
        return dataStoreManager.savedUserId
            .flatMapLatest { uid ->
                if (uid != null) {
                    userRemoteDataSource.getUserFavouriteEvents(uid)
                } else {
                    flowOf(emptyList())
                }
            }
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

    private suspend fun getUser(uid: String): Flow<UserData> {
        try {
            userRemoteDataSource.getUserFromRemote(uid).collect { remoteUser ->
                remoteUser?.let { userDataDao.saveUser(it.toDomain().toEntity()) }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Network error, using cache", e)
        }
        return userDataDao.getUserById(uid).map { it.toDomain() }
    }
}
