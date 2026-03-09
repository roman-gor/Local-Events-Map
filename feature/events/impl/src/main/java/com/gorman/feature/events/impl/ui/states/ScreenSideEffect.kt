package com.gorman.feature.events.impl.ui.states

sealed interface ScreenSideEffect {
    data class MoveCamera(val point: PointUiState, val zoom: Float = 11f) : ScreenSideEffect
    data class ShowToast(val throwable: Throwable) : ScreenSideEffect
}
