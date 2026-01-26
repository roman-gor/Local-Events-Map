package com.gorman.events.ui.states

import com.gorman.ui.states.MapUiEvent
import com.yandex.mapkit.geometry.Point

sealed interface ScreenSideEffect {
    data class MoveCamera(val point: Point, val zoom: Float = 11f) : ScreenSideEffect
    data class ShowToast(val text: String) : ScreenSideEffect
    data class OnNavigateToDetailsScreen(val event: MapUiEvent) : ScreenSideEffect
}
