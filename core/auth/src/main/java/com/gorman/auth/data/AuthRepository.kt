package com.gorman.auth.data

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.gorman.domainmodel.UserData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : IAuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in failed: User is null"))
            }
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(e)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            Result.failure(e)
        }
    }

    override suspend fun signInAnonymously(): Result<UserData> {
        return try {
            val authResult = firebaseAuth.signInAnonymously().await()
            val user = authResult.user
            if (user != null) {
                val domainUser = UserData(
                    uid = user.uid,
                    email = "",
                    username = "guest"
                )
                Result.success(domainUser)
            } else {
                Result.failure(Exception("User is null"))
            }
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(e)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User creation failed: User is null"))
            }
        } catch (e: FirebaseAuthException) {
            Result.failure(e)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(e)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(e)
        } catch (e: FirebaseNetworkException) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
