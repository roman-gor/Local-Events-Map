package com.gorman.map.mapmanager

import com.yandex.mapkit.MapKit
import javax.inject.Inject

class MapManager @Inject constructor(
    private val mapKit: MapKit
) : IMapManager {
    override fun onStart() { mapKit.onStart() }

    override fun onStop() { mapKit.onStop() }
}
