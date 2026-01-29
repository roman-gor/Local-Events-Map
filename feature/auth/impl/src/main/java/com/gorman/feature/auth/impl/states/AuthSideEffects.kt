package com.gorman.feature.auth.impl.states

sealed interface AuthSideEffects {
    data class ShowToast(val text: String) : AuthSideEffects
}
