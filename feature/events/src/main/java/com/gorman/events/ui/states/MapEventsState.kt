package com.gorman.events.ui.states

import kotlinx.collections.immutable.ImmutableList

sealed class MapEventsState {
    object Idle : MapEventsState()
    object Loading : MapEventsState()
    data class Error(val throwable: Throwable) : MapEventsState()
    data class Success(val eventsList: ImmutableList<MapUiEvent>) : MapEventsState()
}
