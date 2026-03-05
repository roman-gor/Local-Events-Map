package com.gorman.feature.bookmarks.impl.domain

import com.gorman.cache.data.IPreferencesDataSource
import com.gorman.data.repository.auth.IAuthRepository
import javax.inject.Inject

class SignOutUserUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val preferencesDataSource: IPreferencesDataSource
) {
    suspend operator fun invoke() {
        authRepository.signOut()
        preferencesDataSource.saveCurrentUid("")
    }
}
