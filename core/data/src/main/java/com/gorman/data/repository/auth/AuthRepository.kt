package com.gorman.data.repository.auth

import com.gorman.auth.data.authenticator.IAuthenticator
import com.gorman.auth.data.googleAuth.IGoogleAuthClient
import com.gorman.auth.mappers.toDomain
import com.gorman.domainmodel.UserData
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val authenticator: IAuthenticator,
    private val googleAuthClient: IGoogleAuthClient
) : IAuthRepository {

    override suspend fun signIn(email: String, password: String): Result<UserData> =
        authenticator.signIn(email, password).map { it.toDomain() }

    override suspend fun signInWithGoogle(): Result<UserData> {
        return googleAuthClient.getIdToken().mapCatching { idToken ->
            authenticator.signInWithGoogle(idToken).map { it.toDomain() }.getOrThrow()
        }
    }

    override suspend fun signInAnonymously(): Result<UserData> =
        authenticator.signInAnonymously().map { it.toDomain() }

    override suspend fun signUp(email: String, password: String): Result<UserData> =
        authenticator.signUp(email, password).map { it.toDomain() }

    override suspend fun signOut() {
        authenticator.signOut()
    }
}
