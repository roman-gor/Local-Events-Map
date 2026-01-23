package com.gorman.featureauth.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.user.IUserRepository
import com.gorman.featureauth.mappers.toDomain
import com.gorman.featureauth.states.AuthScreenUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    fun onUiEvent(uiEvent: AuthScreenUiEvent) {
        when (uiEvent) {
            is AuthScreenUiEvent.OnSignInClick -> {
                viewModelScope.launch {
                    userRepository.signIn(uiEvent.email, uiEvent.password)
                }
            }
            is AuthScreenUiEvent.OnSignUpClick -> {
                viewModelScope.launch {
                    userRepository.signUp(uiEvent.user.toDomain(), uiEvent.password)
                }
            }
        }
    }
}
