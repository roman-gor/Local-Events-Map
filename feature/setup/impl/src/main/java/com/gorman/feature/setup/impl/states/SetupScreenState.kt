package com.gorman.feature.setup.impl.states

sealed interface SetupScreenState {
    object Loading : SetupScreenState
    data class Error(val e: Throwable) : SetupScreenState
}

sealed interface SetupScreenUiEvent {
    object TryAgain : SetupScreenUiEvent
}
