package com.gorman.feature.setup.impl.states

sealed interface SetupScreenState {
    object Loading : SetupScreenState
    data class Error(val e: Throwable) : SetupScreenState
    data class Success(val isIdExists: Boolean) : SetupScreenState
}

sealed interface SetupScreenUiEvent {
    object NavigateToSignIn : SetupScreenUiEvent
    object NavigateToMain : SetupScreenUiEvent
    object TryAgain : SetupScreenUiEvent
}
