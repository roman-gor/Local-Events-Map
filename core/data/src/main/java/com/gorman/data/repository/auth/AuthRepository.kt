package com.gorman.data.repository.auth

import com.gorman.auth.data.IAuthenticator
import com.gorman.auth.mappers.toDomain
import com.gorman.domainmodel.UserData
import com.gorman.data.repository.auth.IAuthRepository
import javax.inject.Inject

internal class AuthRepository @Inject constructor(
    private val firebaseAuthenticator: IAuthenticator
) : IAuthRepository {

    override suspend fun signIn(email: String, password: String): Result<UserData> =
        firebaseAuthenticator.signIn(email, password).map { it.toDomain() }

    override suspend fun signInAnonymously(): Result<UserData> =
        firebaseAuthenticator.signInAnonymously().map { it.toDomain() }

    override suspend fun signUp(email: String, password: String): Result<UserData> =
        firebaseAuthenticator.signUp(email, password).map { it.toDomain() }

    override suspend fun signOut() {
        firebaseAuthenticator.signOut()    }
}
