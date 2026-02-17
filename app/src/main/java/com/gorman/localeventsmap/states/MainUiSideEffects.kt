package com.gorman.localeventsmap.states

sealed interface MainUiSideEffects {
    data class OnNavigateToDetails(val id: String) : MainUiSideEffects
    object ShowErrorToast : MainUiSideEffects
}
