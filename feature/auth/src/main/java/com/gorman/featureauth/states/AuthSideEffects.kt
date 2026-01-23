package com.gorman.featureauth.states

sealed interface AuthSideEffects {
    data class ShowToast(val text: String) : AuthSideEffects
}
