package com.gorman.feature.auth.impl.domain

import com.google.firebase.auth.GoogleAuthProvider
import com.gorman.auth.data.googleAuth.IAuthClient
import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository,
    private val authClient: IAuthClient
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val idToken = authClient.getToken().getOrThrow()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        authRepository.signIn(idToken, credential).mapCatching { user ->
            val existingUser = userRepository.refreshUserData(user.uid)
            if (existingUser.isFailure) {
                userRepository.saveUser(user).getOrThrow()
            }
            userRepository.saveTokenToUser(user.uid).getOrThrow()
        }
    }
}
