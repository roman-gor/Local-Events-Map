package com.gorman.localeventsmap.states

sealed interface MainUiSideEffects {
    data class NavigateToEvent(val eventId: String) : MainUiSideEffects
    data class ShowToast(val res: Int) : MainUiSideEffects
}
