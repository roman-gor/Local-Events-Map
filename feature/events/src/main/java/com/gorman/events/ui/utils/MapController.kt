package com.gorman.events.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MapController {
    private var mapView: MapView? = null

    fun attach(view: MapView) {
        mapView = view
    }

    fun detach() {
        mapView = null
    }

    fun moveCamera(point: Point, zoom: Float = 11f) {
        mapView?.mapWindow?.map?.move(
            CameraPosition(point, zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }
}

@Composable
fun rememberMapController(): MapController {
    return remember { MapController() }
}
