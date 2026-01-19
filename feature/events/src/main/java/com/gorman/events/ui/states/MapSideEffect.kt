package com.gorman.events.ui.states

import com.yandex.mapkit.geometry.Point

sealed interface MapSideEffect {
    data class MoveCamera(val point: Point, val zoom: Float = 11f) : MapSideEffect
}
