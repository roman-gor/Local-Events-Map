package com.gorman.feature.auth.impl.domain

import com.gorman.cache.data.IPreferencesDataSource
import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import javax.inject.Inject

class SignInUserUseCase @Inject constructor(
    private val userRepository: IUserRepository,
    private val authRepository: IAuthRepository,
    private val preferencesDataSource: IPreferencesDataSource
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return authRepository.signIn(email, password).mapCatching { user ->
            preferencesDataSource.saveCurrentUid(user.uid)
            userRepository.refreshUserData(user.uid)
            userRepository.saveTokenToUser(user.uid).getOrThrow()
        }
    }
}
