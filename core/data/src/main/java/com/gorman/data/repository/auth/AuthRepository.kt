package com.gorman.data.repository.auth

import com.gorman.auth.data.IAuthenticator
import com.gorman.auth.mappers.toDomain
import com.gorman.domainmodel.UserData
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val authenticator: IAuthenticator
) : IAuthRepository {

    override suspend fun signIn(email: String, password: String): Result<UserData> =
        authenticator.signIn(email, password).map { it.toDomain() }

    override suspend fun signInAnonymously(): Result<UserData> =
        authenticator.signInAnonymously().map { it.toDomain() }

    override suspend fun signUp(email: String, password: String): Result<UserData> =
        authenticator.signUp(email, password).map { it.toDomain() }

    override suspend fun signOut() {
        authenticator.signOut()
    }
}
