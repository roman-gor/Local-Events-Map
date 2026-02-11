package com.gorman.auth.data

import com.gorman.auth.models.UserAuthModel

interface IAuthenticator {
    suspend fun signIn(email: String, password: String): Result<UserAuthModel>
    suspend fun signUp(email: String, password: String): Result<UserAuthModel>
    suspend fun signInAnonymously(): Result<UserAuthModel>
    suspend fun signOut()
}
