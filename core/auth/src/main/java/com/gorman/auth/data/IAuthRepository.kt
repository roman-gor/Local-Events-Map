package com.gorman.auth.data

import com.google.firebase.auth.FirebaseUser

interface IAuthRepository {
    val currentUser: FirebaseUser?
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signUp(email: String, password: String): Result<FirebaseUser>
    fun signOut()
}
