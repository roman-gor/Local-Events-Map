package com.gorman.data.repository.auth

import com.google.firebase.auth.AuthCredential
import com.gorman.domainmodel.UserData

interface IAuthRepository {
    suspend fun signIn(email: String, password: String): Result<UserData>
    suspend fun signIn(idToken: String, credential: AuthCredential): Result<UserData>
    suspend fun signInAnonymously(): Result<UserData>
    suspend fun signUp(email: String, password: String): Result<UserData>
    suspend fun signOut()
}
