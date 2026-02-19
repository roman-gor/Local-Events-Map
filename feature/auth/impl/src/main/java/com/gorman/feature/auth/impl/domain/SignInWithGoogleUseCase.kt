package com.gorman.feature.auth.impl.domain

import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository
){
    suspend operator fun invoke(): Result<Unit> {

        return authRepository.signInWithGoogle().mapCatching { user ->
            userRepository.clearUserData()
            val existingUser = userRepository.refreshUserData(user.uid)
            if (existingUser.isFailure) {
                userRepository.saveUser(user).getOrThrow()
            }
            userRepository.saveTokenToUser(user.uid).getOrThrow()
        }
    }
}
