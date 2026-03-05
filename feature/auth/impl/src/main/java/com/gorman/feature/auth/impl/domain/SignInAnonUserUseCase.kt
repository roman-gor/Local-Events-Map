package com.gorman.feature.auth.impl.domain

import com.gorman.cache.data.IPreferencesDataSource
import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import javax.inject.Inject

class SignInAnonUserUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository,
    private val preferencesDataSource: IPreferencesDataSource
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signInAnonymously().mapCatching {
            preferencesDataSource.saveCurrentUid(it.uid)
            userRepository.saveUser(it)
            userRepository.saveTokenToUser(it.uid).getOrThrow()
        }
    }
}
