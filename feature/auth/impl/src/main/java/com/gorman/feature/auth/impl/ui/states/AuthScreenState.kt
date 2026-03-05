package com.gorman.feature.auth.impl.ui.states

import com.gorman.ui.states.UserUiState

sealed interface AuthScreenState {
    data class Idle(val user: UserUiState, val password: String) : AuthScreenState
    object Loading : AuthScreenState
}

sealed interface AuthScreenUiEvent {
    data class OnSignUpClick(val user: UserUiState, val password: String) : AuthScreenUiEvent
    data class OnSignInClick(val email: String, val password: String) : AuthScreenUiEvent
    object OnGuestSignIn : AuthScreenUiEvent
    object OnNavigateToSignUpClicked : AuthScreenUiEvent
    object OnNavigateToSignInClicked : AuthScreenUiEvent
    data class ShowToast(val text: String) : AuthScreenUiEvent
    data class OnEmailChange(val email: String) : AuthScreenUiEvent
    data class OnPasswordChange(val password: String) : AuthScreenUiEvent
    data class OnUsernameChange(val username: String) : AuthScreenUiEvent
}
