package com.gorman.feature.events.impl.ui.states

import com.gorman.ui.states.MapUiEvent

sealed interface ScreenSideEffect {
    data class MoveCamera(val point: PointUiState, val zoom: Float = 11f) : ScreenSideEffect
    data class ShowToast(val text: String) : ScreenSideEffect
    data class OnNavigateToDetailsScreen(val event: MapUiEvent) : ScreenSideEffect
}
