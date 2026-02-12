package com.gorman.map.ui

import androidx.annotation.DrawableRes
import com.gorman.domainmodel.PointDomain

data class MapConfig(
    val isDarkMode: Boolean,
    val userLocation: PointDomain? = null,
    @param:DrawableRes val userLocationIconRes: Int? = null,
    val initialPosition: PointDomain? = null,
    val initialZoom: Float? = null
)
