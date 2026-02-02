package com.gorman.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.gorman.auth.mappers.toAuthModel
import com.gorman.auth.models.UserAuthModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class Authenticator @Inject constructor(
    private val provider: FirebaseAuth
) : IAuthenticator {
    @Suppress("TooGenericExceptionCaught")
    override suspend fun signIn(email: String, password: String): Result<UserAuthModel> {
        return try {
            val result = provider.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user.toAuthModel())
            } else {
                Result.failure(Exception("Sign in failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun signUp(email: String, password: String): Result<UserAuthModel> {
        return try {
            val result = provider.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user.toAuthModel())
            } else {
                Result.failure(Exception("User creation failed: User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun signInAnonymously(): Result<UserAuthModel> {
        return try {
            val authResult = provider.signInAnonymously().await()
            val user = authResult.user
            if (user != null) {
                Result.success(user.toAuthModel())
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        provider.signOut()
    }
}
