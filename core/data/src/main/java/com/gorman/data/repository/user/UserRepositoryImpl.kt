package com.gorman.data.repository.user

import android.util.Log
import com.google.firebase.auth.FirebaseAuthException
import com.gorman.auth.data.IAuthRepository
import com.gorman.database.data.datasource.dao.UserDataDao
import com.gorman.database.mappers.toEntity
import com.gorman.domainmodel.UserData
import com.gorman.firebase.data.datasource.users.IUserRemoteDataSource
import com.gorman.firebase.mappers.toDomain
import com.gorman.firebase.mappers.toRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRemoteDataSource: IUserRemoteDataSource,
    private val userDataDao: UserDataDao
) : IUserRepository {
    override suspend fun signIn(email: String, password: String): Flow<UserData> = flow {
        val result = authRepository.signIn(email, password)
        result.fold(
            onSuccess = { user ->
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

            Result.success(newUser)
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        authRepository.signOut()
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

    private fun getUser(uid: String): Flow<UserData> {
        return userRemoteDataSource.getUserFromRemote(uid)
            .mapNotNull { it?.toDomain() }
            .onEach { user ->
                userDataDao.saveUser(user.toEntity())
                Log.d("UserRepository", "User cache updated for $uid")
            }
    }
}
