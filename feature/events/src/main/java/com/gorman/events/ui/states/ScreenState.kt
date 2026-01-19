package com.gorman.events.ui.states

import kotlinx.collections.immutable.ImmutableList

sealed class ScreenState {
    object Idle : ScreenState()
    object Loading : ScreenState()
    data class Error(val throwable: Throwable) : ScreenState()
    data class Success(val eventsList: ImmutableList<MapUiEvent>) : ScreenState()
}
