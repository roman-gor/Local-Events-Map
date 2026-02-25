package com.gorman.map.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gorman.domainmodel.PointDomain
import com.gorman.map.mapper.toYandex
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

interface MapControl {
    fun moveCamera(point: PointDomain, zoom: Float = 15f)
}

class MapControlImpl : MapControl {
    var mapView: MapView? = null

    override fun moveCamera(point: PointDomain, zoom: Float) {
        mapView?.mapWindow?.map?.move(
            CameraPosition(point.toYandex(), zoom, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }
}

@Composable
fun rememberMapControl(): MapControl {
    return remember { MapControlImpl() }
}
