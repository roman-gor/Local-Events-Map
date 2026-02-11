package com.gorman.feature.auth.impl.domain

import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import javax.inject.Inject

class SignInAnonUserUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signInAnonymously().fold(
            onSuccess = {
                userRepository.saveUser(it)
                Result.success(Unit)
            },
            onFailure = { e ->
                Result.failure(Exception(e))
            }
        )
    }
}
