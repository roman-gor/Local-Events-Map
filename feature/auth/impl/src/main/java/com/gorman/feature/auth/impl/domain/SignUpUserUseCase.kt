package com.gorman.feature.auth.impl.domain

import com.gorman.data.repository.auth.IAuthRepository
import com.gorman.data.repository.user.IUserRepository
import com.gorman.domainmodel.UserData
import javax.inject.Inject

class SignUpUserUseCase @Inject constructor(
    private val authRepository: IAuthRepository,
    private val userRepository: IUserRepository
) {
    suspend operator fun invoke(userData: UserData, password: String): Result<Unit> {
        val email = userData.email ?: error("Email is null")
        return authRepository.signUp(email, password).mapCatching { authResult ->
            userRepository.clearUserData()
            userRepository.saveUser(userData.copy(uid = authResult.uid))
        }
    }
}
