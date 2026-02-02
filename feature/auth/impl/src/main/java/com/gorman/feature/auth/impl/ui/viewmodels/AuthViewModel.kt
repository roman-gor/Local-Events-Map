package com.gorman.feature.auth.impl.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.user.IUserRepository
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.auth.api.SignUpScreenNavKey
import com.gorman.feature.auth.impl.domain.SignInAnonUserUseCase
import com.gorman.feature.auth.impl.domain.SignInUserUseCase
import com.gorman.feature.auth.impl.domain.SignOutUserUseCase
import com.gorman.feature.auth.impl.domain.SignUpUserUseCase
import com.gorman.feature.auth.impl.ui.states.AuthScreenState
import com.gorman.feature.auth.impl.ui.states.AuthScreenUiEvent
import com.gorman.feature.auth.impl.ui.states.AuthSideEffects
import com.gorman.feature.auth.impl.states.AuthScreenState
import com.gorman.feature.auth.impl.states.AuthScreenUiEvent
import com.gorman.feature.auth.impl.states.AuthSideEffects
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.navigator.Navigator
import com.gorman.ui.mappers.toDomain
import com.gorman.ui.states.UserUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInUserUseCase: SignInUserUseCase,
    private val signUpUserUseCase: SignUpUserUseCase,
    private val signInAnonUserUseCase: SignInAnonUserUseCase,
    private val navigator: Navigator
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<AuthScreenState>(AuthScreenState.Idle(user = UserUiState(), password = ""))
    val uiState = _uiState.asStateFlow()
    private val _sideEffect = Channel<AuthSideEffects>(Channel.Factory.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    private var lastToastTime = 0L

    fun onUiEvent(uiEvent: AuthScreenUiEvent) {
        val currentState = _uiState.value
        when (uiEvent) {
            is AuthScreenUiEvent.OnSignInClick -> signIn(uiEvent.email, uiEvent.password)
            is AuthScreenUiEvent.OnSignUpClick -> signUp(uiEvent.user, uiEvent.password)
            AuthScreenUiEvent.OnGuestSignIn -> guestSignIn()
            AuthScreenUiEvent.OnNavigateToSignInClicked -> navigator.goTo(SignInScreenNavKey)
            AuthScreenUiEvent.OnNavigateToSignUpClicked -> navigator.goTo(SignUpScreenNavKey)
            is AuthScreenUiEvent.ShowToast -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastToastTime >= 3000L) {
                    lastToastTime = currentTime
                    viewModelScope.launch {
                        _sideEffect.send(AuthSideEffects.ShowToast(uiEvent.text))
                    }
                }
            }
            is AuthScreenUiEvent.OnEmailChange -> {
                if (currentState is AuthScreenState.Idle) {
                    _uiState.value = currentState.copy(user = currentState.user.copy(email = uiEvent.email))
                }
            }
            is AuthScreenUiEvent.OnPasswordChange -> {
                if (currentState is AuthScreenState.Idle) {
                    _uiState.value = currentState.copy(password = uiEvent.password)
                }
            }
            is AuthScreenUiEvent.OnUsernameChange -> {
                if (currentState is AuthScreenState.Idle) {
                    _uiState.value = currentState.copy(user = currentState.user.copy(username = uiEvent.username))
                }
            }
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            signInUserUseCase(email, password)
                .onSuccess {
                    navigator.setRoot(HomeScreenNavKey)
                    _uiState.value = AuthScreenState.Success
                }.onFailure { e ->
                    _uiState.value = AuthScreenState.Idle(UserUiState(email = email), password)
                    _sideEffect.send(AuthSideEffects.ShowError(e))
                    Log.d("Auth VM", "Sign In Failed: ${e.message}")
                }
        }
    }

    private fun guestSignIn() {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            signInAnonUserUseCase()
                .onSuccess {
                    navigator.setRoot(HomeScreenNavKey)
                    _uiState.value = AuthScreenState.Success
                    Log.d("Auth VM", "Successfully Sign In")
                }.onFailure { e ->
                    _uiState.value = AuthScreenState.Idle(UserUiState(), "")
                    _sideEffect.send(AuthSideEffects.ShowError(e))
                    Log.d("Auth VM", "Sign In Failed: ${e.message}")
                }
        }
    }

    private fun signUp(user: UserUiState, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            signUpUserUseCase(user.toDomain(), password)
                .onSuccess {
                    navigator.setRoot(HomeScreenNavKey)
                    _uiState.value = AuthScreenState.Success
                }
                .onFailure { e ->
                    _uiState.value = AuthScreenState.Idle(user, password)
                    _sideEffect.send(AuthSideEffects.ShowError(e))
                    Log.d("Auth VM", "Sign Up Failed: ${e.message}")
                }
        }
    }
}
