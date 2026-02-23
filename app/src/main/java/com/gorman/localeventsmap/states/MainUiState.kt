package com.gorman.localeventsmap.states

sealed interface MainUiState {
    data class NavigateToEvent(val eventId: String) : MainUiState
    data class ShowToast(val res: Int) : MainUiState
}
