package com.gorman.feature.auth.impl.domain

import android.util.Log
import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        val result = authRepository.signIn(email, password)
        return result.fold(
            onSuccess = { user ->
                Log.d("UserAuth", user.uid)
                userRepository.refreshUserData(user.uid)
            },
            onFailure = { e ->
                Log.e("UserRepository", "Sign In Failed: ${e.message}")
                Result.failure(e)
            }
        )
    }
}
