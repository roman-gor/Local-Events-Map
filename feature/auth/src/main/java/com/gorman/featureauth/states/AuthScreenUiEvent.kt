package com.gorman.featureauth.states

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
