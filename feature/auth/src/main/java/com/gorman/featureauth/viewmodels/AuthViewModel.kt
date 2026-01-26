package com.gorman.featureauth.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.user.IUserRepository
import com.gorman.featureauth.mappers.toDomain
import com.gorman.featureauth.mappers.toUiState
import com.gorman.featureauth.states.AuthScreenState
import com.gorman.featureauth.states.AuthScreenUiEvent
import com.gorman.featureauth.states.AuthSideEffects
import com.gorman.featureauth.states.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthScreenState>(AuthScreenState.Idle)
    val uiState = _uiState.asStateFlow()
    private val _sideEffect = Channel<AuthSideEffects>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onUiEvent(uiEvent: AuthScreenUiEvent) {
        when (uiEvent) {
            is AuthScreenUiEvent.OnSignInClick -> signIn(uiEvent.email, uiEvent.password)
            is AuthScreenUiEvent.OnSignUpClick -> signUp(uiEvent.user, uiEvent.password)
            AuthScreenUiEvent.OnGuestSignIn -> guestSignIn()
            AuthScreenUiEvent.OnNavigateToSignInClicked -> {
                viewModelScope.launch {
                    _sideEffect.send(AuthSideEffects.OnNavigateToSignIn)
                }
            }
            AuthScreenUiEvent.OnNavigateToSignUpClicked -> {
                viewModelScope.launch {
                    _sideEffect.send(AuthSideEffects.OnNavigateToSignUp)
                }
            }
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            try {
                val user = userRepository.signIn(email, password).first().toUiState()
                _sideEffect.send(AuthSideEffects.OnNavigateToMain(user))
                _uiState.value = AuthScreenState.Success
                Log.d("Auth VM", "Successfully Sign In")
            } catch (e: IllegalStateException) {
                _uiState.value = AuthScreenState.Error(e)
                Log.d("Auth VM", "Sign In Failed: ${e.message}")
            }
        }
    }

    private fun guestSignIn() {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            try {
                userRepository.signInAnonymously().fold(
                    onSuccess = {
                        _sideEffect.send(AuthSideEffects.OnNavigateToMain(it.toUiState()))
                        _uiState.value = AuthScreenState.Success
                        Log.d("Auth VM", "Successfully Sign In")
                    },
                    onFailure = { e ->
                        _uiState.value = AuthScreenState.Error(e)
                        Log.d("AuthViewModel", "Guest Sign In Failed: ${e.message}")
                    }
                )
            } catch (e: IllegalStateException) {
                _uiState.value = AuthScreenState.Error(e)
                Log.d("Auth VM", "Sign In Failed: ${e.message}")
            }
        }
    }

    private fun signUp(user: UserUiState, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            userRepository.signUp(user.toDomain(), password)
                .onSuccess {
                    _sideEffect.send(AuthSideEffects.OnNavigateToMain(it.toUiState()))
                    _uiState.value = AuthScreenState.Success
                }
                .onFailure { e ->
                    _uiState.value = AuthScreenState.Error(e)
                    _sideEffect.send(AuthSideEffects.ShowToast(e.localizedMessage ?: "Error"))
                }
        }
    }
}
