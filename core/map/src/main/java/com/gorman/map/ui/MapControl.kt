package com.gorman.map.ui

import com.gorman.domainmodel.PointDomain

interface MapControl {
    fun moveCamera(point: PointDomain, zoom: Float = 15f)
}
