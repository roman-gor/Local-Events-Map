package com.gorman.featureauth.states

sealed interface AuthScreenState {
    object Idle : AuthScreenState
    object Loading : AuthScreenState
    data class Error(val e: Throwable) : AuthScreenState
    data class Success(val userData: UserUiState) : AuthScreenState
}

sealed interface AuthScreenUiEvent {
    data class OnSignUpClick(
        val user: UserUiState,
        val password: String
    ) : AuthScreenUiEvent
    data class OnSignInClick(
        val email: String,
        val password: String
    ) : AuthScreenUiEvent
}
