package com.gorman.feature.auth.impl.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorman.data.repository.user.IUserRepository
import com.gorman.feature.auth.api.SignInScreenNavKey
import com.gorman.feature.auth.api.SignUpScreenNavKey
import com.gorman.feature.auth.impl.mappers.toDomain
import com.gorman.feature.auth.impl.states.AuthScreenState
import com.gorman.feature.auth.impl.states.AuthScreenUiEvent
import com.gorman.feature.auth.impl.states.AuthSideEffects
import com.gorman.feature.auth.impl.states.UserUiState
import com.gorman.feature.events.api.HomeScreenNavKey
import com.gorman.navigation.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val navigator: Navigator
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
            AuthScreenUiEvent.OnNavigateToSignInClicked -> navigator.goTo(SignInScreenNavKey)
            AuthScreenUiEvent.OnNavigateToSignUpClicked -> navigator.goTo(SignUpScreenNavKey)
        }
    }

    private fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            userRepository.signIn(email, password)
                .onSuccess {
                    navigator.setRoot(HomeScreenNavKey)
                    _uiState.value = AuthScreenState.Success
                }.onFailure { e ->
                    _uiState.value = AuthScreenState.Error(e)
                    Log.d("Auth VM", "Sign In Failed: ${e.message}")
                }
        }
    }

    private fun guestSignIn() {
        viewModelScope.launch {
            _uiState.value = AuthScreenState.Loading
            userRepository.signInAnonymously()
                .onSuccess {
                    navigator.setRoot(HomeScreenNavKey)
                    _uiState.value = AuthScreenState.Success
                    Log.d("Auth VM", "Successfully Sign In")
                }.onFailure { e ->
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
                    navigator.setRoot(HomeScreenNavKey)
                    _uiState.value = AuthScreenState.Success
                }
                .onFailure { e ->
                    _uiState.value = AuthScreenState.Error(e)
                    _sideEffect.send(AuthSideEffects.ShowToast(e.localizedMessage ?: "Error"))
                }
        }
    }
}
