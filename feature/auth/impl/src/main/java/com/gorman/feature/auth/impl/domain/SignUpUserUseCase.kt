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
        return authRepository.signUp(email, password).fold(
            onSuccess = { authResult ->
                val uid = authResult.uid

                val newUser = userData.copy(uid = uid)

                val saveResult = userRepository.saveUser(newUser)

                if (saveResult.isFailure) {
                    return Result.failure(saveResult.exceptionOrNull() ?: Exception("Save failed"))
                }
                Result.success(Unit)
            },
            onFailure = { e ->
                Result.failure(e)
            }
        )
    }
}
