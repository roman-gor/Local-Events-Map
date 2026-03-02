package com.gorman.feature.auth.impl.domain

import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.signIn(email, password).mapCatching { user ->
            userRepository.clearUserData()
            userRepository.refreshUserData(user.uid)
            userRepository.saveTokenToUser(user.uid).getOrThrow()
        }
    }
}
