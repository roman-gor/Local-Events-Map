package com.gorman.auth.data

import com.google.firebase.auth.FirebaseUser
import com.gorman.domainmodel.UserData

interface IAuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signInAnonymously(): Result<UserData>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>
    fun signOut()
}
