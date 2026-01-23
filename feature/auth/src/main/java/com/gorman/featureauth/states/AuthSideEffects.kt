package com.gorman.featureauth.states

sealed interface AuthSideEffects {
    data class ShowToast(val text: String) : AuthSideEffects
    data class OnNavigateToMain(val userUiState: UserUiState) : AuthSideEffects
    object OnNavigateToSignUp : AuthSideEffects
    object OnNavigateToSignIn : AuthSideEffects
}
