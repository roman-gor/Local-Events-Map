package com.gorman.events.ui.states

import com.gorman.domainmodel.MapEvent

sealed class MapEventsState {
    object Idle : MapEventsState()
    object Loading : MapEventsState()
    data class Error(val throwable: Throwable) : MapEventsState()
    data class Success(val eventsList: List<MapEvent>) : MapEventsState()
}
