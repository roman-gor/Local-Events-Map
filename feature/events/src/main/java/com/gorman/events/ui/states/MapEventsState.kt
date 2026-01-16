package com.gorman.events.ui.states

sealed class MapEventsState {
    object Idle : MapEventsState()
    object Loading : MapEventsState()
    data class Error(val throwable: Throwable) : MapEventsState()
    data class Success(val eventsList: List<MapUiEvent>) : MapEventsState()
}
