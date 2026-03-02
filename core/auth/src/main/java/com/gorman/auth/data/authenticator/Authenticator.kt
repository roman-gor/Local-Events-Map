package com.gorman.auth.data.authenticator

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.gorman.auth.mappers.toAuthModel
import com.gorman.auth.models.UserAuthModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class Authenticator @Inject constructor(
    private val provider: FirebaseAuth
) : IAuthenticator {
    override suspend fun signIn(email: String, password: String): Result<UserAuthModel> = runCatching {
        val result = provider.signInWithEmailAndPassword(email, password).await()
        result.user?.toAuthModel() ?: error(Exception("Sign in failed: User is null"))
    }

    override suspend fun signIn(idToken: String, credential: AuthCredential): Result<UserAuthModel> = runCatching {
        val result = provider.signInWithCredential(credential).await()
        result.user?.toAuthModel() ?: error(Exception("Google Sign-In failed: User is null"))
    }

    override suspend fun signUp(email: String, password: String): Result<UserAuthModel> = runCatching {
        val result = provider.createUserWithEmailAndPassword(email, password).await()
        result.user?.toAuthModel() ?: error(Exception("User creation failed: User is null"))
    }

    override suspend fun signInAnonymously(): Result<UserAuthModel> = runCatching {
        val authResult = provider.signInAnonymously().await()
        authResult.user?.toAuthModel() ?: error(Exception("User is null"))
    }

    override suspend fun signOut() {
        provider.signOut()
    }
}
